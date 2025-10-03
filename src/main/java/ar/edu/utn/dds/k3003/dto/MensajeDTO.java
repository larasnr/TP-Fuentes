package ar.edu.utn.dds.k3003.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)   // ignora campos extra
public class MensajeDTO {

    private int version;

    @JsonProperty("externalId")
    private String externalId;

    private String titulo;
    private String descripcion;

    @JsonProperty("coleccionNombre")
    private String coleccionNombre;

    private String imageUrl;
    private String origen;

    private String fecha;

    public MensajeDTO() {}

    public MensajeDTO(int version, String externalId, String titulo, String descripcion,
                      String coleccionNombre, String imageUrl, String origen, String fecha) {
        this.version = version;
        this.externalId = externalId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.coleccionNombre = coleccionNombre;
        this.imageUrl = imageUrl;
        this.origen = origen;
        this.fecha = fecha;
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

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
