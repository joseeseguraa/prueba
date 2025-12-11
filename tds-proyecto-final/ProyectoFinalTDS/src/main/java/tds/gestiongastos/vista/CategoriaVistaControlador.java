package tds.gestiongastos.vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;

public class CategoriaVistaControlador {

    @FXML
    private TextField txtNombreCategoria;

    @FXML
    public void guardarCategoria(ActionEvent event) {
        String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre de la categoría no puede estar vacío.");
            return;
        }

        boolean exito = Configuracion.getInstancia().getGestionGastos().registrarCategoria(nombre, "Descripción por defecto");

        if (exito) {
            cerrarVentana();
        } else {
            mostrarAlerta("Duplicado", "La categoría '" + nombre + "' ya existe.");
        }
    }

    @FXML
    public void cancelarCategoria(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtNombreCategoria.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}