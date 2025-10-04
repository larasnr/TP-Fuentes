package ar.edu.utn.dds.k3003.dto;

import ar.edu.utn.dds.k3003.facades.dtos.CategoriaHechoEnum;

import java.time.LocalDateTime;
import java.util.List;

public record HechoDTO(String id, String nombreColeccion, String titulo, List<String> etiquetas, CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen, String destino) {
    public HechoDTO(String id, String nombreColeccion, String titulo) {
        this(id, nombreColeccion, titulo, (List)null, (CategoriaHechoEnum)null, (String)null, (LocalDateTime)null, (String)null, (String)null);
    }

    public HechoDTO(String id, String nombreColeccion, String titulo, List<String> etiquetas, CategoriaHechoEnum categoria, String ubicacion, LocalDateTime fecha, String origen, String destino) {
        this.id = id;
        this.nombreColeccion = nombreColeccion;
        this.titulo = titulo;
        this.etiquetas = etiquetas;
        this.categoria = categoria;
        this.ubicacion = ubicacion;
        this.fecha = fecha;
        this.origen = origen;
        this.destino = destino;
    }

    public String id() {
        return this.id;
    }

    public String nombreColeccion() {
        return this.nombreColeccion;
    }

    public String titulo() {
        return this.titulo;
    }

    public List<String> etiquetas() {
        return this.etiquetas;
    }

    public CategoriaHechoEnum categoria() {
        return this.categoria;
    }

    public String ubicacion() {
        return this.ubicacion;
    }

    public LocalDateTime fecha() {
        return this.fecha;
    }

    public String origen() {
        return this.origen;
    }

}

