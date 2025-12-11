package tds.gestiongastos.modelo.impl;

import java.time.LocalDate;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;

public class GastoImpl implements Gasto {

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("cantidad")
    private double cantidad;

    @JsonProperty("fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate fecha;

    @JsonProperty("categoria")
    @JsonDeserialize(as = CategoriaImpl.class)
    private CategoriaImpl categoria; 

    public GastoImpl() {
        this.id = UUID.randomUUID().toString();
    }

    public GastoImpl(String descripcion, double cantidad, LocalDate fecha, CategoriaImpl categoria) {
        this.id = UUID.randomUUID().toString();
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    @Override public String getId() { return id; }

    public void setId(String id) { 
        this.id = id; 
    }

    @Override public String getDescripcion() { return descripcion; }
    @Override public double getCantidad() { return cantidad; }
    @Override public LocalDate getFecha() { return fecha; }
    @Override public Categoria getCategoria() { return categoria; }

    @Override public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    @Override public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    @Override public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    @Override
    public void setCategoria(Categoria categoria) {
        if (categoria instanceof CategoriaImpl) {
            this.categoria = (CategoriaImpl) categoria;
        }
    }
}