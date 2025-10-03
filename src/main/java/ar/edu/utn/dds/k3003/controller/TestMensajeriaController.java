// ar/edu/utn/dds/k3003/controller/TestMensajeriaController.java
package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.dto.MensajeDTO;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeHecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaCanal;
import ar.edu.utn.dds.k3003.repository.HechoRepository;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestMensajeriaController {

    private final MensajeriaCanal canal;
    private final JpaHechoRepository hechoRepo;

    public TestMensajeriaController(MensajeriaCanal canal, JpaHechoRepository hechoRepo) {
        this.canal = canal;
        this.hechoRepo = hechoRepo;
    }

    @PostMapping(path = "/publicaciones", consumes = "application/json")
    public ResponseEntity<?> publish(@RequestBody MensajeDTO body) {
        if (body.getExternalId() == null || body.getExternalId().isBlank())
            return ResponseEntity.badRequest().body("externalId es requerido");
        if (body.getColeccionNombre() == null || body.getColeccionNombre().isBlank())
            return ResponseEntity.badRequest().body("coleccionNombre es requerido");

        // 1) Construyo el mensaje y publico (InMemoryBus dispara onMessage sincrónicamente)
        MensajeHecho msg = MensajeHecho.fromDTO(body);
        canal.publicar(msg);

        // 2) Leo el Hecho recién creado (la Fachada lo persistió en onMessage)
        Hecho creado = hechoRepo.findByExternalId(body.getExternalId())
                .orElseThrow(() -> new IllegalStateException(
                        "Se publicó el mensaje pero no se encontró el Hecho (externalId=" + body.getExternalId() + ")"));

        // 3) Devuelvo el Hecho tal cual quedó en BD (id, coleccionId, etc.)
        return ResponseEntity.ok(creado);
    }
}
