package ar.edu.utn.dds.k3003.model.mensajeria;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MensajeriaAutoStart {

    @Bean
    public ApplicationRunner startMensajeria(MensajeriaManager manager,
                                             @Value("${mensajeria.enabled:false}") boolean enabled) {
        return args -> {
            if (enabled && manager.isConfigurado()) {
                manager.activar();
            } else {
                System.out.println("[AMQP] Mensajería no activada. enabled=" + enabled +
                        " configurado=" + manager.isConfigurado());
            }
        };
    }
}
