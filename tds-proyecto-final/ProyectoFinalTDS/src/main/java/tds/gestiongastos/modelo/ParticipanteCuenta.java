package tds.gestiongastos.modelo;

public interface ParticipanteCuenta {
	String getNombre();

	double getSaldo();

	void actualizarSaldo(double cantidad);

	double getPorcentajeAsumido();

	void setPorcentajeAsumido(double porcentaje);
}