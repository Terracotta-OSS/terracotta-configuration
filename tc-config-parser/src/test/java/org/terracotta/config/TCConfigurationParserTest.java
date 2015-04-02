package org.terracotta.config;

import org.junit.Test;

import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TCConfigurationParserTest {


  @Test
  public void testSimpleTCParser() throws Exception {

    URL resource = Thread.currentThread().getContextClassLoader().getResource("tc-configuration-1.xml");
    TcConfiguration conf = TCConfigurationParser.parse(resource);
    TcConfig tcConfig = conf.getTcConfig();

    assertThat("servers should not be secured", tcConfig.getServers().isSecure(), is(false));
    assertThat("servers client reconnect window should be 200", tcConfig.getServers().getClientReconnectWindow(), is(200));

    List<Server> servers = tcConfig.getServers().getServer();
    assertThat("Server configuration should not be null", servers, notNullValue());
    assertThat("Number of servers should be 1", servers.size(), is(1));

    Server s = servers.get(0);

    assertThat("Server hostname should be hostname", s.getHost(), is("hostname"));
    assertThat("Server bind port should be 0.0.0.0", s.getBind(), is("0.0.0.0"));
    assertThat("Server name should be hostname1", s.getName(), is("hostname1"));

    assertThat("Server log location should be mylog", s.getLogs(), is("mylog"));

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

    List<?> serviceConfigurations = conf.getServiceConfigurations();
    assertThat("service configuration should not be null", serviceConfigurations, notNullValue());
    assertThat(serviceConfigurations.get(0), is(FooServiceConfigurationParser.parsedObject));
  }
}
