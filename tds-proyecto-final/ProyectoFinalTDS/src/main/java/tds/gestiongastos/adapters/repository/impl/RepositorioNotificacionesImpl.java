package tds.gestiongastos.adapters.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tds.gestiongastos.adapters.repository.RepositorioNotificaciones;
import tds.gestiongastos.modelo.Notificacion;

public class RepositorioNotificacionesImpl implements RepositorioNotificaciones {

    private List<Notificacion> notificaciones;

    public RepositorioNotificacionesImpl() {
        this.notificaciones = new ArrayList<Notificacion>();
    }

    @Override
    public void addNotificacion(Notificacion notificacion) {
        this.notificaciones.add(notificacion);
    }

    
    public List<Notificacion> getAllNotificaciones() {
		return Collections.unmodifiableList(notificaciones);
	}

	public void setNotificaciones(List<Notificacion> notificaciones) {
		this.notificaciones = notificaciones;
	}

    public List<Notificacion> obtenerPorUsuario(String idUsuario) {
        return new ArrayList<>(this.notificaciones);
    }
}