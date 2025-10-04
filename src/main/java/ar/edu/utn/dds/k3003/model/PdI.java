package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PdI {

    private String id;
    private String hechoId;
    private String contenido;
    private List<String> etiquetas;
    private boolean procesado;
    private LocalDateTime fechaProcesamiento;

    public PdI(String id, String hechoId, String contenido, List<String> etiquetas, boolean procesado) {
        if (hechoId == null) {
            throw new IllegalArgumentException("El id de Hecho no puede ser nulo.");
        }
        this.id = id;
        this.hechoId = hechoId;
        this.contenido = contenido;
        this.etiquetas = etiquetas;
        this.procesado = procesado;
        this.fechaProcesamiento = LocalDateTime.now();
    }

    public PdI() {

    }

    // Versión con baseUrl pasada por parámetro (si preferís @Value, omití el parámetro)
    public PdIDTO crearPdi(String hechoId, String imageUrl, String baseUrl) {
        return WebClient.create(baseUrl)
                .post()
                .uri("/pdi")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        // ojo con el contrato del otro servicio:
                        // si espera snake_case, mandá "hecho_id"; si espera camelCase, usá "hechoId".
                        "hecho_id", hechoId,
                        "contenido", imageUrl
                ))
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("PDI error %s: %s".formatted(resp.statusCode(), body))))
                )
                .bodyToMono(PdIDTO.class)
                .block(); // bloqueante (simple)
    }

    // Si igual querés el id nomás, te dejo el helper:
    public String crearPdiYDevolverId(String hechoId, String imageUrl, String baseUrl) {
        PdIDTO dto = crearPdi(hechoId, imageUrl, baseUrl);
        return dto != null ? dto.id() : null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHechoId(String hechoId) {
        this.hechoId = hechoId;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public void setProcesado(boolean procesado) {
        this.procesado = procesado;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
}