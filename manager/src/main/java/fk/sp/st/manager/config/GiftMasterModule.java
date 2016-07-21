package fk.sp.st.manager.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.Clock;

import javax.sql.DataSource;

import fk.sp.common.event.publisher.EventConfiguration;
import fk.sp.common.extensions.GraphiteConfig;
import fk.sp.common.extensions.dropwizard.db.HasDataSourceFactory;
import fk.sp.sa.reports.update.ReportDefinitionFile;
import fk.sp.st.manager.GiftMasterConfiguration;
import fk.sp.st.manager.GiftMasterResource;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GiftMasterModule extends AbstractModule {

  private static final String DB_NAME = "DarwinDb";


  @Override
  protected void configure() {
    Multibinder<ReportDefinitionFile> multiBinder = Multibinder.newSetBinder(binder(),
                                                                             ReportDefinitionFile.class);

    bind(GiftMasterResource.class).in(Singleton.class);

  }

  @Provides
  HasDataSourceFactory providesDatabaseConfiguration(
      final Provider<GiftMasterConfiguration> analyticsDataServiceConfigurationProvider) {
    return () -> analyticsDataServiceConfigurationProvider.get().getDataSourceFactory();
  }

  @Provides
  @Singleton
  Client providesJerseyClient(Environment environment,
                              GiftMasterConfiguration GiftMasterConfiguration) {
    return new JerseyClientBuilder(environment)
        .using(GiftMasterConfiguration.getJerseyClientConfiguration())
        .build("JERSEY_CLIENT");
  }

  @Provides
  ObjectMapper objectMapper(Environment environment) {
    return environment.getObjectMapper();
  }

  @Provides
  JdbcTemplate providesJdbcTemplate(DataSource dataSource)
      throws ClassNotFoundException {
    return new JdbcTemplate(dataSource);
  }

  @Provides
  NamedParameterJdbcTemplate providesNamedParameterJdbcTemplate(DataSource dataSource)
      throws ClassNotFoundException {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  @Provides
  Clock providesClock() {
    return Clock.systemDefaultZone();
  }

}
