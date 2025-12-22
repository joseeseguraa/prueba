package tds.gestiongastos.modelo;

public interface Alerta {
	String getTipo();

	double getLimite();

	boolean comprobar(Gasto gastoNuevo, TipoCuenta cuenta);

	Notificacion crearNotificacion();

	String getIdAlerta();
	
	Categoria getCategoria();
}