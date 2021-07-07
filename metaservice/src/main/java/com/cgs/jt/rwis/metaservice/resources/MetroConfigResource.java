package com.cgs.jt.rwis.metaservice.resources;

import com.cgs.jt.rwis.metaservice.core.MetroConfigService;
import com.cgs.jt.rwis.metro.MetroLocationDescription;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("metroconfig")
public class MetroConfigResource {

    @Inject
    private MetroConfigService metroConfigService;

    // CREATE
    @Operation(summary = "Create METRO config",
            description = "Create a new METRO config for specific location",
            responses = {
                    @ApiResponse(responseCode = "201", description = "METRO config created", headers = {@Header(schema = @Schema(type = "string"), name = "Location", description = "URL to a created model")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @POST
    @UnitOfWork
    public Response createMetroConfig(@RequestBody(description = "New metroConfig model object", required = true, content = @Content(schema = @Schema(implementation = MetroLocationDescription.class))) @Valid MetroLocationDescription metroLocationDescription) {
        MetroLocationDescription createdMetroLocationDescription = metroConfigService.createMetroConfig(metroLocationDescription);

        return Response
                .ok(createdMetroLocationDescription)
                .status(Response.Status.CREATED)
                .header("Location", "/metroconfig/" + createdMetroLocationDescription.getForecastModelId() + "/" + createdMetroLocationDescription.getGeoLocation().getLatitude() + "/" + createdMetroLocationDescription.getGeoLocation().getLongitude())
                .build();
    }

    // READ
    @Operation(summary = "Get METRO config",
            description = "Get all the METRO configs",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MetroLocationDescription.class)),
                            description = "List of all the METRO configs", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned models")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    public Response getMetroConfigs() {
        List<MetroLocationDescription> metroLocationDescriptionList = metroConfigService.getMetroConfigs();

        return Response
                .ok(metroLocationDescriptionList)
                .header("X-Total-Count", metroLocationDescriptionList.size())
                .build();
    }

    @Operation(summary = "Get METRO config by model name and location",
            description = "Get METRO config by model name and location",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MetroLocationDescription.class)), description = "Metro config model"),
                    @ApiResponse(responseCode = "404", description = "Metro config not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{model}/{latitude}/{longitude}")
    public Response getMetroConfigForModelAndLocation(@Parameter(description = "Name of the model for METRO config", schema = @Schema(type = "string", description = "Name of the model for METRO config to be returned"), required = true) @PathParam("model") String model,
                                                      @Parameter(description = "Latitude location for METRO config", schema = @Schema(type = "double", description = "Latitude location for METRO config to be returned"), required = true) @PathParam("latitude") Double latitude,
                                                      @Parameter(description = "Longitude location for METRO config", schema = @Schema(type = "double", description = "Longitude location for METRO config to be returned"), required = true) @PathParam("longitude") Double longitude) {
        MetroLocationDescription metroLocationDescription = metroConfigService.getMetroConfigForModelAndLocation(model, latitude, longitude);
        return Response.ok(metroLocationDescription).build();
    }


    // UPDATE
    /*@UnitOfWork
    @PUT
    @Path("{name}")
    public Response updateModel(@PathParam("name") String name, @Valid ModelDTO modelDTO) {
        ModelDTO updatedModelDTO = modelService.updateModel(name, modelDTO);
        return Response.ok(updatedModelDTO).build();
    }*/


    // DELETE
    @Operation(summary = "Delete METRO config by model name and location",
            description = "Delete METRO config by model name and location",
            responses = {
                    @ApiResponse(responseCode = "200", description = "METRO config deleted"),
                    @ApiResponse(responseCode = "404", description = "METRO config not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @DELETE
    @Path("{model}/{latitude}/{longitude}")
    public Response deleteMetroConfigForModelAndLocation(@Parameter(description = "Name of the model for METRO config", schema = @Schema(type = "string", description = "Name of the model for METRO config to be returned"), required = true) @PathParam("model") String model,
                                @Parameter(description = "Latitude location for METRO config", schema = @Schema(type = "double", description = "Latitude location for METRO config to be returned"), required = true) @PathParam("latitude") Double latitude,
                                @Parameter(description = "Longitude location for METRO config", schema = @Schema(type = "double", description = "Longitude location for METRO config to be returned"), required = true) @PathParam("longitude") Double longitude) {
        MetroLocationDescription metroLocationDescription = metroConfigService.deleteMetroConfigForModelAndLocation(model, latitude, longitude);
        return Response.ok(metroLocationDescription).build();
    }
}
