package tds.gestiongastos.modelo.impl;

import java.time.LocalDate;
import tds.gestiongastos.modelo.EstrategiaTemporal;

public class EstrategiaMensual implements EstrategiaTemporal {

    @Override
    public boolean esMismoPeriodo(LocalDate fechaGasto, LocalDate fechaReferencia) {
        return fechaGasto.getMonth() == fechaReferencia.getMonth() &&
               fechaGasto.getYear() == fechaReferencia.getYear();
    }

    @Override
    public String getNombre() {
        return "Mensual";
    }
}