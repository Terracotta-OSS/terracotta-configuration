package org.terracotta.config;

public interface TCConfigDefaults {
  short GROUPPORT_OFFSET_FROM_TSAPORT = 20;

  int TSA_PORT = 9410;
  int GROUP_PORT = TSA_PORT + GROUPPORT_OFFSET_FROM_TSAPORT;
}
