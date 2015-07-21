package org.terracotta.config;

import org.terracotta.config.service.ServiceConfigParser;
import org.w3c.dom.Element;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.terracotta.entity.ServiceProvider;
import org.terracotta.entity.ServiceProviderConfiguration;

public class FooServiceConfigurationParser implements ServiceConfigParser {
  private static final URI NAMESPACE = URI.create("http://www.example.com/foo");
  private static final URL XML_SCHEMA = FooServiceConfigurationParser.class.getResource("/foo.xsd");

  static final ServiceProviderConfiguration parsedObject = new ServiceProviderConfiguration() {

    @Override
    public Class<? extends ServiceProvider> getServiceProviderType() {
      return ServiceProvider.class;
    }

    
  };
  @Override
  public Source getXmlSchema() throws IOException {
    return new StreamSource(XML_SCHEMA.openStream());
  }

  @Override
  public URI getNamespace() {
    return NAMESPACE;
  }

  @Override
  public ServiceProviderConfiguration parse(Element fragment, String source) {
    return parsedObject;
  }
}
