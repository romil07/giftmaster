package fk.sp.st.manager;

import com.google.common.collect.Sets;
import com.google.inject.Stage;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.palominolabs.metrics.guice.MetricsInstrumentationModule;

import java.util.Properties;

import fk.sp.common.extensions.dropwizard.hystrix.HystrixRequestContextModule;
import fk.sp.common.extensions.dropwizard.logging.RequestContextBundle;
import fk.sp.common.extensions.guice.jpa.spring.JpaWithSpringModule;
import fk.sp.common.extensions.swagger.SwaggerBundle;
import fk.sp.st.manager.config.GiftMasterModule;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class GiftMasterApplication extends Application<GiftMasterConfiguration> {

  private GuiceBundle<GiftMasterConfiguration> guiceBundle;

  public static void main(String[] args) throws Exception {
    new GiftMasterApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<GiftMasterConfiguration> bootstrap) {
    bootstrap.addBundle(new MigrationsBundle<GiftMasterConfiguration>() {
      public DataSourceFactory getDataSourceFactory(
          GiftMasterConfiguration configuration) {
        return configuration.getDataSourceFactory();
      }
    });

    Properties jpaProperties = new Properties();

    GuiceBundle.Builder<GiftMasterConfiguration>
        guiceBundleBuilder =
        GuiceBundle.newBuilder();
    bootstrap.addBundle(new RequestContextBundle());
    guiceBundle = guiceBundleBuilder.
        setConfigClass(GiftMasterConfiguration.class)
        .addModule(new HystrixRequestContextModule())
        .addModule(new MetricsInstrumentationModule(bootstrap.getMetricRegistry()))
        .addModule(new GiftMasterModule())
        .addModule(new JpaWithSpringModule(Sets.newHashSet(),
                                           jpaProperties))
        .enableAutoConfig(
            "fk.sp.common.extensions.jackson",

            "fk.sp.common.extensions.dropwizard.hystrix",
            "fk.sp.common.extensions.dropwizard.logging",
            "fk.sp.common.extensions.dropwizard.jersey"
        )
        .build(Stage.DEVELOPMENT);
    bootstrap.addBundle(guiceBundle);
    bootstrap.addBundle(new ViewBundle());
    bootstrap.addBundle(new SwaggerBundle());
  }

  @Override
  public void run(GiftMasterConfiguration configuration, Environment environment)
      throws Exception {
  }

}
