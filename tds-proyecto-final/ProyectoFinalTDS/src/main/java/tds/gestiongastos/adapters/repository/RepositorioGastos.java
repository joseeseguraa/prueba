package tds.gestiongastos.adapters.repository;

import java.util.List;
import tds.gestiongastos.modelo.Gasto;

public interface RepositorioGastos {

	List<Gasto> getAllGastos();

	void addGasto(Gasto gasto);

	void removeGasto(Gasto gasto);

	void updateGasto(Gasto gasto);
}