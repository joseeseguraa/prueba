package tds.gestiongastos.modelo.impl;

import java.time.LocalDate;
import tds.gestiongastos.modelo.EstrategiaTemporal;

public class EstrategiaSemanal implements EstrategiaTemporal {

    @Override
    public boolean esMismoPeriodo(LocalDate fechaGasto, LocalDate fechaReferencia) {
       
        int diasDesdeLunes = fechaReferencia.getDayOfWeek().getValue() - 1;
        LocalDate lunesSemana = fechaReferencia.minusDays(diasDesdeLunes);
        
        LocalDate domingoSemana = lunesSemana.plusDays(6);

        return !fechaGasto.isBefore(lunesSemana) && !fechaGasto.isAfter(domingoSemana);
    }

    @Override
    public String getNombre() {
        return "Semanal";
    }
}