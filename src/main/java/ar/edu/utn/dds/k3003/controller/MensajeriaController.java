package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.dto.MensajeDTO;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeHecho;
import ar.edu.utn.dds.k3003.model.mensajeria.MensajeriaCanal;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MensajeriaController {

    private final MensajeriaCanal canal;
    private final JpaHechoRepository hechoRepo;

    public MensajeriaController(MensajeriaCanal canal, JpaHechoRepository hechoRepo) {
        this.canal = canal;
        this.hechoRepo = hechoRepo;
    }

    @PostMapping(path = "/publicaciones", consumes = "application/json")
    public ResponseEntity<?> publish(@RequestBody MensajeDTO body) {
        if (body.getExternalId() == null || body.getExternalId().isBlank())
            return ResponseEntity.badRequest().body("externalId es requerido");
        if (body.getColeccionNombre() == null || body.getColeccionNombre().isBlank())
            return ResponseEntity.badRequest().body("coleccionNombre es requerido");

        //Se construye mensaje a publicar
        MensajeHecho msg = MensajeHecho.fromDTO(body);
        canal.publicar(msg);

        //Se lee el hecho creado para después devolver
        Hecho creado = hechoRepo.findByExternalId(body.getExternalId())
                .orElseThrow(() -> new IllegalStateException(
                        "Se publicó el mensaje pero no se encontró el Hecho (externalId=" + body.getExternalId() + ")"));

        return ResponseEntity.ok(creado);
    }
}
