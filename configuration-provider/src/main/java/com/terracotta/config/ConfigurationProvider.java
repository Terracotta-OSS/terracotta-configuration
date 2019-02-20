package com.terracotta.config;

import java.util.List;
import java.util.ServiceLoader;

/**
 * A {@link ConfigurationProvider} provides the configuration for the server start-up
 */

public interface ConfigurationProvider {

  default ConfigurationProvider get() {
    ConfigurationProvider configurationProvider = null;
    for (ConfigurationProvider candidate : ServiceLoader.load(ConfigurationProvider.class)) {
      if (configurationProvider == null) {
        configurationProvider = candidate;
      } else {
        String errorMessage = String.format(
            "Multiple implementations found for service '%s': '%s' and '%s'",
            ConfigurationProvider.class,
            configurationProvider.getClass(),
            candidate.getClass()
        );
        throw new RuntimeException(errorMessage);
      }
    }

    if (configurationProvider == null) {
      throw new RuntimeException("No implementation found for service " + ConfigurationProvider.class);
    }

    return configurationProvider;
  }

  /**
   * Initializes this {@link ConfigurationProvider} using the given configuration parameters
   *
   * <p>Typically configuration parameters are command-line arguments passed during server start-up.</p>
   *
   * @param configurationParams list of configuration parameters supported by this {@link ConfigurationProvider}
   * @throws ConfigurationException if any issues during initialization
   *
   * @see #getConfigurationParamsDescription()
   */
  void initialize(List<String> configurationParams) throws ConfigurationException;

  /**
   * Returns the latest server configuration
   *
   * @return latest {@link Configuration}
   */
  Configuration getConfiguration();

  /**
   * Provides the description for configuration params supported by this {@link Configuration}
   *
   * @return the description for configuration params
   *
   * @see #initialize(List)
   */
  String getConfigurationParamsDescription();

  /**
   * closes this {@link ConfigurationProvider}
   */
  void close();
}