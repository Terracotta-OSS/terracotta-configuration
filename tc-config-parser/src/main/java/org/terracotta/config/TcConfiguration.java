package org.terracotta.config;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.terracotta.entity.ServiceProviderConfiguration;

public class TcConfiguration {
  private final TcConfig platformConfiguration;

  private final Map<String, List<ServiceProviderConfiguration>> serviceConfigurations;

  public TcConfiguration(TcConfig platformConfiguration, String source , Map<String, List<ServiceProviderConfiguration>> serviceConfigurations) {
    this.platformConfiguration = platformConfiguration;
    this.serviceConfigurations = serviceConfigurations;
  }

  public TcConfig getPlatformConfiguration() {
    return platformConfiguration;
  }

  public Map<String, List<ServiceProviderConfiguration>> getServiceConfigurations() {
    return this.serviceConfigurations;
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    JAXB.marshal(platformConfiguration, sw);
    return sw.toString();
  }

}
