package tds.gestiongastos.adapters.repository.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Importante para las fechas LocalDate

import tds.gestiongastos.adapters.repository.RepositorioCuentas;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.TipoCuentaImpl;

public class RepositorioCuentasImpl implements RepositorioCuentas {

	private List<TipoCuentaImpl> cuentas = null;
	private final String RUTA_FICHERO = "src/main/resources/cuentas.json";

	@Override
	public List<TipoCuenta> getAllCuentas() {
		if (cuentas == null)
			cargarDatos();
		return new ArrayList<>(cuentas);
	}

	@Override
	public void addCuenta(TipoCuenta cuenta) {
		if (cuentas == null)
			cargarDatos();
		if (cuenta instanceof TipoCuentaImpl) {
			cuentas.add((TipoCuentaImpl) cuenta);
			guardarDatos();
		}
	}

	@Override
	public void removeCuenta(TipoCuenta cuenta) {
		if (cuentas == null)
			cargarDatos();
		cuentas.remove(cuenta);
		guardarDatos();
	}

	@Override
	public void updateCuenta(TipoCuenta cuenta) {
		if (cuentas == null)
			cargarDatos();

		guardarDatos();
	}

	// --- PERSISTENCIA ---

	private void cargarDatos() {
		try {
			File fichero = new File(RUTA_FICHERO);
			if (!fichero.exists()) {
				cuentas = new ArrayList<>();
				return;
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			// Jackson detectará gracias a @JsonTypeInfo si es Personal o Compartida
			cuentas = mapper.readValue(fichero, new TypeReference<List<TipoCuentaImpl>>() {
			});

		} catch (IOException e) {
			e.printStackTrace();
			cuentas = new ArrayList<>();
		}
	}

	private void guardarDatos() {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			mapper.writerWithDefaultPrettyPrinter().writeValue(new File(RUTA_FICHERO), cuentas);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}