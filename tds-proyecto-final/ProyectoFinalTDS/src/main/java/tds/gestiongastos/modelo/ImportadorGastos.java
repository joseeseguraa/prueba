package tds.gestiongastos.modelo;

import java.io.File;
import java.util.List;

public interface ImportadorGastos {
    List<Gasto> importarGastos(File fichero);
}
