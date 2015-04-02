package org.terracotta.config;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.List;

public class TcConfigDocument {
  private final TcConfig config;

  private final List<?> serviceConfigurations;

  public TcConfigDocument(TcConfig config, List<?> serviceConfigurations) {
    this.config = config;
    this.serviceConfigurations = serviceConfigurations;
  }

  public TcConfig getTcConfig() {
    return config;
  }


  public List<?> getServiceConfigurations() {
    return this.serviceConfigurations;
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    JAXB.marshal(config, sw);
    return sw.toString();
  }

}
