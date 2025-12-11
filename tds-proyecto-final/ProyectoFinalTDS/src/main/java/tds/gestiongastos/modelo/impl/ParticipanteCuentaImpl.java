package tds.gestiongastos.modelo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import tds.gestiongastos.modelo.ParticipanteCuenta;

public class ParticipanteCuentaImpl implements ParticipanteCuenta {

	@JsonProperty("nombre")
	private String nombre;

	@JsonProperty("saldo")
	private double saldo;

	@JsonProperty("porcentaje")
	private double porcentajeAsumido;

	// Constructor vacío
	public ParticipanteCuentaImpl() {
		this.saldo = 0.0;
		this.porcentajeAsumido = 0.0;
	}

	public ParticipanteCuentaImpl(String nombre) {
		this();
		this.nombre = nombre;
	}

	@Override
	public String getNombre() {
		return nombre;
	}

	@Override
	public double getSaldo() {
		return saldo;
	}

	@Override
	public double getPorcentajeAsumido() {
		return porcentajeAsumido;
	}

	@Override
	public void actualizarSaldo(double cantidad) {
		this.saldo += cantidad;
	}

	@Override
	public void setPorcentajeAsumido(double porcentaje) {
		this.porcentajeAsumido = porcentaje;
	}
}