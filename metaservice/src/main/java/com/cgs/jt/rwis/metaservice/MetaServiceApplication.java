package com.cgs.jt.rwis.metaservice;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cgs.jt.rwis.api.EarthSurfacePoint;
import com.cgs.jt.rwis.metaservice.core.*;
import com.cgs.jt.rwis.metaservice.db.dao.*;
import com.cgs.jt.rwis.metaservice.db.entity.*;
import com.cgs.jt.rwis.metaservice.health.DatabaseHealthCheck;
import com.cgs.jt.rwis.metaservice.health.TemplateHealthCheck;
import com.cgs.jt.rwis.metaservice.resources.*;
import com.cgs.jt.rwis.srvcs.json.EarthSurfacePointMapKeySerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

// https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Getting-started
// https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations
@OpenAPIDefinition(
        info = @Info(
                title = "CGS Labs Vedra metaservice",
                version = "1.6",
                description = "Openapi 3 specification for Vedra metaservice",
                license = @License(name = "CGS Labs d.o.o.", url = "https://cgs-labs.si/"),
                contact = @Contact(name = "Borut Sila", email = "borut.sila@cgs-labs.com")
        )
)
public class MetaServiceApplication extends Application<MetaServiceConfiguration> {
    private static final Logger log = Logger.getLogger(MetaServiceApplication.class.getName());

    private final HibernateBundle<MetaServiceConfiguration> hibernateBundle = new HibernateBundle<MetaServiceConfiguration>(
            Model.class,
            MeasuredParameter.class,
            ForecastedParameter.class,
            ParameterOnStation.class,
            Station.class,
            BaseCanSeeStation.class,
            Subscription.class,
            Location.class,
            MetroConfig.class
    ) {
        @Override
        public DataSourceFactory getDataSourceFactory(MetaServiceConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final MigrationsBundle<MetaServiceConfiguration> migrationsBundle = new MigrationsBundle<MetaServiceConfiguration>() {
        @Override
        public DataSourceFactory getDataSourceFactory(MetaServiceConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    private final SwaggerConfiguration swaggerConfig = new SwaggerConfiguration()
            .openAPI(new OpenAPI())
            .prettyPrint(true)
            .resourcePackages(Stream.of("com.cgs.jt.rwis.metaservice").collect(Collectors.toSet()));

    public static void main(final String[] args) throws Exception {
        new MetaServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "MetaService";
    }

    @Override
    public void initialize(final Bootstrap<MetaServiceConfiguration> bootstrap) {
        log.info("Initializing MetaService.");
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(migrationsBundle);

        //we get access to the Jackson ObjectMapper and register our custom key serializer/deserializer via SimpleModule
        //NOTE: pay attention that this same ObjectMapper is then later used/configured by dropwizard environment (i.e. the
        //code that reads Dropwizard config .yml file) - so settings done here might get overridden later...
        //however registering custom serializers/deserializers works without problem
        ObjectMapper mapper = bootstrap.getObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addKeySerializer(EarthSurfacePoint.class, new EarthSurfacePointMapKeySerializer());
        mapper.registerModule(module);
        //also configure the format of java.time.Instant when serialized into JSON
        //I believe that the JavaTimeModule is already registered with the ObjectMapper by default (by Dropwizard)
        //so we just need to configure the object mapper
        //see https://stackoverflow.com/questions/45662820/how-to-set-format-of-string-for-java-time-instant-using-objectmapper
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);//will produce {"time":"2017-08-14T12:17:47.720Z"}

    }

    @Override
    public void run(final MetaServiceConfiguration configuration, final Environment environment) {
        log.info("Running MetaService.");

        // Openapi 3
        environment.jersey().register(new OpenApiResource().openApiConfiguration(swaggerConfig));

        // Registering resources
        environment.jersey().register(ModelResource.class);
        environment.jersey().register(MeasuredParameterResource.class);
        environment.jersey().register(ForecastedParameterResource.class);
        environment.jersey().register(StationResource.class);
        environment.jersey().register(SubscriptionResource.class);
        environment.jersey().register(MetroConfigResource.class);

        // Registering health checks
        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
        final DatabaseHealthCheck databaseHealthCheck = new DatabaseHealthCheck(configuration.getDataSourceFactory());
        environment.healthChecks().register("template", healthCheck);
        environment.healthChecks().register("database", databaseHealthCheck);

        // Initializing DAO's
        final ModelDAO modelDAO = new ModelDAO(hibernateBundle.getSessionFactory());
        final MeasuredParameterDAO measuredParameterDAO = new MeasuredParameterDAO(hibernateBundle.getSessionFactory());
        final ForecastedParameterDAO forecastedParameterDAO = new ForecastedParameterDAO(hibernateBundle.getSessionFactory());
        final StationDAO stationDAO = new StationDAO(hibernateBundle.getSessionFactory());
        final SubscriptionDAO subscriptionDAO = new SubscriptionDAO(hibernateBundle.getSessionFactory());
        final MetroConfigDAO metroConfigDAO = new MetroConfigDAO(hibernateBundle.getSessionFactory());
        final LocationDAO locationDAO = new LocationDAO(hibernateBundle.getSessionFactory());

        // Registering classes for use by HK2 dependency injection library
        environment.jersey().register(new AbstractBinder() {
            @Override
            public void configure() {
                // Service classes
                bindAsContract(ModelService.class);
                bindAsContract(MeasuredParameterService.class);
                bindAsContract(ForecastedParameterService.class);
                bindAsContract(StationService.class);
                bindAsContract(SubscriptionService.class);
                bindAsContract(MetroConfigService.class);

                // DAO classes
                bind(modelDAO).to(ModelDAO.class);
                bind(measuredParameterDAO).to(MeasuredParameterDAO.class);
                bind(forecastedParameterDAO).to(ForecastedParameterDAO.class);
                bind(stationDAO).to(StationDAO.class);
                bind(subscriptionDAO).to(SubscriptionDAO.class);
                bind(metroConfigDAO).to(MetroConfigDAO.class);
                bind(locationDAO).to(LocationDAO.class);
            }
        });
    }
}
