package tds.gestiongastos.modelo;

import java.time.LocalDate;

public interface Gasto {
	String getId();

	String getDescripcion();

	double getCantidad();

	LocalDate getFecha();

	Categoria getCategoria();
	
	String getPagador();

	void setDescripcion(String descripcion);

	void setCantidad(double cantidad);

	void setFecha(LocalDate fecha);

	void setCategoria(Categoria categoria);
	
	void setPagador(String pagador);
}