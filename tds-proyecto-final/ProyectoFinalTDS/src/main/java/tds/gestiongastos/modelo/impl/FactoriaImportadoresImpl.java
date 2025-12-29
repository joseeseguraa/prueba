package tds.gestiongastos.modelo.impl;

import tds.gestiongastos.modelo.ImportadorGastos;

public class FactoriaImportadoresImpl {
    public static ImportadorGastos crearImportador(String tipo) {
        if (tipo.equalsIgnoreCase("csv")) {
            return new CSVImportadorGastos();
        }
        return null;
    }
}
