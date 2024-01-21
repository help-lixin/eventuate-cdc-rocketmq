package help.lixin.eventuate.cdc.producer.wrappers.rocketmq;

import help.lixin.eventuate.messaging.rocketmq.producer.EventuateRocketMQProducer;
import io.eventuate.cdc.producer.wrappers.DataProducer;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class EventuateRocketMQDataProducerWrapper implements DataProducer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private EventuateRocketMQProducer eventuateRocketMQProducer;
    private MeterRegistry meterRegistry;

    public EventuateRocketMQDataProducerWrapper(EventuateRocketMQProducer eventuateRocketMQProducer, //
                                                MeterRegistry meterRegistry) {
        this.eventuateRocketMQProducer = eventuateRocketMQProducer;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public CompletableFuture<?> send(String topic, String key, String body) {
        return eventuateRocketMQProducer.send(topic, key, body);
    }

    @Override
    public void close() {
        logger.info("closing EventuateRocketMQProducerWrapper");
        eventuateRocketMQProducer.close();
        logger.info("closed EventuateRocketMQProducerWrapper");
    }
}
