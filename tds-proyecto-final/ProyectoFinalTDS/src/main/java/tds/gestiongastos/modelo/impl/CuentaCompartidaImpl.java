package tds.gestiongastos.modelo.impl;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import tds.gestiongastos.modelo.ParticipanteCuenta;

public class CuentaCompartidaImpl extends TipoCuentaImpl {

	@JsonProperty("participantes")
	private List<ParticipanteCuentaImpl> participantes;

	public CuentaCompartidaImpl() {
		super();
		this.participantes = new ArrayList<>();
	}

	public CuentaCompartidaImpl(String nombre, List<ParticipanteCuentaImpl> participantes) {
		super(nombre);
		this.participantes = participantes;
	}

	public List<ParticipanteCuenta> getParticipantes() {
		return new ArrayList<>(participantes);
	}
}