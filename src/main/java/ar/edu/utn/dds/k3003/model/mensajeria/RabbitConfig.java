package ar.edu.utn.dds.k3003.model.mensajeria;

import ar.edu.utn.dds.k3003.app.Fachada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class RabbitConfig {

    @Bean
    public MensajeriaBroker rabbitBus(
            @Value("${amqp.uri:${RMQ_AMQP_URI:}}") String uri,
            @Value("${amqp.exchange:fuentes.exchange}") String exchange) {
        return new RabbitMqAdapter(uri, exchange);
    }

    @Bean
    public MensajeriaCanal canal(MensajeriaBroker bus, Fachada fachada) {
        MensajeriaCanal canal = new MensajeriaCanal(bus, "fuentes.hechos.nuevos", fachada);
        canal.suscribir();
        return canal;
    }
}
