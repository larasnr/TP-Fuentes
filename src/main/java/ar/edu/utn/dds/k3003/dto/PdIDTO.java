package ar.edu.utn.dds.k3003.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PdIDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas,
                     String imagenUrl) {

    public PdIDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String contenido, List<String> etiquetas, String imagenUrl) {
        this.id = id;
        this.hechoId = hechoId;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.momento = momento;
        this.contenido = contenido;
        this.etiquetas = etiquetas;
        this.imagenUrl = imagenUrl;
    }

    /*public PdIDTO(Object id, Object hechoId, Object contenido, Object hechoId1, Object ubicacion, Object contenido1, Object contenido2, Object etiquetas, Object contenido3) {
    }*/

    public String id() {
        return this.id;
    }

    public String hechoId() {
        return this.hechoId;
    }

    public String descripcion() {
        return this.descripcion;
    }

    public String lugar() {
        return this.lugar;
    }

    public LocalDateTime momento() {
        return this.momento;
    }

    public String contenido() {
        return this.contenido;
    }

    public List<String> etiquetas() {
        return this.etiquetas;
    }

    public String imagenUrl() {return this.imagenUrl;}
}
