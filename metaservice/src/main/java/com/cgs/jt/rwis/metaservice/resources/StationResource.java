package com.cgs.jt.rwis.metaservice.resources;

import com.cgs.jt.rwis.metaservice.api.StationDTO;
import com.cgs.jt.rwis.metaservice.core.StationService;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("stations")
public class StationResource {

    @Inject
    private StationService stationService;

    // CREATE
    @Operation(summary = "Create station",
            description = "Create a new station",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Station created", headers = {@Header(schema = @Schema(type = "string"), name = "Location", description = "URL to a created station")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @POST
    @UnitOfWork
    public Response createStation(@RequestBody(description = "New station model object", required = true, content = @Content(schema = @Schema(implementation = StationDTO.class))) @Valid StationDTO stationDTO) {
        StationDTO createdStation = stationService.createStation(stationDTO);

        return Response
                .ok(createdStation)
                .status(Response.Status.CREATED)
                .header("Location", "/stations/" + createdStation.getId().toString())
                .build();
    }

    // READ
    @Operation(summary = "Get stations",
            description = "Get all the stations",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StationDTO.class)),
                            description = "List of all the stations", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned stations")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    public Response getStations() {
        List<StationDTO> stationDTOList = stationService.getStations();

        return Response
                .ok(stationDTOList)
                .header("X-Total-Count", stationDTOList.size())
                .build();
    }

    @Operation(summary = "Get station by ID",
            description = "Get station by ID",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StationDTO.class)), description = "Station with appropriate ID"),
                    @ApiResponse(responseCode = "404", description = "Station not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{id}")
    public Response getStation(@Parameter(description = "ID of the station to be returned", schema = @Schema(type = "integer", description = "ID of the station to be returned"), required = true) @PathParam("id") int id) {
        StationDTO stationDTO = stationService.getStation(id);
        return Response.ok(stationDTO).build();
    }


    // UPDATE
    @Operation(summary = "Update station with ID",
            description = "Update station with ID",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = StationDTO.class)), description = "Station with appropriate ID updated"),
                    @ApiResponse(responseCode = "404", description = "Station not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @PUT
    @Path("{id}")
    public Response updateStation(@Parameter(description = "ID of the station to be returned", schema = @Schema(type = "integer", description = "ID of the station to be returned"), required = true) @PathParam("id") int id,
                                  @RequestBody(description = "New station model object", required = true, content = @Content(schema = @Schema(implementation = StationDTO.class))) @Valid StationDTO stationDTO) {
        StationDTO updatedStation = stationService.updateStation(id, stationDTO);
        return Response.ok(updatedStation).build();
    }


    // DELETE
    @Operation(summary = "Delete station by ID",
            description = "Delete station by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Station deleted"),
                    @ApiResponse(responseCode = "404", description = "Station not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @DELETE
    @Path("{id}")
    public Response deleteParameter(@Parameter(description = "ID of the station to be deleted", schema = @Schema(type = "integer", description = "ID of the station to be deleted"), required = true)@PathParam("id") int id) {
        StationDTO deletedStation = stationService.deleteStation(id);
        return Response.ok(deletedStation).build();
    }
}