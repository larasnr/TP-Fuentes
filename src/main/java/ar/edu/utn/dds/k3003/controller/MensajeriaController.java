package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.dto.MensajeDTO;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeHecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaCanal;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaManager;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/suscripciones")
public class MensajeriaController {

    private final MensajeriaManager manager;

    public MensajeriaController(MensajeriaManager manager) {
        this.manager = manager;
    }

    @GetMapping
    public Map<String, Object> estado() {
        return Map.of(
                "topic", manager.getTopic(),
                "configurado", manager.isConfigurado(),
                "activado", manager.isActivo()
        );
    }

    @PostMapping
    public ResponseEntity<?> toggle(@RequestParam boolean activado) {
        if (!manager.isConfigurado())
            return ResponseEntity.status(503).body("Mensajería no configurada");
        if (activado) manager.activar(); else manager.desactivar();
        return ResponseEntity.ok(Map.of("activado", manager.isActivo()));
    }
}

