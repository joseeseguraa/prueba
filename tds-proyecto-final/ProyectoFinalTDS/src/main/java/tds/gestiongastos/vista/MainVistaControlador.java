package tds.gestiongastos.vista;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import tds.gestiongastos.controlador.GestionGastos;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.CuentaPersonalImpl;

public class MainVistaControlador {

    @FXML
    public void initialize() {
        System.out.println("Vista Principal cargada correctamente.");
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
        GestionGastos gestion = Configuracion.getInstancia().getGestionGastos();
        
        boolean hayCompartidas = gestion.getCuentasDisponibles().stream()
                .anyMatch(c -> c instanceof CuentaCompartida);
        
        if (!hayCompartidas) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sin Cuentas Compartidas");
            alert.setHeaderText(null);
            alert.setContentText("No existe ninguna cuenta compartida.\nPulsa 'Crear Cuenta Compartida' para empezar.");
            alert.showAndWait();
        } else {
            Configuracion.getInstancia().getSceneManager().showSeleccionarCompartida();
        }
    }

    @FXML
    public void botonCrearCompartida(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showCrearCuentaCompartida();
    }
}