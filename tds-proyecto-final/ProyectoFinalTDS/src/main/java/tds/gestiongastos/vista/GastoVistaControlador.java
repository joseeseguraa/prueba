package tds.gestiongastos.vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tds.gestiongastos.main.Configuracion;

public class GastoVistaControlador {

    @FXML
    public void initialize() {

    }

    // Coincide con onAction="#botonImportarDatos"
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
        System.out.println("Entrando a Cuenta Personal...");
        Configuracion.getInstancia().getSceneManager().showCuentaPersonal();
    }

    @FXML
    public void botonAccederCompartidas(ActionEvent event) {
        System.out.println("Entrando a Cuentas Compartidas...");
    }
}