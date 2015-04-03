package org.terracotta.config;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.List;

public class TcConfiguration {
  private final TcConfig platformConfiguration;

  private final List<?> serviceConfigurations;

  public TcConfiguration(TcConfig platformConfiguration, List<?> serviceConfigurations) {
    this.platformConfiguration = platformConfiguration;
    this.serviceConfigurations = serviceConfigurations;
  }

  public TcConfig getPlatformConfiguration() {
    return platformConfiguration;
  }


  public List<?> getServiceConfigurations() {
    return this.serviceConfigurations;
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    JAXB.marshal(platformConfiguration, sw);
    return sw.toString();
  }

}
