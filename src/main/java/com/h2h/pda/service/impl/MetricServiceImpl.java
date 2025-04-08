package com.h2h.pda.service.impl;

import com.h2h.pda.pojo.metric.CounterMetric;
import com.h2h.pda.pojo.metric.Counters;
import com.h2h.pda.pojo.metric.Gauges;
import com.h2h.pda.service.api.MetricService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.h2h.pda.pojo.metric.Counters.*;

@Service
public class MetricServiceImpl implements MetricService {

    @Autowired
    MeterRegistry meterRegistry;

    Map<Counters, CounterMetric> counters = new HashMap<>();

    Map<Gauges, Gauge> gaugeMap = new HashMap<>();

    @Value("${service.commit-id}")
    String commitId;

    @PostConstruct
    public void init() {
        new CounterMetric(VERSION, "id", meterRegistry).increment(commitId);

        counters.put(COMMAND_COUNTS, new CounterMetric(COMMAND_COUNTS, "id", meterRegistry));
        counters.put(EMAIL_COUNTS, new CounterMetric(EMAIL_COUNTS, "id", meterRegistry));
        counters.put(ALARM_COUNTS, new CounterMetric(ALARM_COUNTS, meterRegistry));
        counters.put(PASSWORD_JOB_COUNTS, new CounterMetric(PASSWORD_JOB_COUNTS, meterRegistry));

        counters.put(LOGIN_ATTEMPT_COUNT, new CounterMetric(LOGIN_ATTEMPT_COUNT, "id", meterRegistry));
        counters.put(LOGIN_SUCCESS_COUNT, new CounterMetric(LOGIN_ATTEMPT_COUNT, "id", meterRegistry));
        counters.put(LOGIN_FAIL_COUNT, new CounterMetric(LOGIN_ATTEMPT_COUNT, "id", meterRegistry));
        counters.put(SYSTEM_STATUS_COUNT, new CounterMetric(SYSTEM_STATUS_COUNT, "id", meterRegistry));
    }

    @Override
    public CounterMetric getCounter(Counters name) {
        return counters.get(name);
    }

    @Override
    public Gauge gauge(Gauges gaues, Supplier<Number> f) {
        if (!gaugeMap.containsKey(gaues)) {
            gaugeMap.put(gaues, Gauge.builder(gaues.getName(), f).register(meterRegistry));
        }

        return gaugeMap.get(gaues);
    }

}
