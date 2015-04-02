package org.terracotta.config;


import javax.xml.bind.JAXBException;

/**
 * Exception thrown incase of any misconfiguration in the TC Configuration or service configuration.
 */
public class TCConfigurationSetupException extends RuntimeException {
  public TCConfigurationSetupException(Exception e) {
    super(e);
  }

  public TCConfigurationSetupException(String message) {
    super(message);
  }

  public TCConfigurationSetupException(String message, Exception e) {
    super(message, e);
  }
}
