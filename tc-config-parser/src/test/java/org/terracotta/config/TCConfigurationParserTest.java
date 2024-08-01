/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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

import org.junit.Test;
import org.terracotta.config.FooServiceConfigurationParser.FooServiceProviderConfiguration;
import org.terracotta.config.util.ParameterSubstitutor;
import org.terracotta.entity.ServiceProviderConfiguration;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class TCConfigurationParserTest {


  @Test
  public void testSimpleTCParser() throws Exception {

    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-1.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();

    assertThat("servers client reconnect window should be 200", tcConfig.getServers().getClientReconnectWindow(), is(200));

    List<Server> servers = tcConfig.getServers().getServer();
    assertThat("Server configuration should not be null", servers, notNullValue());
    assertThat("Number of servers should be 1", servers.size(), is(1));

    Server s = servers.get(0);

    assertThat("Server hostname should be hostname", s.getHost(), is("hostname"));
    assertThat("Server bind port should be 0.0.0.0", s.getBind(), is("0.0.0.0"));
    assertThat("Server name should be hostname1", s.getName(), is("hostname1"));

    assertThat("Server log location should be mylog", s.getLogs(), is(new File(resource.getPath()+ File.separator+"mylog").getAbsolutePath()));

    assertThat("Server tsa-port port should be 200", s.getTsaPort().getValue(), is(200));
    assertThat("Server tsa-port bind should be 0.0.0.0", s.getTsaPort().getBind(), is("0.0.0.0"));

    assertThat("Server tsa-group-port port should be 210", s.getTsaGroupPort().getValue(), is(210));
    assertThat("Server tsa-group-port bind should be 0.0.0.0", s.getTsaGroupPort().getBind(), is("0.0.0.0"));

    TcProperties tCproperties = tcConfig.getTcProperties();
    List<Property> properties = tCproperties.getProperty();
    assertThat("Number of properties should be 1", properties.size(), is(1));
    assertThat("key property should be name1", properties.get(0).getName(), is("name1"));
    assertThat("value property should be blah1", properties.get(0).getValue(), is("blah1"));

  }

  @Test
  public void testServiceParser() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-service.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);

    List<ServiceProviderConfiguration> serviceConfigurations = conf.getServiceConfigurations();

    assertThat("service configuration should not be null", serviceConfigurations, notNullValue());
    assertThat(serviceConfigurations.get(0), instanceOf(FooServiceProviderConfiguration.class));
    FooServiceProviderConfiguration serviceProviderConfiguration = (FooServiceProviderConfiguration) serviceConfigurations.get(0);
    assertEquals("foo", serviceProviderConfiguration.getFoo().getName());
  }

  @Test
  public void testEmptyService() throws Exception {

    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-empty-service.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);

    assertThat("there should be no services", conf.getServiceConfigurations().size(), is(0));

  }
  
  
  @Test
  public void testConfigAndService() throws Exception {

    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-config-service.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    List<String> configs = conf.getExtendedConfiguration(String.class);
    assertEquals(2, configs.size());
    assertEquals("bar", configs.get(0));
    assertEquals("baz", configs.get(1));

    List<ServiceProviderConfiguration> serviceConfigurations = conf.getServiceConfigurations();

    assertThat("service configuration should not be null", serviceConfigurations, notNullValue());
    assertThat(serviceConfigurations.get(0), instanceOf(FooServiceProviderConfiguration.class));
    FooServiceProviderConfiguration serviceProviderConfiguration = (FooServiceProviderConfiguration) serviceConfigurations.get(0);
    assertEquals("foo", serviceProviderConfiguration.getFoo().getName());
  }

  @Test (expected = TCConfigurationSetupException.class)
  public void testConfigAndServiceWithConfigurationErrors() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-config-service-with-errors.xml");
    TCConfigurationParser.parse(resource.openStream());
  }


  @Test
  public void testDefaults() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-default-settings.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();
    List<Server> servers = tcConfig.getServers().getServer();
    Server s = servers.get(0);
    assertThat("Server tsa-port port should be " + TCConfigDefaults.TSA_PORT, s.getTsaPort().getValue(), is(TCConfigDefaults.TSA_PORT));
    assertThat("Server tsa-group-port port should be " + TCConfigDefaults.GROUP_PORT, s.getTsaGroupPort().getValue(), is(TCConfigDefaults.GROUP_PORT));

    String defaultLogPath = Paths.get(resource.toURI()).resolve("logs").resolve(ParameterSubstitutor.getHostName() + "-" + TCConfigDefaults.TSA_PORT).toString();
    assertThat(s.getLogs(), is(defaultLogPath));
  }

  @Test
  public void testFailoverPriorityAvailability() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-config-failover-availability.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();
    assertThat(tcConfig.getFailoverPriority().getAvailability(), notNullValue());
  }

  @Test
  public void testFailoverPriorityConsistency() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-config-failover-consistency.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();
    Consistency consistency = tcConfig.getFailoverPriority().getConsistency();
    assertThat(consistency, notNullValue());
    assertThat(consistency.getVoter(), nullValue());
  }

  @Test
  public void testFailoverPriorityConsistencyVoters() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-config-failover-consistency-voters.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();
    Consistency consistency = tcConfig.getFailoverPriority().getConsistency();
    assertThat(consistency, notNullValue());
    Voter voter = consistency.getVoter();
    assertThat(voter, notNullValue());
    assertThat(voter.getCount(), is(2));
  }
}
