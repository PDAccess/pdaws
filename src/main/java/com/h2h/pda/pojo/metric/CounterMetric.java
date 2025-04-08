package com.h2h.pda.pojo.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.HashMap;
import java.util.Map;

public class CounterMetric {
    String name;
    String tagName;
    MeterRegistry registry;
    Map<String, Counter> taggedCounters = new HashMap<>();
    Counter counter;

    public CounterMetric(Counters counter, MeterRegistry registry) {
        this.name = counter.metricName();
        this.registry = registry;
    }

    public CounterMetric(Counters counter, String tagName, MeterRegistry registry) {
        this.name = counter.metricName();
        this.tagName = tagName;
        this.registry = registry;
    }

    public void increment() {
        increment(1);
    }

    public void increment(double amount) {
        if (this.counter == null) {
            this.counter = Counter.builder(name).register(registry);
        }
        this.counter.increment(amount);
    }

    public void increment(String tagValue) {
        increment(tagValue, 1);
    }

    public void increment(String tagValue, double amount) {
        Counter count = taggedCounters.computeIfAbsent(tagValue, k -> Counter.builder(name).tags(tagName, tagValue).register(registry));
        count.increment(amount);
    }
}
