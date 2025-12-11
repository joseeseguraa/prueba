package tds.gestiongastos.modelo.impl;

import java.time.LocalDate;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tds.gestiongastos.modelo.Notificacion;

public class NotificacionImpl implements Notificacion {

	@JsonProperty("id")
	private String idNotificacion;

	@JsonProperty("mensaje")
	private String mensaje;

	@JsonProperty("fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	private LocalDate fechaGeneracion;

	public NotificacionImpl() {
	}

	public NotificacionImpl(String mensaje, LocalDate fecha) {
		this.idNotificacion = UUID.randomUUID().toString();
		this.mensaje = mensaje;
		this.fechaGeneracion = fecha;
	}

	@Override
	public String getIdNotificacion() {
		return idNotificacion;
	}

	@Override
	public String getMensaje() {
		return mensaje;
	}

	@Override
	public LocalDate getFechaGeneracion() {
		return fechaGeneracion;
	}
}