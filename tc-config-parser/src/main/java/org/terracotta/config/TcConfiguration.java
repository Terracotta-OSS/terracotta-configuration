package org.terracotta.config;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.List;
import org.terracotta.entity.ServiceProviderConfiguration;

public class TcConfiguration {
  private final TcConfig platformConfiguration;

  private final List<ServiceProviderConfiguration> serviceConfigurations;

  public TcConfiguration(TcConfig platformConfiguration, String source ,List<ServiceProviderConfiguration> serviceConfigurations) {
    this.platformConfiguration = platformConfiguration;
    this.serviceConfigurations = serviceConfigurations;
  }

  public TcConfig getPlatformConfiguration() {
    return platformConfiguration;
  }

  public List<ServiceProviderConfiguration> getServiceConfigurations() {
    return this.serviceConfigurations;
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    JAXB.marshal(platformConfiguration, sw);
    return sw.toString();
  }

}
