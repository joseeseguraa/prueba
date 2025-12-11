package tds.gestiongastos.modelo;

public interface Alerta {
	String getTipo();

	double getLimite();

	Categoria getCategoriaMonitorizada();

	boolean comprobar(Gasto gastoNuevo, TipoCuenta cuenta);

	Notificacion crearNotificacion();

	String getIdAlerta();
}