package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.app.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.dto.HechoDTO;
import ar.edu.utn.dds.k3003.repository.HechoRepository;
import ar.edu.utn.dds.k3003.repository.JpaHechoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ColeccionController {

    private final FachadaFuente fachada;
    private final JpaHechoRepository hechos;
    private final MeterRegistry meterRegistry;

    @Autowired
    public ColeccionController(FachadaFuente fachada, JpaHechoRepository hechos, MeterRegistry meterRegistry) {
        this.fachada = fachada;
        this.hechos = hechos;
        this.meterRegistry = meterRegistry;
    }

    @GetMapping("/colecciones")
    public ResponseEntity<List<ColeccionDTO>> listar() {
        return ResponseEntity.ok(fachada.colecciones());
    }

    @GetMapping("/colecciones/{nombre}")
    public ResponseEntity<ColeccionDTO> porNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(fachada.buscarColeccionXId(nombre));
    }

    @PostMapping("/colecciones")
    public ResponseEntity<ColeccionDTO> crear(@RequestBody ColeccionDTO dto) {
        try {
            meterRegistry.counter("Fuentes.POST.colecciones").increment();
            return ResponseEntity.ok(fachada.agregar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build(); // o body con el mensaje si los tests lo piden
        }
    }

    @GetMapping("/colecciones/{nombre}/hechos")
    public ResponseEntity<List<HechoDTO>> listarPorColeccion(@PathVariable String nombre) {
        List<HechoDTO> lista = hechos.findByColeccionId(nombre).stream()
                .filter(h -> !h.estaBorrado())
                .map(h -> new HechoDTO(
                        h.getId(), h.getColeccionId(), h.getTitulo(),
                        h.getEtiquetas(), h.getCategoria(), h.getUbicacion(),
                        h.getFecha(), h.getOrigen(),
                        h.getEstado()
                ))
                .toList();
        return ResponseEntity.ok(lista);
    }

}
