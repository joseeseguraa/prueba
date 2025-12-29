package tds.gestiongastos.vista;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;

public class CategoriaVistaControlador {

    @FXML private ListView<Categoria> listaCategorias;
    @FXML private TextField txtNombreCategoria;

    private static final String SEPARADOR = "--------------------------------------------------";

    @FXML
    public void initialize() {
        System.out.println("Inicializando Gestión de Categorías...");
        listaCategorias.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        listaCategorias.setCellFactory(lv -> new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String nombreInterno = item.getNombre();
                    int index = nombreInterno.indexOf("_");
                    setText((index != -1) ? nombreInterno.substring(index + 1) : nombreInterno);
                }
            }
        });
        
        cargarLista();
        System.out.println(SEPARADOR);
    }
    
    private void cargarLista() {
        List<Categoria> todas = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        listaCategorias.setItems(FXCollections.observableArrayList(todas));
    }
    
    @FXML
    public void guardarCategoria(ActionEvent event) {
        System.out.println(">>> Acción: Guardar Categoría");
        String nombre = txtNombreCategoria.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "El nombre de la categoría no puede estar vacío.");
            System.out.println(SEPARADOR);
            return;
        }
        boolean exito = Configuracion.getInstancia().getGestionGastos().registrarCategoria(nombre);
        if (exito) {
            txtNombreCategoria.clear();
            cargarLista();
        } else {
            mostrarAlerta("Duplicado", "La categoría '" + nombre + "' ya existe.");
            System.out.println(SEPARADOR); 
        }
    }

    @FXML
    public void eliminarCategoria(ActionEvent event) {
        System.out.println(">>> Acción: Eliminar Categoría");
        var seleccionados = listaCategorias.getSelectionModel().getSelectedItems();
        if (seleccionados.isEmpty()) {
            mostrarAlertaSimple("Selecciona al menos una categoría para eliminar.");
            System.out.println(SEPARADOR);
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas eliminar las " + seleccionados.size() + " categorías seleccionadas?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            List<Categoria> categoriasParaBorrar = new ArrayList<>(seleccionados);
            try {
                Configuracion.getInstancia().getGestionGastos().eliminarCategorias(categoriasParaBorrar);
                cargarLista();
            } catch (IllegalStateException e) {
                mostrarAlertaSimple(e.getMessage());
                System.out.println(SEPARADOR);
            }
        } else {
            System.out.println(SEPARADOR);
        }
    }
            
    @FXML
    public void cancelarCategoria(ActionEvent event) {
        System.out.println(">>> Acción: Cerrar Categorías");
        System.out.println(SEPARADOR);
        Stage stage = (Stage) txtNombreCategoria.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlertaSimple(String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Atención");
        alert.setHeaderText(null);
        Label label = new Label(msg);
        label.setWrapText(true);
        label.setPrefWidth(350);
        alert.getDialogPane().setContent(label);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}