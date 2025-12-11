package tds.gestiongastos.modelo;

import java.time.LocalDate;

public interface Notificacion {
	String getIdNotificacion();

	String getMensaje();

	LocalDate getFechaGeneracion();
}