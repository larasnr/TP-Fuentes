package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.dto.MensajeDTO;          // tu DTO
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeHecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestMensajeriaController {

    private final MensajeriaManager manager;

    public TestMensajeriaController(MensajeriaManager manager) {
        this.manager = manager;
    }

    @PostMapping(path = "/publicaciones", consumes = "application/json")
    public ResponseEntity<?> publish(@RequestBody MensajeDTO body) {
        if (!manager.isConfigurado())
            return ResponseEntity.status(503).body("Mensajería deshabilitada en esta instancia");

        // validaciones mínimas
        if (body.getExternalId() == null || body.getExternalId().isBlank())
            return ResponseEntity.badRequest().body("externalId es requerido");
        if (body.getColeccionNombre() == null || body.getColeccionNombre().isBlank())
            return ResponseEntity.badRequest().body("coleccionNombre es requerido");

        var msg = MensajeHecho.fromDTO(body);
        manager.publicar(msg);
        return ResponseEntity.accepted().build();
    }
}
