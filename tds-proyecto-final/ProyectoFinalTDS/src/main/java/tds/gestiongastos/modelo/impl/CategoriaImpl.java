package tds.gestiongastos.modelo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import tds.gestiongastos.modelo.Categoria;

public class CategoriaImpl implements Categoria {

	@JsonProperty("nombre")
	private String nombre;

	@JsonProperty("descripcion")
	private String descripcion;

	public CategoriaImpl() {
	}

	public CategoriaImpl(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	@Override
	public String getNombre() {
		return nombre;
	}

	@Override
	public String getDescripcion() {
		return descripcion;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return nombre;
	}
}