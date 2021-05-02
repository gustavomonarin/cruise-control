/*
 * Copyright 2020 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor.sampling.prometheus;

import com.linkedin.cruisecontrol.common.CruiseControlConfigurable;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.RawMetricType;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


public class PropertiesFilePrometheusQuerySupplier implements PrometheusQuerySupplier, CruiseControlConfigurable {

  private static final String PROMETHEUS_QUERY_SUPPLIER_PROPERTIES_FILE_CONFIG = "prometheus.query.supplier.properties.file";
  private static final String DEFAULT_PROMETHEUS_QUERY_SUPPLIER_PROPERTIES_FILE_CONFIG = "config/prometheus_queries.properties";

  private final Map<RawMetricType, String> _configuredQueries = new HashMap<>();

  private final Set<RawMetricType> _missedQueries = new HashSet<>();

  @Override
  public void configure(Map<String, ?> configs) {
    String queriesFile = (String) configs.get(PROMETHEUS_QUERY_SUPPLIER_PROPERTIES_FILE_CONFIG);
    if (queriesFile == null) {
      queriesFile = DEFAULT_PROMETHEUS_QUERY_SUPPLIER_PROPERTIES_FILE_CONFIG;
    }

    Properties props = new Properties();
    try (InputStream propStream = new FileInputStream(queriesFile)) {
      props.load(propStream);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (RawMetricType type : RawMetricType.allMetricTypes()) {
      String query = props.getProperty(type.name());
      if (query != null) {
        _configuredQueries.put(type, query);
      } else {
        _missedQueries.add(type);
      }
    }

    if (!_missedQueries.isEmpty()) {

    }
  }

  @Override
  public Map<RawMetricType, String> get() {
    return _configuredQueries;
  }

  Set<RawMetricType> getMissedQueries() {
    return _missedQueries;
  }
}
