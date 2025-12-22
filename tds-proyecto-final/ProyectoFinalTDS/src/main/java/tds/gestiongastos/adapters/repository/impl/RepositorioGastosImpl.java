package tds.gestiongastos.adapters.repository.impl;

import java.util.ArrayList;
import java.util.List;

import tds.gestiongastos.adapters.repository.RepositorioCuentas;
import tds.gestiongastos.adapters.repository.RepositorioGastos;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;

public class RepositorioGastosImpl implements RepositorioGastos {

	private RepositorioCuentas repoCuentas;

	public RepositorioGastosImpl(RepositorioCuentas repoCuentas) {
		this.repoCuentas = repoCuentas;
	}

	@Override
	public List<Gasto> getAllGastos() {
		List<Gasto> todosLosGastos = new ArrayList<>();
		for (TipoCuenta c : repoCuentas.getAllCuentas()) {
			todosLosGastos.addAll(c.getGastos());
		}
		return todosLosGastos;
	}

	@Override
	public void addGasto(Gasto gasto) {
	}

	@Override
	public void removeGasto(Gasto gasto) {
		List<TipoCuenta> cuentas = repoCuentas.getAllCuentas();
		for (TipoCuenta c : cuentas) {
			if (c.getGastos().contains(gasto)) {
				c.eliminarGasto(gasto);
				repoCuentas.updateCuenta(c);
				return;
			}
		}
	}

	@Override
	public void updateGasto(Gasto gasto) {
		List<TipoCuenta> cuentas = repoCuentas.getAllCuentas();
		for (TipoCuenta c : cuentas) {
			if (c.getGastos().contains(gasto)) {
				repoCuentas.updateCuenta(c);
				return;
			}
		}
	}
}