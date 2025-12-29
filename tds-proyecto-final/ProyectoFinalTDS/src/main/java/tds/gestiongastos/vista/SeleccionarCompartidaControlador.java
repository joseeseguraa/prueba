package tds.gestiongastos.vista;

import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.TipoCuenta;

public class SeleccionarCompartidaControlador {

    @FXML private ListView<CuentaCompartida> listaCuentas;
    @FXML private Button btnAcceder;

    @FXML
    public void initialize() {
        System.out.println("Abriendo selector de Cuentas Compartidas...");
        List<TipoCuenta> todas = Configuracion.getInstancia().getGestionGastos().getCuentasDisponibles();
        
        List<CuentaCompartida> compartidas = todas.stream()
            .filter(c -> c instanceof CuentaCompartida)
            .map(c -> (CuentaCompartida) c)
            .collect(Collectors.toList());

        listaCuentas.setItems(FXCollections.observableArrayList(compartidas));
    }

    @FXML
    public void acceder(ActionEvent event) {
        System.out.println(">>> Acción de Usuario: Click en 'Entrar en Cuenta'");
        CuentaCompartida seleccionada = listaCuentas.getSelectionModel().getSelectedItem();
        
        if (seleccionada == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setContentText("Selecciona una cuenta para entrar.");
            alert.show();
            return;
        }

        Configuracion.getInstancia().getGestionGastos().setCuentaActiva(seleccionada);

        Stage stage = (Stage) btnAcceder.getScene().getWindow();
        stage.close();
        
        Configuracion.getInstancia().getSceneManager().showCuentaCompartida();
    }
}