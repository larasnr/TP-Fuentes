package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.dto.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.model.Hecho;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class PDIController {
    private final FachadaFuente fachada;
    private final JpaHechoRepository hechos;
    private final MeterRegistry meterRegistry;
    private WebClient web;

    @Autowired
    public PDIController(FachadaFuente fachada, JpaHechoRepository hechos, MeterRegistry meterRegistry, @Value("${pdi.url}") String baseUrl) {
        this.fachada = fachada;
        this.hechos = hechos;
        this.meterRegistry = meterRegistry;
        this.web = WebClient.create(baseUrl);
    }

    @PostMapping(path = "/pdis", consumes = "application/json")
    @Transactional
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        // 1) Leer y validar los campos
        Object hId = body.get("hechoId");              // nombre EXACTO esperado en el JSON
        Object contenido = body.get("contenido");      // URL de imagen

        if (!(hId instanceof String hechoId) || hechoId.isBlank()) {
            return ResponseEntity.badRequest().body("hechoId es requerido y no puede ser vacío");
        }
        if (!(contenido instanceof String url) || url.isBlank()) {
            return ResponseEntity.badRequest().body("contenido (URL) es requerido y no puede ser vacío");
        }

        // 2) Buscar el Hecho (asegurate que el tipo del id coincida con tu entidad)
        Hecho h = hechos.findById(hechoId)
                .orElseThrow(() -> new NoSuchElementException("Hecho no encontrado: " + hechoId));

        // 3) Llamar al servicio PDI (POST /pdi) y leer respuesta como Map para no crear DTOs
        Map<?,?> resp = web.post().uri("/pdis")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("hecho_id", h.getId(), "contenido", url)) // ojo con snake/camel en la otra app
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null || !resp.containsKey("id") || !(resp.get("id") instanceof Number n)) {
            return ResponseEntity.internalServerError().body("No se pudo crear PDI (respuesta sin id)");
        }

        Long pdiId = n.longValue();

        // 4) Guardar el id de PDI en el Hecho (si usás un único pdiId)
        h.agregarPdI(String.valueOf(pdiId));
        hechos.save(h);

        return ResponseEntity.ok(Map.of("hechoId", h.getId(), "pdiId", pdiId));
    }

}