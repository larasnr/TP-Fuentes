package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.app.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.dto.PdIDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


public class FachadaPdI implements FachadaProcesadorPdI {
    private final String url;
    public FachadaPdI(@Value("${pdi.url:${PDI_URL}}") String url) {
        this.url = url;
    }
    @Override
    public PdIDTO procesar(PdIDTO pdIDTO) throws IllegalStateException {
        WebClient pdiClient = WebClient.builder().baseUrl(this.url).build();
        Map<String, Object> body = Map.of(
                "id", pdIDTO.id(),
                "hechoId", pdIDTO.hechoId(),
                "contenido", pdIDTO.contenido()
                //,"imagenUrl", pdIDTO
        );

        Map res = pdiClient.post()
                .uri("/pdis")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        PdIDTO pdIdTO = new PdIDTO(
        String.valueOf(res.get("id")),
                String.valueOf(res.get("hechoId")),
                null, null, null, null, null,
                String.valueOf(res.get("imagen_url")));

            return pdIdTO;
    }

    @Override
    public PdIDTO buscarPdIPorId(String hecho) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String hecho) throws NoSuchElementException {
        WebClient pdiClient = WebClient.builder().baseUrl(this.url).build();

        List<Map> pdis = pdiClient.get()
                .uri("/pdis?hecho={hecho}", hecho)
                .retrieve()
                .bodyToFlux(Map.class)
                .collectList()
                .block();

        List<PdIDTO> pdIdTOS = new ArrayList<>();

        pdis.forEach(p -> pdIdTOS.add(
                new PdIDTO(
                (String) p.get("id")
                ,(String)p.get("hecho_id")
                ,(String)p.get("texto_extraido")
                ,(String)p.get("ubicacion")
                ,LocalDateTime.now()
                ,(String)p.get("contenido")
                ,(List) p.get("etiquetas")
                ,(String)p.get("contenido")
        )));

        return pdIdTOS;
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes var1) {
    }


}
