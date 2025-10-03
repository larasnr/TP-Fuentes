package ar.edu.utn.dds.k3003.model.mensajeria;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
public class RabbitConfig {

    @Bean
    public MensajeriaBroker noOpBroker() {
        return new NoOpMensajeriaBroker();
    }

    @Bean
    @Primary
    @Conditional(HasAmqpUri.class)
    public MensajeriaBroker rabbitBus(
            @Value("${amqp.uri:${RMQ_AMQP_URI}}") String uri,
            @Value("${amqp.exchange:fuentes.exchange}") String exchange) {
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException("Falta amqp.uri/AMQP_URI (amqps://USER:PASS@HOST/VHOST)");
        }
        return new RabbitMqAdapter(uri, exchange);
    }

    static class HasAmqpUri implements Condition {
        @Override public boolean matches(ConditionContext c, AnnotatedTypeMetadata m) {
            var env = c.getEnvironment();
            String uri = env.getProperty("amqp.uri", env.getProperty("RMQ_AMQP_URI", ""));
            return uri != null && !uri.isBlank();
        }
    }
}
