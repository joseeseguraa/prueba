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

	public ParticipanteCuentaImpl() {
		this.saldo = 0.0;
		this.porcentajeAsumido = 0.0;
	}

	public ParticipanteCuentaImpl(String nombre) {
		this();
		this.nombre = nombre;
	}

	public ParticipanteCuentaImpl(String nombre, double porcentajeAsumido) {
		this();
		this.nombre = nombre;
		this.porcentajeAsumido = porcentajeAsumido;
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
	public void setSaldo(double saldo) {
	    this.saldo = saldo;
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

	@Override
	public String toString() {
		return nombre + " (" + porcentajeAsumido + "%)";
	}
}