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
