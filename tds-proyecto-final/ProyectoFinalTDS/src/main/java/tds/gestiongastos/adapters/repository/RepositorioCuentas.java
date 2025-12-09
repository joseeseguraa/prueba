package tds.gestiongastos.adapters.repository;

import java.util.List;
import tds.gestiongastos.modelo.TipoCuenta;

public interface RepositorioCuentas {

	List<TipoCuenta> getAllCuentas();

	void addCuenta(TipoCuenta cuenta);

	void removeCuenta(TipoCuenta cuenta);

	void updateCuenta(TipoCuenta cuenta);
}