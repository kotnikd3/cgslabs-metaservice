package com.cgs.jt.rwis.metaservice.health;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.db.DataSourceFactory;

public class DatabaseHealthCheck extends HealthCheck {
    private final DataSourceFactory dataSourceFactory;

    public DatabaseHealthCheck(DataSourceFactory dataSourceFactory) {
        this.dataSourceFactory = dataSourceFactory;
    }

    @Override
    protected Result check() throws Exception {
        if (dataSourceFactory.getCheckConnectionOnConnect()) {
            return Result.healthy();
        } else {
            return Result.unhealthy("Cannot connect to " + dataSourceFactory.getUrl());
        }
    }
}
