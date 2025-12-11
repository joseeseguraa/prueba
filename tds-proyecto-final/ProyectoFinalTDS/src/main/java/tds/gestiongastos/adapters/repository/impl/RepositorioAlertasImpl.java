package tds.gestiongastos.adapters.repository.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tds.gestiongastos.adapters.repository.RepositorioAlertas;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.impl.AlertaImpl;

public class RepositorioAlertasImpl implements RepositorioAlertas {

    private List<AlertaImpl> alertas;
    private final String RUTA_FICHERO = "src/main/resources/alertas.json";

    public RepositorioAlertasImpl() {
        this.alertas = new ArrayList<>();
        cargarDatos();
    }

    @Override
    public void addAlerta(Alerta alerta) {
        if (alerta instanceof AlertaImpl) {
            this.alertas.add((AlertaImpl) alerta);
            guardarDatos();
        }
    }

    @Override
    public void removeAlerta(Alerta alerta) {
        this.alertas.remove(alerta);
        guardarDatos();
    }

    @Override
    public List<Alerta> getAllAlertas() {
        return Collections.unmodifiableList(alertas);
    }


    private void cargarDatos() {
        try {
            File fichero = new File(RUTA_FICHERO);
            if (!fichero.exists()) {
                alertas = new ArrayList<>();
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            alertas = mapper.readValue(fichero, new TypeReference<List<AlertaImpl>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            alertas = new ArrayList<>();
        }
    }

    private void guardarDatos() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_FICHERO), alertas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
