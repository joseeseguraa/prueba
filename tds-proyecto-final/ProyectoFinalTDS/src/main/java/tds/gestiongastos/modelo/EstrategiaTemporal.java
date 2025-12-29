package tds.gestiongastos.modelo;

import java.time.LocalDate;

public interface EstrategiaTemporal {
    boolean esMismoPeriodo(LocalDate fechaGasto, LocalDate fechaReferencia);
    String getNombre();
}