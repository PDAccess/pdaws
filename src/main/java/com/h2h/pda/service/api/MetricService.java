package com.h2h.pda.service.api;

import com.h2h.pda.pojo.metric.CounterMetric;
import com.h2h.pda.pojo.metric.Counters;
import com.h2h.pda.pojo.metric.Gauges;
import io.micrometer.core.instrument.Gauge;

import java.util.function.Supplier;

public interface MetricService {
    CounterMetric getCounter(Counters counters);

    Gauge gauge(Gauges gaues, Supplier<Number> f);
}
