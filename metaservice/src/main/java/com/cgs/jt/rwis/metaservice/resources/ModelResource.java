package com.cgs.jt.rwis.metaservice.resources;

import com.cgs.jt.rwis.metaservice.api.ModelDTO;
import com.cgs.jt.rwis.metaservice.core.ModelService;
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
@Path("models")
public class ModelResource {

    @Inject
    private ModelService modelService;

    // CREATE
    @Operation(summary = "Create forecast model",
            description = "Create a new model which will be used as a forecasting model",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Model created", headers = {@Header(schema = @Schema(type = "string"), name = "Location", description = "URL to a created model")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @POST
    @UnitOfWork
    public Response createModel(@RequestBody(description = "New forecasted model object", required = true, content = @Content(schema = @Schema(implementation = ModelDTO.class))) @Valid ModelDTO modelDTO) {
        ModelDTO createdModel = modelService.createModel(modelDTO);

        return Response
                .ok(createdModel)
                .status(Response.Status.CREATED)
                .header("Location", "/models/" + createdModel.getName().toString())
                .build();
    }

    // READ
    @Operation(summary = "Get models",
            description = "Get all the models for forecasting",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ModelDTO.class)),
                            description = "List of all the models", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned models")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    public Response getModels() {
        List<ModelDTO> modelDTOList = modelService.getModels();

        return Response
                .ok(modelDTOList)
                .header("X-Total-Count", modelDTOList.size())
                .build();
    }

    @Operation(summary = "Get model by name",
            description = "Get model for forecasting by name",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ModelDTO.class)), description = "Model with appropriate name"),
                    @ApiResponse(responseCode = "404", description = "Model not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{name}")
    public Response getModel(@Parameter(description = "Name of the model to be returned", schema = @Schema(type = "string", description = "Name of model to be returned"), required = true) @PathParam("name") String name) {
        ModelDTO modelDTO = modelService.getModel(name);
        return Response.ok(modelDTO).build();
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
    @Operation(summary = "Delete model by name",
            description = "Delete model for forecasting by name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Model deleted"),
                    @ApiResponse(responseCode = "404", description = "Model not found"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @DELETE
    @Path("{name}")
    public Response deleteModel(@Parameter(description = "Name of the model to be deleted", schema = @Schema(type = "string", description = "Name of model to be returned"), required = true) @PathParam("name") String name) {
        ModelDTO deletedModelDTO = modelService.deleteModel(name);
        return Response.ok(deletedModelDTO).build();
    }
}
