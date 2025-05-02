package com.chapo.aggregator.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(name = "application.aggregator.enabled", havingValue = "false")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OnAggregatorDisabled {

}
