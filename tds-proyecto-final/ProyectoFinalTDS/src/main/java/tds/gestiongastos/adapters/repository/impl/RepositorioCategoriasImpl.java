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

    private List<CategoriaImpl> categorias = null;
    private final String RUTA_FICHERO = "categorias.json";
    
    @Override
    public List<Categoria> getAllCategorias() {
        if (categorias == null) {
            cargarDatos();
        }
        return new ArrayList<>(categorias);
    }

    @Override
    public void addCategoria(Categoria categoria) {
        if (categorias == null) {
			cargarDatos();
		}

        if (categoria instanceof CategoriaImpl) {
            categorias.add((CategoriaImpl) categoria);
            guardarDatos();
        }
    }

    @Override
    public Categoria findByNombre(String nombre) {
        if (categorias == null) {
			cargarDatos();
		}

        return categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void borrarCategoria(Categoria categoria) {
        if (categorias == null) {
            cargarDatos();
        }

        boolean borrado = categorias.remove(categoria);
        
        if (borrado) {
            guardarDatos();
            System.out.println(">> Categoría '" + categoria.getNombre() + "' eliminada y guardada.");
        }
    }
    
    
    private void cargarDatos() {
        try {
            File fichero = new File(RUTA_FICHERO);
            if (!fichero.exists() || fichero.length() == 0) {
                iniciarCategoriasPorDefecto();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            categorias = mapper.readValue(fichero, new TypeReference<List<CategoriaImpl>>() {});

        } catch (IOException e) {
            System.out.println(">> Aviso: Error leyendo categorias.json. Se regenerará con valores por defecto.");
            iniciarCategoriasPorDefecto();
        }
    }

    private void iniciarCategoriasPorDefecto() {
        categorias = new ArrayList<>();
        categorias.add(new CategoriaImpl("Alimentación"));
        categorias.add(new CategoriaImpl("Transporte"));
        categorias.add(new CategoriaImpl("Ocio"));
        guardarDatos();
    }

    private void guardarDatos() {
        try {
            File fichero = new File(RUTA_FICHERO);
            if (fichero.getParentFile() != null) {
                fichero.getParentFile().mkdirs();
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(fichero, categorias);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}