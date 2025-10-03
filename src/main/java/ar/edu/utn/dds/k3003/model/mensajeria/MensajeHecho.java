// ar/edu/utn/dds/k3003/model/mensajeria/MensajeHecho.java
package ar.edu.utn.dds.k3003.model.mensajeria;

import ar.edu.utn.dds.k3003.dto.MensajeDTO;
import ar.edu.utn.dds.k3003.model.Hecho;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

public class MensajeHecho {
    private int version = 1;
    private String externalId;
    private String titulo;
    private String descripcion;
    private String coleccionNombre;
    private String imageUrl;
    private String origen;
    private LocalDateTime fecha;

    public static MensajeHecho fromDTO(MensajeDTO d) {
        MensajeHecho m = new MensajeHecho();
        m.setVersion(d.getVersion());
        m.setExternalId(d.getExternalId());
        m.setTitulo(d.getTitulo());
        m.setDescripcion(d.getDescripcion());
        m.setColeccionNombre(d.getColeccionNombre());
        m.setImageUrl(d.getImageUrl());
        m.setOrigen(d.getOrigen());
        if (d.getFecha() != null && !d.getFecha().isBlank()) {
            m.setFecha(LocalDateTime.parse(d.getFecha())); // ISO-8601
        }
        return m;
    }

    //Conversión sin PDI
    public Hecho toHecho() {
        Hecho h = new Hecho();
        h.setTitulo(titulo);
        h.setDescripcion(descripcion);
        h.setExternalId(externalId);
        h.setOrigen(origen);
        h.setFecha(fecha);
        return h;
    }

    public static MensajeHecho fromBytes(byte[] payload) {
        try { return new ObjectMapper().readValue(payload, MensajeHecho.class); }
        catch (Exception e) { throw new RuntimeException("Error deserializando MensajeHecho", e); }
    }
    public byte[] toBytes() {
        try { return new ObjectMapper().writeValueAsBytes(this); }
        catch (Exception e) { throw new RuntimeException("Error serializando MensajeHecho", e); }
    }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getColeccionNombre() { return coleccionNombre; }
    public void setColeccionNombre(String coleccionNombre) { this.coleccionNombre = coleccionNombre; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; } // ← por si no estaba
}
