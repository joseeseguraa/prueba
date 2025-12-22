package tds.gestiongastos.vista;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;

public class CategoriaVistaControlador {

	@FXML private ListView<Categoria> listaCategorias;
	
    @FXML
    private TextField txtNombreCategoria;

    @FXML
    public void initialize() {
    	System.out.println("Abriendo ventana de Gestión de Categorías...");
        cargarLista();
    }
    
    private void cargarLista() {
        List<Categoria> todas = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        listaCategorias.setItems(FXCollections.observableArrayList(todas));
    }
    
    @FXML
    public void guardarCategoria(ActionEvent event) {
        
    	String nombre = txtNombreCategoria.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre de la categoría no puede estar vacío.");
            return;
        }

        boolean exito = Configuracion.getInstancia().getGestionGastos().registrarCategoria(nombre);

        if (exito) {
        	System.out.println("Nueva categoría guardada: " + nombre);
        	txtNombreCategoria.clear();
        	cargarLista();
        } else {
        	System.out.println("Error: Intentando crear categoría duplicada (" + nombre + ")");
            mostrarAlerta("Duplicado", "La categoría '" + nombre + "' ya existe.");
        }
    }

    @FXML
    public void eliminarCategoria(ActionEvent event) {
        Categoria seleccionada = listaCategorias.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Atención", "Selecciona una categoría de la lista para eliminarla.");
            return;
        }

        try {
        	System.out.println("Eliminando categoría: " + seleccionada.getNombre());
        	Configuracion.getInstancia().getGestionGastos().eliminarCategoria(seleccionada);
            cargarLista();
        } catch (IllegalStateException e) {
            mostrarAlerta("No se puede borrar", e.getMessage());
        }
    }
            
            
    @FXML
    public void cancelarCategoria(ActionEvent event) {
    	System.out.println("Operación cancelada. Cerrando ventana de categoria.");

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