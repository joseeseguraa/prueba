package tds.gestiongastos.modelo;

import java.util.List;

public interface TipoCuenta {
	String getNombre();

	void setNombre(String nombre);

	List<Gasto> getGastos();

	void agregarGasto(Gasto gasto);

	void eliminarGasto(Gasto gasto);

	double getSaldoTotal();
}