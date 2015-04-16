package org.terracotta.config;

import org.terracotta.config.service.ServiceConfigParser;
import org.w3c.dom.Element;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class FooServiceConfigurationParser implements ServiceConfigParser<Object> {
  private static final URI NAMESPACE = URI.create("http://www.example.com/foo");
  private static final URL XML_SCHEMA = FooServiceConfigurationParser.class.getResource("/foo.xsd");

  static final Object parsedObject = new Object();
  @Override
  public Source getXmlSchema() throws IOException {
    return new StreamSource(XML_SCHEMA.openStream());
  }

  @Override
  public URI getNamespace() {
    return NAMESPACE;
  }

  @Override
  public Object parse(Element fragment, String source) {
    return parsedObject;
  }
}
