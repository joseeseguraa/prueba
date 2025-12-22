package tds.gestiongastos.vista;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tds.gestiongastos.controlador.GestionGastos;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.CuentaPersonalImpl;

public class MainVistaControlador {

    @FXML
    public void initialize() {
        System.out.println("Vista Principal cargada correctamente.");
    }

    @FXML
    public void botonImportarDatos(ActionEvent event) {
        System.out.println("Click en Importar Datos");
    }

    @FXML
    public void botonHistorialNotificaciones(ActionEvent event) {
        System.out.println("Click en Historial");
    }

    @FXML
    public void botonAccederPersonal(ActionEvent event) {
        System.out.println("Accediendo a Cuenta Personal...");


        GestionGastos gestion = Configuracion.getInstancia().getGestionGastos();
        List<TipoCuenta> cuentas = gestion.getCuentasDisponibles();


        TipoCuenta miCuenta = cuentas.stream()
                .filter(c -> c instanceof CuentaPersonalImpl)
                .findFirst()
                .orElse(null);

        if (miCuenta != null) {
            gestion.setCuentaActiva(miCuenta);
            System.out.println(">> Cuenta activa fijada: " + miCuenta.getNombre());

            Configuracion.getInstancia().getSceneManager().showCuentaPersonal();
        } else {
            System.err.println("ERROR: No se encontró ninguna cuenta personal cargada.");
        }
    }

    @FXML
    public void botonAccederCompartidas(ActionEvent event) {
        System.out.println("Accediendo a Cuentas Compartidas...");
    }

    @FXML
    public void botonCrearCompartida(ActionEvent event) {
        System.out.println("Crear nueva cuenta compartida...");


        System.out.println("TODO: Implementar showCrearCuentaCompartida en SceneManager");
    }
}