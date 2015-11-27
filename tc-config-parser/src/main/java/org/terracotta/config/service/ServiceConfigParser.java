/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Configuration.
 *
 * The Initial Developer of the Covered Software is
 * Terracotta, Inc., a Software AG company
 *
 */

package org.terracotta.config.service;

import org.w3c.dom.Element;

import javax.xml.transform.Source;
import java.io.IOException;
import java.net.URI;
import org.terracotta.entity.ServiceProviderConfiguration;

/**
 * Interface which defines which a service configuration should be parsed. Each of the service configuration parser
 * should define the xml schema along with namespacing for the service configuration.
 *
 * @author vmad
 */
public interface ServiceConfigParser {

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
   ServiceProviderConfiguration parse(Element fragment, String source);
}
