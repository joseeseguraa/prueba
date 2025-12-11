package tds.gestiongastos.adapters.repository.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tds.gestiongastos.adapters.repository.RepositorioNotificaciones;
import tds.gestiongastos.modelo.Notificacion;
import tds.gestiongastos.modelo.impl.NotificacionImpl;

public class RepositorioNotificacionesImpl implements RepositorioNotificaciones {

    private List<NotificacionImpl> notificaciones;
    private final String RUTA_FICHERO = "src/main/resources/notificaciones.json";

    public RepositorioNotificacionesImpl() {
        this.notificaciones = new ArrayList<>();
        cargarDatos();
    }

    @Override
    public void addNotificacion(Notificacion notificacion) {
        if (notificacion instanceof NotificacionImpl) {
            this.notificaciones.add((NotificacionImpl) notificacion);
            guardarDatos();
        }
    }

    @Override
    public List<Notificacion> getAllNotificaciones() {
        return Collections.unmodifiableList(notificaciones);
    }


    private void cargarDatos() {
        try {
            File fichero = new File(RUTA_FICHERO);
            if (!fichero.exists()) {
                notificaciones = new ArrayList<>();
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            notificaciones = mapper.readValue(fichero, new TypeReference<List<NotificacionImpl>>() {});

        } catch (IOException e) {
            e.printStackTrace();
            notificaciones = new ArrayList<>();
        }
    }

    private void guardarDatos() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_FICHERO), notificaciones);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
