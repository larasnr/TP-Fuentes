package ar.edu.utn.dds.k3003.model.mensajeria;

import ar.edu.utn.dds.k3003.app.Fachada;
import org.springframework.context.annotation.*;

@Configuration
public class MensajeriaConfig {

    @Bean
    public MensajeriaBroker inMemoryBus() { return new InMemoryBus(); }

    @Bean
    public MensajeriaCanal canal(MensajeriaBroker bus, Fachada fachada) {
        MensajeriaCanal canal = new MensajeriaCanal(bus, "fuentes.hechos.nuevos", fachada);
        canal.suscribir();
        return canal;
    }
}
