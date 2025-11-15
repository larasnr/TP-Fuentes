package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.FachadaFuente;
import ar.edu.utn.dds.k3003.dto.HechoDTO;
import ar.edu.utn.dds.k3003.dto.PdIDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PdIController {
    private final FachadaFuente fachada;
    private final JpaHechoRepository hechos;
    private final MeterRegistry meterRegistry;

    @Autowired
    public PdIController(FachadaFuente fachada, JpaHechoRepository hechos, MeterRegistry meterRegistry) {
        this.fachada = fachada;
        this.hechos = hechos;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/pdis")
    public ResponseEntity<PdIDTO> crear(@RequestBody PdIDTO dto) {
        try {
            meterRegistry.counter("Fuentes.POST.hechos").increment();
            return ResponseEntity.ok(fachada.agregar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build();
        }
    }
}
