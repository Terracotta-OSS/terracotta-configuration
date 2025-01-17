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
package org.terracotta.config;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.terracotta.entity.ServiceProviderConfiguration;
import org.terracotta.entity.StateDumpCollector;
import org.terracotta.entity.StateDumpable;

public class TcConfiguration implements StateDumpable {
  private final TcConfig platformConfiguration;

  private final List<ServiceProviderConfiguration> serviceConfigurations;
  private final List<Object> objects;

  public TcConfiguration(TcConfig platformConfiguration, String source , List<Object> objects, List<ServiceProviderConfiguration> serviceConfigurations) {
    this.platformConfiguration = platformConfiguration;
    this.serviceConfigurations = serviceConfigurations;
    this.objects = objects;
  }

  public TcConfig getPlatformConfiguration() {
    return platformConfiguration;
  }

  public List<ServiceProviderConfiguration> getServiceConfigurations() {
    return this.serviceConfigurations;
  }
  
  public <T> List<T> getExtendedConfiguration(Class<T> type) {
    return objects.stream().filter(o->type.isInstance(o)).map(o->type.cast(o)).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    JAXB.marshal(platformConfiguration, sw);
    return sw.toString();
  }

  @Override
  public void addStateTo(final StateDumpCollector stateDumpCollector) {
    for (Object config : objects){
      if(config instanceof StateDumpable) {
        ((StateDumpable)config).addStateTo(stateDumpCollector.subStateDumpCollector(config.getClass().getName()));
      }
    }
  }
}
