package tds.gestiongastos.adapters.repository;

import java.util.List;

import tds.gestiongastos.modelo.Categoria;

public interface RepositorioCategorias {

	List<Categoria> getAllCategorias();

	void addCategoria(Categoria categoria);

	Categoria findByNombre(String nombre);
	
	void borrarCategoria(Categoria categoria);
}