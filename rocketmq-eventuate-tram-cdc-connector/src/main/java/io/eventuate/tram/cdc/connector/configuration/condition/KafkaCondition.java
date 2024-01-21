package io.eventuate.tram.cdc.connector.configuration.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 需要改动CDC的源码,增加RocketMQ这一行
 */
public class KafkaCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !context.getEnvironment().acceptsProfiles("ActiveMQ") &&
                !context.getEnvironment().acceptsProfiles("RabbitMQ") &&
                // TODO lixin
                !context.getEnvironment().acceptsProfiles("RocketMQ") &&
                !context.getEnvironment().acceptsProfiles("Redis");
    }
}
