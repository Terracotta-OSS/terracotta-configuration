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

import com.example.bar.Bar;
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
import org.terracotta.config.service.ExtendedConfigParser;

public class BarConfigurationParser implements ExtendedConfigParser {
  private static final URI NAMESPACE = URI.create("http://www.example.com/bar");
  private static final URL XML_SCHEMA = BarConfigurationParser.class.getResource("/bar.xsd");

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
    Bar foo = null;
    try {
      JAXBContext jc = JAXBContext.newInstance("com.example.bar:org.terracotta.config");
      Unmarshaller u = jc.createUnmarshaller();
      foo = u.unmarshal(fragment, Bar.class).getValue();
      DefaultSubstitutor.applyDefaults(foo);
    } catch (JAXBException e) {
      e.printStackTrace();
    }

    return foo.getName();
  }
}
