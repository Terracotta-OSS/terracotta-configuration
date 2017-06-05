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

import com.example.bar.Bar;
import org.junit.Test;
import org.terracotta.config.FooServiceConfigurationParser.FooServiceProviderConfiguration;
import org.terracotta.entity.ServiceProviderConfiguration;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class TCConfigurationParserTest {


  @Test
  public void testSimpleTCParser() throws Exception {

    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-1.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();

    assertThat("servers should not be secured", tcConfig.getServers().isSecure(), is(false));
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

    assertThat("Server management-port port should be 210", s.getManagementPort().getValue(), is(210));
    assertThat("Server management-port bind should be 0.0.0.0", s.getManagementPort().getBind(), is("0.0.0.0"));

    assertThat("Server tsa-group-port port should be 220", s.getTsaGroupPort().getValue(), is(220));
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

  @Test
  public void testDefaultServerPorts() throws Exception {
    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-default-settings.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getPlatformConfiguration();
    List<Server> servers = tcConfig.getServers().getServer();
    Server s = servers.get(0);
    assertThat("Server tsa-port port should be " + TCConfigDefaults.TSA_PORT, s.getTsaPort().getValue(), is(TCConfigDefaults.TSA_PORT));
    assertThat("Server tsa-group-port port should be " + TCConfigDefaults.GROUP_PORT, s.getTsaGroupPort().getValue(), is(TCConfigDefaults.GROUP_PORT));
    assertThat("Server management-port port should be " + TCConfigDefaults.MANAGEMENT_PORT, s.getManagementPort().getValue(), is(TCConfigDefaults.MANAGEMENT_PORT));
  }
}
