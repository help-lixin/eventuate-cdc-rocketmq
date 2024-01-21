package help.lixin.eventuate.tram.cdc.connector.configuration;

import help.lixin.eventuate.cdc.producer.wrappers.rocketmq.EventuateRocketMQDataProducerWrapper;
import help.lixin.eventuate.messaging.rocketmq.common.EventuateRocketMQConfigurationProperties;
import help.lixin.eventuate.messaging.rocketmq.producer.EventuateRocketMQProducer;
import help.lixin.eventuate.messaging.rocketmq.producer.EventuateRocketMQProducerConfigurationProperties;
import help.lixin.eventuate.messaging.rocketmq.producer.IDefaultMQProducerCustomizer;
import help.lixin.eventuate.messaging.rocketmq.spring.basic.consumer.EventuateRocketMQConsumerSpringConfigurationPropertiesConfiguration;
import help.lixin.eventuate.messaging.rocketmq.spring.common.EventuateRocketMQPropertiesConfiguration;
import help.lixin.eventuate.messaging.rocketmq.spring.producer.EventuateRocketMQProducerSpringConfigurationPropertiesConfiguration;
import help.lixin.eventuate.tram.cdc.connector.configuration.condition.RocketMQCondition;
import io.eventuate.cdc.producer.wrappers.DataProducerFactory;
import io.eventuate.common.eventuate.local.BinlogFileOffset;
import io.eventuate.local.common.PublishingFilter;
import io.eventuate.local.mysql.binlog.DebeziumBinlogOffsetKafkaStore;
import io.eventuate.local.unified.cdc.pipeline.dblog.common.factory.OffsetStoreFactory;
import io.eventuate.local.unified.cdc.pipeline.dblog.mysqlbinlog.factory.DebeziumOffsetStoreFactory;
import io.eventuate.tram.cdc.connector.JdbcOffsetStore;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;

@Configuration
@Import({EventuateRocketMQPropertiesConfiguration.class, //
        EventuateRocketMQProducerSpringConfigurationPropertiesConfiguration.class, //
        EventuateRocketMQConsumerSpringConfigurationPropertiesConfiguration.class, //
})
@Conditional(RocketMQCondition.class)
public class RocketMQMessageTableChangesToDestinationsConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "eventuateRocketMQProducer")
    public EventuateRocketMQProducer eventuateRocketMQProducer(EventuateRocketMQConfigurationProperties eventuateRocketMQConfigurationProperties, //
                                                               EventuateRocketMQProducerConfigurationProperties eventuateRocketMQProducerConfigurationProperties, //
                                                               List<IDefaultMQProducerCustomizer> defaultMQProducerCustomizers) throws Exception {
        return new EventuateRocketMQProducer(eventuateRocketMQConfigurationProperties.getNameServerAddress(), //
                eventuateRocketMQProducerConfigurationProperties, //
                defaultMQProducerCustomizers);
    }


    @Bean
    @ConditionalOnMissingBean(name = "rocketMQDataProducerFactory")
    public DataProducerFactory rocketMQDataProducerFactory(EventuateRocketMQProducer eventuateRocketMQProducer, //
                                                           MeterRegistry meterRegistry) {
        return () -> new EventuateRocketMQDataProducerWrapper(eventuateRocketMQProducer, meterRegistry);
    }


    @Bean
    @ConditionalOnMissingBean(name = "rocketMQOffsetStoreFactory")
    public OffsetStoreFactory rocketMQOffsetStoreFactory() {
        return (roperties, dataSource, eventuateSchema, clientName) -> //
                new JdbcOffsetStore(clientName, new JdbcTemplate(dataSource), eventuateSchema);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rocketMQDuplicatePublishingDetector")
    public PublishingFilter rocketMQDuplicatePublishingDetector() {
        return (fileOffset, topic) -> true;
    }


    @Bean
    @ConditionalOnMissingBean(name = "emptyDebeziumOffsetStoreFactory")
    public DebeziumOffsetStoreFactory emptyDebeziumOffsetStoreFactory() {

        return () -> new DebeziumBinlogOffsetKafkaStore(null, null) {
            @Override
            public Optional<BinlogFileOffset> getLastBinlogFileOffset() {
                return Optional.empty();
            }
        };
    }
}