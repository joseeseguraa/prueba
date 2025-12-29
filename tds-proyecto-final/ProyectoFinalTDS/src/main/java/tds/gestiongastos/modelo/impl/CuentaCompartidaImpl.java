package tds.gestiongastos.modelo.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.ParticipanteCuenta;

public class CuentaCompartidaImpl extends TipoCuentaImpl implements CuentaCompartida {

	@JsonProperty("participantes")
    @JsonDeserialize(contentAs = ParticipanteCuentaImpl.class)
    private List<ParticipanteCuenta> participantes;
	
	public CuentaCompartidaImpl() {
        super();
        this.participantes = new ArrayList<>();
    }
	
	public CuentaCompartidaImpl(String nombre, List<ParticipanteCuenta> participantes) {
        super(nombre);
        this.participantes = participantes;
    }

	@Override
    public List<ParticipanteCuenta> getParticipantes() {
        return participantes;
    }

    @Override
    public void calculoDistribucionEquitativa() {
        if (participantes == null || participantes.isEmpty()) return;
        double porcentaje = 100.0 / participantes.size();
        for (ParticipanteCuenta p : participantes) {
            p.setPorcentajeAsumido(porcentaje);
        }
    }

    @Override
    public void calculoPorcentajeGastoAsumido() {
        if (participantes == null || participantes.isEmpty()) return;
        double suma = 0.0;
        for (ParticipanteCuenta p : participantes) {
            suma += p.getPorcentajeAsumido();
        }
        if (Math.abs(suma - 100.0) > 0.01) {
            throw new IllegalArgumentException("Los porcentajes suman " + String.format("%.2f", suma) + "%, deben sumar 100%.");
        }
    }

    @Override
    public void agregarGasto(Gasto gasto) {
        super.agregarGasto(gasto);

        if (participantes == null || participantes.isEmpty()) return;

        String nombrePagador = gasto.getPagador(); 
        double importeTotal = gasto.getCantidad();

        for (ParticipanteCuenta p : participantes) {
            double parteQueDebePagar = importeTotal * (p.getPorcentajeAsumido() / 100.0);

            if (p.getNombre().equals(nombrePagador)) {
                p.actualizarSaldo(importeTotal - parteQueDebePagar);
            } else {
                p.actualizarSaldo(-parteQueDebePagar);
            }
        }
    }
    
 
    public void recalcularSaldos() {
        if (participantes != null) {
            for (ParticipanteCuenta p : participantes) {
                p.setSaldo(0.0);
            }
        }

        for (Gasto g : getGastos()) {
            distribuirGastoEnSaldos(g);
        }
    }
    
    
    private void distribuirGastoEnSaldos(Gasto gasto) {
        if (participantes == null || participantes.isEmpty()) return;

        String nombrePagador = gasto.getPagador(); 
        double importeTotal = gasto.getCantidad();

        for (ParticipanteCuenta p : participantes) {
            double parteQueDebePagar = importeTotal * (p.getPorcentajeAsumido() / 100.0);

            if (p.getNombre().equals(nombrePagador)) {
                p.actualizarSaldo(importeTotal - parteQueDebePagar);
            } else {
                p.actualizarSaldo(-parteQueDebePagar);
            }
        }
    }
    
    @Override
    public String toString() {
    	return super.getNombre() + " (Compartida)";
    }
}