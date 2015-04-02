package org.terracotta.config;


import org.terracotta.config.service.ServiceConfigParser;
import org.terracotta.config.util.DefaultSubstitutor;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class TCConfigurationParser {

  private static final SchemaFactory XSD_SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
  private static final URL TERRACOTTA_XML_SCHEMA = TCConfigurationParser.class.getResource("/terracotta.xsd");

  private static final Map<URI, ServiceConfigParser<?>> serviceParsers = new HashMap<>();

  @SuppressWarnings("unchecked")
  private static TcConfiguration parseStream(InputStream in, ErrorHandler eh) throws IOException, SAXException {
    Collection<Source> schemaSources = new ArrayList<>();

    for (ServiceConfigParser<?> parser : loadConfigurationParserClasses()) {
      schemaSources.add(parser.getXmlSchema());
      serviceParsers.put(parser.getNamespace(), parser);
    }
    schemaSources.add(new StreamSource(TERRACOTTA_XML_SCHEMA.openStream()));

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setIgnoringComments(true);
    factory.setIgnoringElementContentWhitespace(true);
    factory.setSchema(XSD_SCHEMA_FACTORY.newSchema(schemaSources.toArray(new Source[schemaSources.size()])));

    final DocumentBuilder domBuilder;
    try {
      domBuilder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      throw new AssertionError(e);
    }
    domBuilder.setErrorHandler(eh);
    final Element config = domBuilder.parse(in).getDocumentElement();

    try {
      JAXBContext jc = JAXBContext.newInstance("org.terracotta.config", TCConfigurationParser.class.getClassLoader());
      Unmarshaller u = jc.createUnmarshaller();

      TcConfig tcConfig = u.unmarshal(config, TcConfig.class).getValue();
      DefaultSubstitutor.applyDefaults(tcConfig);


      ArrayList serviceConfigurations = new ArrayList();
      if (tcConfig.getServices() != null && tcConfig.getServices().getService() != null) {
        //now parse the service configuration.
        for (Service service : tcConfig.getServices().getService()) {
          Element element = service.getAny();
          URI namespace = URI.create(element.getNamespaceURI());
          ServiceConfigParser<?> parser = serviceParsers.get(namespace);
          if (parser == null) {
            throw new TCConfigurationSetupException("Can't find parser for service " + namespace);
          }
          serviceConfigurations.add(parser.parse(element));
        }
      }

      return new TcConfiguration(tcConfig, serviceConfigurations);
    } catch (JAXBException e) {
      throw new TCConfigurationSetupException(e);
    }
  }

  private static TcConfiguration convert(InputStream in) throws IOException, SAXException {
    byte[] data = new byte[in.available()];
    in.read(data);
    in.close();
    ByteArrayInputStream bais = new ByteArrayInputStream(data);

    return parseStream(bais, RethrowErrorHandler.INSTANCE);
  }

  public static TcConfiguration parse(File file) throws IOException, SAXException {
    FileInputStream in = null;

    try {
      in = new FileInputStream(file);
      return convert(in);
    } finally {

      IOUtils.closeQuietly(in);
    }
  }

  public static TcConfiguration parse(String xmlText) throws IOException, SAXException {
    return convert(new ByteArrayInputStream(xmlText.getBytes()));
  }

  public static TcConfiguration parse(InputStream stream) throws IOException, SAXException {
    return convert(stream);
  }

  public static TcConfiguration parse(URL url) throws IOException, SAXException {
    return convert(url.openStream());
  }

  public static TcConfiguration parse(InputStream in, Collection<SAXParseException> errors) throws IOException, SAXException {
    return parseStream(in, new CollectingErrorHandler(errors));
  }

  private static class CollectingErrorHandler implements ErrorHandler {

    private final Collection<SAXParseException> errors;

    CollectingErrorHandler(Collection<SAXParseException> errors) {
      this.errors = errors;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
      errors.add(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      errors.add(exception);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      errors.add(exception);
    }

  }

  private static class RethrowErrorHandler implements ErrorHandler {

    public static final ErrorHandler INSTANCE = new RethrowErrorHandler();

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
      throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      throw exception;
    }
  }

  private static ServiceLoader<ServiceConfigParser> loadConfigurationParserClasses() {
    return ServiceLoader.load(ServiceConfigParser.class,
        TCConfigurationParser.class.getClassLoader());
  }

}
