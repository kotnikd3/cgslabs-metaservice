package com.cgs.jt.rwis.metaservice.resources;

import com.cgs.jt.rwis.metaservice.api.MeasuredParameterDTO;
import com.cgs.jt.rwis.metaservice.core.MeasuredParameterService;
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
@Path("measuredparameters")
public class MeasuredParameterResource {

    @Inject
    private MeasuredParameterService parameterService;

    // CREATE
    @Operation(summary = "Create measured parameter",
            description = "Create a new measured parameter",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Measured parameter created", headers = {@Header(schema = @Schema(type = "string"), name = "Location", description = "URL to a created measured parameter")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @POST
    @UnitOfWork
    public Response createParameter(@RequestBody(description = "New parameter object", required = true,
            content = @Content(schema = @Schema(implementation = MeasuredParameterDTO.class))) @Valid MeasuredParameterDTO parameterDTO) {
        MeasuredParameterDTO createdParameter = parameterService.createParameter(parameterDTO);

        return Response
                .ok(createdParameter)
                .status(Response.Status.CREATED)
                .header("Location", "/measuredparameters/" + createdParameter.getName() + "/" + createdParameter.getName())
                .build();
    }

    // READ
    @Operation(summary = "Get measured parameters",
            description = "Get all the measured parameters which are being used on RWS's",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MeasuredParameterDTO.class)),
                            description = "List of all the measured parameters", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned measured parameters")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    public Response getParameters() {
        List<MeasuredParameterDTO> parameterList = parameterService.getParameters();

        return Response
                .ok(parameterList)
                .header("X-Total-Count", parameterList.size())
                .build();
    }

    @Operation(summary = "Get measured parameter by name",
            description = "Get measured parameter by name",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = MeasuredParameterDTO.class)), description = "Measured parameter with appropriate name"),
                    @ApiResponse(responseCode = "404", description = "Parameter not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{name}")
    public Response getParameter(
            @Parameter(description = "Name of the parameter to be returned", schema = @Schema(type = "string", description = "Name of parameter to be returned"), required = true) @PathParam("name") String name) {
        MeasuredParameterDTO parameterDTO = parameterService.getParameter(name);
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
    @Operation(summary = "Delete measured parameter by name",
            description = "Delete measured parameter by name",
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
        MeasuredParameterDTO deletedParameter = parameterService.deleteParameter(name);
        return Response.ok(deletedParameter).build();
    }
}