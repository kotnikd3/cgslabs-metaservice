package com.cgs.jt.rwis.metaservice.resources;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.api.ParameterForecastSubscription;
import com.cgs.jt.rwis.metaservice.core.SubscriptionService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("subscriptions")
public class SubscriptionResource {

    @Inject
    private SubscriptionService subscriptionService;

    // CREATE
    @Operation(summary = "Create subscription",
            description = "Create a new subscription",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Subscription created", headers = {@Header(schema = @Schema(type = "string"), name = "Location", description = "URL to a created subscription")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @POST
    @UnitOfWork
    public Response createSubscription(@RequestBody(description = "New subscription object", required = true,
            content = @Content(schema = @Schema(implementation = ParameterForecastSubscription.class))) @Valid ParameterForecastSubscription subscription) {
        ParameterForecastSubscription createdSubscription = subscriptionService.createSubscription(subscription);

        return Response
                .ok(createdSubscription)
                .status(Response.Status.CREATED)
                .build();
    }

    // READ
    @Operation(summary = "Get subscriptions",
            description = "Get all the subscriptions",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ParameterForecastSubscription.class)),
                            description = "List of all the subscriptions", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned subscriptions")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    public Response getSubscriptions() {
        List<ParameterForecastSubscription> subscriptionDTOList = subscriptionService.getSubscriptions();

        return Response
                .ok(subscriptionDTOList)
                .header("X-Total-Count", subscriptionDTOList.size())
                .build();
    }

    @Operation(summary = "Get subscriptions for model",
            description = "Get all the subscriptions for model",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ParameterForecastSubscription.class)),
                            description = "List of all the subscriptions for model", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned subscriptions for model")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{model}")
    public Response getSubscriptionsForModel(@Parameter(description = "Name of the model for subscriptions", schema = @Schema(type = "string", description = "Name of the model for subscriptions to be returned"), required = true) @PathParam("model") String model) {
        HashMap<EarthSurfacePoint, HashMap<String, HashSet<String>>> subscriptionDTOList = subscriptionService.getSubscriptionsByModelName(model);

        return Response
                .ok(subscriptionDTOList)
                .header("X-Total-Count", subscriptionDTOList.size())
                .build();
    }

    @Operation(summary = "Get subscriptions for model and location",
            description = "Get all the subscriptions for model and location",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ParameterForecastSubscription.class)),
                            description = "List of all the subscriptions for model and location", headers = {@Header(schema = @Schema(type = "integer"), name = "X-Total-Count", description = "Number of returned subscriptions for model and location")}),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @GET
    @Path("{model}/{latitude}/{longitude}")
    public Response getSubscriptionsForModelAndLocation(@Parameter(description = "Name of the model for subscriptions", schema = @Schema(type = "string", description = "Name of the model for subscriptions to be returned"), required = true) @PathParam("model") String model,
                                                        @Parameter(description = "Latitude location for subscription", schema = @Schema(type = "double", description = "Latitude location for subscription to be returned"), required = true) @PathParam("latitude") Double latitude,
                                                        @Parameter(description = "Longitude location for subscription", schema = @Schema(type = "double", description = "Longitude location for subscription to be returned"), required = true) @PathParam("longitude") Double longitude) {
        List<ParameterForecastSubscription> subscriptionDTOList = subscriptionService.getSubscriptionsByModelNameAndLocation(model, latitude, longitude);

        return Response
                .ok(subscriptionDTOList)
                .header("X-Total-Count", subscriptionDTOList.size())
                .build();
    }


    // UPDATE
    /*@UnitOfWork
    @PUT
    @Path("{id}")
    public Response updateSubscription(@PathParam("id") int id, @Valid SubscriptionDTO subscriptionDTO) {
        SubscriptionDTO updatedSubscription = subscriptionService.updateSubscription(id, subscriptionDTO);
        return Response.ok(updatedSubscription).build();
    }*/


    // DELETE
    @Operation(summary = "Delete subscription for model, location, parameter and customer",
            description = "Delete subscription for model, location, parameter and customer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Subscription deleted"),
                    @ApiResponse(responseCode = "500", description = "Server error")
            })
    @UnitOfWork
    @DELETE
    @Path("{model}/{latitude}/{longitude}/{customer}/{parameter}")
    public Response deleteSubscription(@Parameter(description = "Name of the model for subscription to be delted", schema = @Schema(type = "string", description = "Name of the model for subscriptions to be deleted"), required = true) @PathParam("model") String model,
                                       @Parameter(description = "Latitude for subscription to be deleted", schema = @Schema(type = "double", description = "Latitude location for subscription to be deleted"), required = true) @PathParam("latitude") Double latitude,
                                       @Parameter(description = "Longitude for subscription to be deleted", schema = @Schema(type = "double", description = "Longitude location for subscription to be deleted"), required = true) @PathParam("longitude") Double longitude,
                                       @Parameter(description = "Customer for subscription to be deleted", schema = @Schema(type = "string", description = "Customer for subscription to be deleted"), required = true) @PathParam("customer") String customer,
                                       @Parameter(description = "Forecasted parameter for subscription to be deleted", schema = @Schema(type = "string", description = "Forecasted parameter location for subscription to be deleted"), required = true) @PathParam("parameter") String parameter) {
        ParameterForecastSubscription deletedSubscription = subscriptionService.deleteSubscription(model, latitude, longitude, customer, parameter);
        return Response.ok(deletedSubscription).build();
    }
}
