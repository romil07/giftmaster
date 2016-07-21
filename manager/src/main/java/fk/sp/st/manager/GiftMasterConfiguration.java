package fk.sp.st.manager;


import fk.sp.common.event.publisher.EventConfiguration;
import fk.sp.common.extensions.GraphiteConfig;
import fk.sp.common.extensions.swagger.HasSwaggerConfiguration;
import fk.sp.common.extensions.swagger.SwaggerConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class GiftMasterConfiguration extends Configuration
        implements HasSwaggerConfiguration {

    @Valid
    private GraphiteConfig graphiteConfig = null;

    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @Valid
    @NotNull
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return httpClient;
    }

    @Valid
    @NotNull
    private SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();

    @Valid
    @NotNull
    private EventConfiguration eventConfiguration = new EventConfiguration();

    @Override
    public SwaggerConfiguration getSwaggerConfiguration() {
        return this.swaggerConfiguration;
    }


}
