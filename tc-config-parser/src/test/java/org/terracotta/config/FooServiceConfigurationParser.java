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

import com.example.foo.Foo;
import org.terracotta.config.service.ServiceConfigParser;
import org.terracotta.config.util.DefaultSubstitutor;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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

  public static final class FooServiceProviderConfiguration implements  ServiceProviderConfiguration {

    private final Foo foo;

    public FooServiceProviderConfiguration(Foo foo) {
      this.foo = foo;
    }

    public Foo getFoo() {
      return foo;
    }

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
    Foo foo = null;
    try {
      JAXBContext jc = JAXBContext.newInstance("com.example.foo:org.terracotta.config");
      Unmarshaller u = jc.createUnmarshaller();
      foo = u.unmarshal(fragment, Foo.class).getValue();
      DefaultSubstitutor.applyDefaults(foo);
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    return new FooServiceProviderConfiguration(foo);
  }
}
