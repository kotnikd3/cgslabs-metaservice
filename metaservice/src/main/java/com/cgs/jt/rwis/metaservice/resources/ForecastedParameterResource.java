package com.cgs.jt.rwis.metaservice.resources;

import com.cgs.jt.rwis.metaservice.api.ForecastedParameterDTO;
import com.cgs.jt.rwis.metaservice.core.ForecastedParameterService;
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
@Path("forecastedparameters")
public class ForecastedParameterResource {

    @Inject
    private ForecastedParameterService parameterService;

    // CREATE
    @Operation(summary = "Create forecasted parameter",
            description = "Create a new forecasted parameter",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Forecasted parameter created", headers = {@Header(schema = @Schema(type = "string"), name = "Location", description = "URL to a created forecasted parameter")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @POST
    @UnitOfWork
    public Response createParameter(@RequestBody(description = "New parameter object", required = true,
            content = @Content(schema = @Schema(implementation = ForecastedParameterDTO.class))) @Valid ForecastedParameterDTO parameterDTO) {
        ForecastedParameterDTO createdParameter = parameterService.createParameter(parameterDTO);

        return Response
                .ok(createdParameter)
                .status(Response.Status.CREATED)
                .header("Location", "/forecastedparameters/" + createdParameter.getName() + "/" + createdParameter.getName())
                .build();
    }

    // READ
    @Operation(summary = "Get forecasted parameters",
            description = "Get all the forecasted parameters which are being used on RWS's",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ForecastedParameterDTO.class)),
                            description = "List of all the forecasted parameters", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned forecasted parameters")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    public Response getParameters() {
        List<ForecastedParameterDTO> parameterList = parameterService.getParameters();

        return Response
                .ok(parameterList)
                .header("X-Total-Count", parameterList.size())
                .build();
    }

    @Operation(summary = "Get forecasted parameter by name",
            description = "Get forecasted parameter by name",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ForecastedParameterDTO.class)), description = "Forecasted parameter with appropriate name"),
                    @ApiResponse(responseCode = "404", description = "Parameter not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{name}")
    public Response getParameter(
            @Parameter(description = "Name of the parameter to be returned", schema = @Schema(type = "string", description = "Name of parameter to be returned"), required = true) @PathParam("name") String name) {
        ForecastedParameterDTO parameterDTO = parameterService.getParameter(name);
        return Response.ok(parameterDTO).build();
    }


    // UPDATE
    /*@UnitOfWork
    @PUT
    @Path("{name}")
    public Response updateParameter(@PathParam("name") String name, @Valid ParameterDTO parameterDTO) {
        ParameterDTO updatedParameter = parameterService.updateParameter(name, parameterDTO);
        return Response.ok(updatedParameter).build();
    }*/


    // DELETE
    @Operation(summary = "Delete forecasted parameter by name",
            description = "Delete forecasted parameter by name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Parameter deleted"),
                    @ApiResponse(responseCode = "404", description = "Parameter not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @DELETE
    @Path("{name}")
    public Response deleteParameter(@Parameter(
            description = "Name of the parameter to be deleted", schema = @Schema(type = "string", description = "Name of parameter to be deleted"), required = true) @PathParam("name") String name) {
        ForecastedParameterDTO deletedParameter = parameterService.deleteParameter(name);
        return Response.ok(deletedParameter).build();
    }
}