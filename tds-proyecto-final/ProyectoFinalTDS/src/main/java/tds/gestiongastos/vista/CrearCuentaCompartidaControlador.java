package tds.gestiongastos.vista;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.ParticipanteCuenta;
import tds.gestiongastos.modelo.impl.ParticipanteCuentaImpl;

public class CrearCuentaCompartidaControlador {

    @FXML private TextField txtNombreCuenta;
    @FXML private TextField txtNumPersonas;
    @FXML private VBox containerParticipantes;
    @FXML private RadioButton radioEquitativa;
    @FXML private RadioButton radioPorcentaje;
    
    private ToggleGroup grupoDistribucion;

    @FXML
    public void initialize() {
        grupoDistribucion = new ToggleGroup();
        radioEquitativa.setToggleGroup(grupoDistribucion);
        radioPorcentaje.setToggleGroup(grupoDistribucion);
        radioEquitativa.setSelected(true);
    }

    @FXML
    public void generarCampos(ActionEvent event) {
        String textoNum = txtNumPersonas.getText();
        
        try {
            int numPersonas = Integer.parseInt(textoNum);
            
            if (numPersonas < 2) {
                mostrarAlerta("Debe haber al menos 2 personas.");
                return;
            }

            containerParticipantes.getChildren().clear();

            TextField txtYo = new TextField("Yo");
            txtYo.setDisable(true);
            txtYo.getStyleClass().add("form-field");
            containerParticipantes.getChildren().add(txtYo);

            for (int i = 1; i < numPersonas; i++) {
                TextField txtNuevo = new TextField();
                txtNuevo.setPromptText("Nombre del participante " + (i + 1));
                txtNuevo.getStyleClass().add("form-field");
                containerParticipantes.getChildren().add(txtNuevo);
            }
            
            containerParticipantes.requestLayout();

        } catch (NumberFormatException e) {
            mostrarAlerta("Por favor, introduce un número válido.");
        }
    }

    @FXML
    public void guardarCuenta(ActionEvent event) {
        String nombreCuenta = txtNombreCuenta.getText();
        
        if (nombreCuenta.isEmpty()) {
            mostrarAlerta("Debes poner un nombre a la cuenta.");
            return;
        }

        if (containerParticipantes.getChildren().isEmpty()) {
            mostrarAlerta("Primero debes indicar el número de personas y pulsar 'Generar'.");
            return;
        }

        List<ParticipanteCuenta> listaFinal = new ArrayList<>();
        
        for (Node nodo : containerParticipantes.getChildren()) {
            if (nodo instanceof TextField) {
                String nombre = ((TextField) nodo).getText();
                
                if (nombre == null || nombre.trim().isEmpty()) {
                    mostrarAlerta("Todos los nombres deben estar rellenos.");
                    return;
                }
                
                listaFinal.add(new ParticipanteCuentaImpl(nombre));
            }
        }

        try {
            boolean esEquitativa = radioEquitativa.isSelected();
            Configuracion.getInstancia().getGestionGastos()
                .crearCuentaCompartida(nombreCuenta, listaFinal, esEquitativa);

            cerrarVentana();

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error: " + e.getMessage());
        }
    }

    @FXML
    public void cancelarCuenta(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombreCuenta.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}