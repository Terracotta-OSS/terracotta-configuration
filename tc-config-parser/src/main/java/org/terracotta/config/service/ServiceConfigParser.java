package org.terracotta.config.service;

import org.w3c.dom.Element;

import javax.xml.transform.Source;
import java.io.IOException;
import java.net.URI;

/**
 * Interface which defines which a service configuration should be parsed. Each of the service configuration parser
 * should define the xml schema along with namespacing for the service configuration.
 *
 * @author vmad
 */
public interface ServiceConfigParser<T> {

  /**
   * Get the XML schema which will be handled by the service configuration parser.
   * @return schema source
   * @throws IOException in case any error.
   */
  Source getXmlSchema() throws IOException;

  /**
   * The name space which is handled by the service configuration parser.
   * @return namespace
   */
  URI getNamespace();

  /**
   * Parse method which handles converting the XMLElement into a particular service configuration for a particular service.
   * @param fragment element to be parsed
   * @return service configuration
   */
   T parse(Element fragment);
}
