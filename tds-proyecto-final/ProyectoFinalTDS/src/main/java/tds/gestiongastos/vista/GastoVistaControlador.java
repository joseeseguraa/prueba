package tds.gestiongastos.vista;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;

public class GastoVistaControlador {

    @FXML private TextField txtGastoImporte;
    @FXML private TextField txtGastoDescripcion;
    @FXML private DatePicker dateGastoFecha;
    @FXML private ComboBox<Categoria> comboGastoCategoria;

    @FXML
    public void initialize() {
        List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        comboGastoCategoria.setItems(FXCollections.observableArrayList(categorias));


        comboGastoCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return c == null ? null : c.getNombre();
            }
            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    public void guardarGasto(ActionEvent event) {
        try {
            if (txtGastoImporte.getText().isEmpty() || dateGastoFecha.getValue() == null || comboGastoCategoria.getValue() == null) {
                mostrarAlerta("Campos vacíos", "Por favor rellena importe, fecha y categoría.");
                return;
            }

            double importe = Double.parseDouble(txtGastoImporte.getText());
            String desc = (txtGastoDescripcion.getText() != null) ? txtGastoDescripcion.getText() : "Sin concepto"; 
            
            Configuracion.getInstancia().getGestionGastos().registrarGasto(
                desc, 
                importe, 
                dateGastoFecha.getValue(), 
                comboGastoCategoria.getValue().getNombre()
            );

            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El importe debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el gasto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelarRegistro(ActionEvent event) {
        cerrarVentana();
    }
    
    @FXML
    public void botonCategorias(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
        initialize();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtGastoImporte.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}