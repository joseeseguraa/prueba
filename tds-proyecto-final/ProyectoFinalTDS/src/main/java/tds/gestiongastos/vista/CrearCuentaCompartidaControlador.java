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
import javafx.scene.layout.HBox;
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

        grupoDistribucion.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            actualizarEstadoCamposPorcentaje();
        });
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

            agregarFilaParticipante("Yo", true);
            for (int i = 1; i < numPersonas; i++) {
                agregarFilaParticipante("", false);
            }
            
            containerParticipantes.requestLayout();
            
            actualizarEstadoCamposPorcentaje(); 

        } catch (NumberFormatException e) {
            mostrarAlerta("Por favor, introduce un número válido de personas.");
        }
    }

    private void agregarFilaParticipante(String nombreDefecto, boolean esYo) {
        HBox fila = new HBox(10); 
        
        TextField txtNombre = new TextField(nombreDefecto);
        txtNombre.setPromptText("Nombre participante");
        txtNombre.getStyleClass().add("form-field");
        if (esYo) txtNombre.setDisable(true); 
        else txtNombre.setPrefWidth(200);

        TextField txtPorcentaje = new TextField();
        txtPorcentaje.setPromptText("%");
        txtPorcentaje.setPrefWidth(60);
        txtPorcentaje.getStyleClass().add("form-field");
        
        fila.getChildren().addAll(txtNombre, txtPorcentaje);
        containerParticipantes.getChildren().add(fila);
    }

    private void actualizarEstadoCamposPorcentaje() {
        boolean esEquitativa = radioEquitativa.isSelected();
        int numParticipantes = containerParticipantes.getChildren().size();
        
        double porcentajeEquitativo = (numParticipantes > 0) ? (100.0 / numParticipantes) : 0;
        String textoPorcentaje = String.format("%.2f", porcentajeEquitativo).replace(",", ".");

        for (Node nodoFila : containerParticipantes.getChildren()) {
            if (nodoFila instanceof HBox) {
                HBox fila = (HBox) nodoFila;
                if (fila.getChildren().size() > 1) {
                    TextField txtPorc = (TextField) fila.getChildren().get(1);
                    
                    txtPorc.setDisable(esEquitativa);
                    if (esEquitativa) {
                        txtPorc.setText(textoPorcentaje);
                    } else {
                        if (txtPorc.getText().isEmpty()) txtPorc.setText(""); 
                    }
                }
            }
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
            mostrarAlerta("Primero indica el número de personas y pulsa 'Generar'.");
            return;
        }

        List<ParticipanteCuenta> listaFinal = new ArrayList<>();
        double sumaPorcentajes = 0.0;
        boolean esEquitativa = radioEquitativa.isSelected();
        
        for (Node nodoFila : containerParticipantes.getChildren()) {
            if (nodoFila instanceof HBox) {
                HBox fila = (HBox) nodoFila;
                TextField txtNombre = (TextField) fila.getChildren().get(0);
                TextField txtPorcentaje = (TextField) fila.getChildren().get(1);

                String nombre = txtNombre.getText();
                if (nombre == null || nombre.trim().isEmpty()) {
                    mostrarAlerta("Todos los nombres deben estar rellenos.");
                    return;
                }
                
                ParticipanteCuenta p = new ParticipanteCuentaImpl(nombre); 
                
                if (!esEquitativa) {
                    try {
                        String val = txtPorcentaje.getText().replace(",", ".");
                        double d = Double.parseDouble(val);
                        if (d < 0) throw new NumberFormatException();
                        p.setPorcentajeAsumido(d);
                        sumaPorcentajes += d;
                    } catch (NumberFormatException e) {
                        mostrarAlerta("Error: Los porcentajes deben ser números válidos.");
                        return;
                    }
                }
                
                listaFinal.add(p);
            }
        }

        if (!esEquitativa && Math.abs(sumaPorcentajes - 100.0) > 0.01) {
            mostrarAlerta("La suma de los porcentajes es " + String.format("%.2f", sumaPorcentajes) + "%. Debe ser 100%.");
            return;
        }

        try {
            Configuracion.getInstancia().getGestionGastos()
                .crearCuentaCompartida(nombreCuenta, listaFinal, esEquitativa);
            
            cerrarVentana();

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error al crear la cuenta: " + e.getMessage());
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