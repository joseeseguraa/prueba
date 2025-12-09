package tds.gestiongastos.adapters.repository.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tds.gestiongastos.adapters.repository.RepositorioCategorias;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.impl.CategoriaImpl;

public class RepositorioCategoriasImpl implements RepositorioCategorias {

	private List<CategoriaImpl> categorias = new ArrayList<CategoriaImpl>();

	private final String RUTA_FICHERO = "src/main/resources/categorias.json";

	public RepositorioCategoriasImpl() {
	}

	@Override
	public List<Categoria> getAllCategorias() {
		if (categorias == null) {
			cargarDatos();
		}
		return new ArrayList<>(categorias);
	}

	@Override
	public void addCategoria(Categoria categoria) {
		if (categorias == null)
			cargarDatos();

		if (categoria instanceof CategoriaImpl) {
			categorias.add((CategoriaImpl) categoria);
			guardarDatos();
		}
	}

	@Override
	public Categoria findByNombre(String nombre) {
		if (categorias == null)
			cargarDatos();

		return categorias.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
	}

	// --- MÉTODOS PRIVADOS DE PERSISTENCIA (Jackson) ---

	private void cargarDatos() {
		try {
			File fichero = new File(RUTA_FICHERO);
			if (!fichero.exists()) {
				categorias = new ArrayList<>();
				return;
			}

			ObjectMapper mapper = new ObjectMapper();
			categorias = mapper.readValue(fichero, new TypeReference<List<CategoriaImpl>>() {
			});

		} catch (IOException e) {
			e.printStackTrace();
			categorias = new ArrayList<>();
		}
	}

	private void guardarDatos() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_FICHERO), categorias);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}