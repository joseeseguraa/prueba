package tds.gestiongastos.modelo.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.ImportadorGastos;
import tds.gestiongastos.modelo.TipoCuenta;

public class CSVImportadorGastos implements ImportadorGastos {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    public List<Gasto> importarGastos(File fichero) {
        List<Gasto> listaGastos = new ArrayList<>();
        Charset codificacion = StandardCharsets.UTF_8;

        try {
            Files.lines(fichero.toPath(), StandardCharsets.UTF_8).count();
        } catch (Exception e) {
            codificacion = StandardCharsets.ISO_8859_1;
        }

        TipoCuenta cuentaActiva = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        boolean esCuentaCompartida = (cuentaActiva instanceof CuentaCompartida);

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fichero), codificacion))) {

            String linea = br.readLine();

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String lineaLimpia = linea.trim().replace("\"", "");
                String[] columnas = lineaLimpia.split(",");

                if (columnas.length >= 7) {
                    try {
                        String tipoCuentaCSV = columnas[1].trim();

                        if (esCuentaCompartida) {
                            if ("Personal".equalsIgnoreCase(tipoCuentaCSV)) {
                                continue; 
                            }
                        } else {
                            if (!"Personal".equalsIgnoreCase(tipoCuentaCSV)) {
                                continue;
                            }
                        }
                        
                        String fechaStr = columnas[0].trim();
                        String nombreCat = columnas[3].trim();
                        String desc = columnas[4].trim();
                        String pagador = columnas[5].trim();
                        String importeStr = columnas[6].trim().replace("€", "");

                        double importe = Double.parseDouble(importeStr);
                        LocalDate fecha = LocalDate.parse(fechaStr, FORMATO);

                        GastoImpl nuevoGasto = new GastoImpl(desc, importe, fecha, new CategoriaImpl(nombreCat));
                        nuevoGasto.setPagador(pagador);

                        listaGastos.add(nuevoGasto);

                    } catch (Exception e) {
                    }
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return listaGastos;
    }
}