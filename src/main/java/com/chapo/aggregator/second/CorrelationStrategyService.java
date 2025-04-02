package com.chapo.aggregator.second;

import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class CorrelationStrategyService implements CorrelationStrategy {

    @Override
    public Object getCorrelationKey(Message<?> message) {
        return 0;
    }
}
