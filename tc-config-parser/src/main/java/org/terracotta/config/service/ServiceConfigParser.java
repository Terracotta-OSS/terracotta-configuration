/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

  /**
   * Get the validator for the XMLElement
   *
   * @return {@link ConfigValidator}.
   */
  default ConfigValidator getConfigValidator() {
    return ConfigValidator.NOOP_VALIDATOR;
  }
}
