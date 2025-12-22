package tds.gestiongastos.adapters.repository;

import java.util.List;

import tds.gestiongastos.modelo.Alerta;

public interface RepositorioAlertas {

	List<Alerta> getAllAlertas();

	void addAlerta(Alerta alerta);

	void removeAlerta(Alerta alerta);
}