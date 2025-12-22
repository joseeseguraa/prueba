package tds.gestiongastos.vista;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;

public class AlertaVistaControlador {

	@FXML private TextField txtNombreAlerta;
	@FXML private TextField txtAlertaLimite;
	@FXML private ListView<Alerta> listaAlertas;
    @FXML private ComboBox<String> comboPeriodicidad;
    @FXML private ComboBox<Categoria> comboAlertaCategoria;
    

    @FXML
    public void initialize() {
    	System.out.println("Abriendo ventana de Nueva Alerta...");
    	
    	comboPeriodicidad.setItems(FXCollections.observableArrayList("Mensual", "Semanal"));
        
        List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        comboAlertaCategoria.setItems(FXCollections.observableArrayList(categorias));
        
        listaAlertas.setCellFactory(param -> new ListCell<Alerta>() {
            @Override
            protected void updateItem(Alerta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String cat = (item.getCategoria() != null) ? item.getCategoria().getNombre() : "General";
                    setText(item.getTipo() + ": " + item.getLimite() + "€ (" + cat + ")");
                }
            }
        });
        cargarListaAlertas();
    }
    
    private void cargarListaAlertas() {
    	List<Alerta> alertas = Configuracion.getInstancia().getGestionGastos().getAlertas();
    	
    	listaAlertas.setItems(FXCollections.observableArrayList(alertas));
        listaAlertas.refresh();
    }
    
    @FXML
    public void guardarAlerta(ActionEvent event) {
        try {
            String tipo = comboPeriodicidad.getValue();
            double limite = Double.parseDouble(txtAlertaLimite.getText());
            Categoria cat = comboAlertaCategoria.getValue();

            if (tipo == null) {
                mostrarMensaje("Selecciona un tipo de alerta (Mensual/Semanal)");
                return;
            }
            
            String nombreCat = "General";
            if (cat != null) {
                nombreCat = cat.getNombre();
            }
            
            System.out.println("Guardando nueva alerta -> Tipo: " + tipo + " | Límite: " + limite + "€ | Categoría: " + nombreCat);
            
            Configuracion.getInstancia().getGestionGastos().configurarAlerta(tipo, limite, cat);
            
            txtAlertaLimite.clear();
            cargarListaAlertas();
            
        } catch (NumberFormatException e) {
            mostrarMensaje("El límite debe ser un número válido.");
        }
    }
    
    @FXML
    public void eliminarAlerta(ActionEvent event) {
        Alerta seleccionada = listaAlertas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            Configuracion.getInstancia().getGestionGastos().borrarAlerta(seleccionada);
            cargarListaAlertas();
        }
    }

    private void mostrarMensaje(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    
    @FXML
    public void cancelarRegistro(ActionEvent event) {
    	System.out.println("Operación cancelada. Cerrando ventana de alertas.");
    	
    	Stage stage = (Stage) txtAlertaLimite.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void botonCategorias(ActionEvent event) {
    	System.out.println("Clic en botón Categorías: Abriendo gestor de categorías...");
    	
    	Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
    	List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        comboAlertaCategoria.setItems(FXCollections.observableArrayList(categorias));
    }
}
