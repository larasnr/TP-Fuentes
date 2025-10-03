package ar.edu.utn.dds.k3003.mensajeria;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.app.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.model.mensajeria.InMemoryBus;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeHecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaBroker;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaCanal;
import ar.edu.utn.dds.k3003.repository.HechoRepository;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import ar.edu.utn.dds.k3003.dto.MensajeDTO;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MensajeriaTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JpaHechoRepository hechoRepo;

    @Test
    void testSuscripcionPersisteHecho() {

        ColeccionDTO bodyC = new ColeccionDTO("politica","d");
        restTemplate.postForEntity("/colecciones", bodyC, Void.class);

        MensajeDTO body = new MensajeDTO();
        body.setVersion(1);
        body.setExternalId("test:1");
        body.setTitulo("Corte");
        body.setDescripcion("En el centro");
        body.setColeccionNombre("politica");

        restTemplate.postForEntity("/publicaciones", body, Void.class);

        // esperar un poquito por async
        Awaitility.await().atMost(Duration.ofSeconds(2))
                .until(() -> hechoRepo.findByExternalId("test:1").isPresent());
    }

    @Test
    void testCanalSuscribeYDisparaOnMessage() {
        MensajeriaBroker bus = new InMemoryBus();

        // Fachada fake para verificar invocación
        var fachada = Mockito.mock(Fachada.class);

        MensajeriaCanal canal = new MensajeriaCanal(bus, "fuentes.hechos.nuevos", fachada);
        canal.suscribir();

        MensajeHecho msg = new MensajeHecho();
        msg.setExternalId("x:1");
        msg.setTitulo("Prueba");
        msg.setColeccionNombre("test");

        canal.publicar(msg);

        Mockito.verify(fachada, Mockito.timeout(1000)).onMessage(Mockito.any());
    }

}
