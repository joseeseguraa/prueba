package tds.gestiongastos.vista;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Notificacion;

public class HistorialNotificacionesControlador {

    @FXML
    private ListView<Notificacion> listaNotificaciones;

    @FXML
    public void initialize() {
        System.out.println(">>> Acción de Usuario: Abriendo Ventana Historial");
        listaNotificaciones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cargarDatos();
    }

    private void cargarDatos() {
        var gestionGastos = Configuracion.getInstancia().getGestionGastos();
        List<Notificacion> todas = gestionGastos.revisarHistorialNotificaciones();
        String nombreCuentaActiva = gestionGastos.getCuentaActiva().getNombre();

        List<Notificacion> filtradas = new ArrayList<>();
        if (todas != null) {
            for (Notificacion n : todas) {
                if (n.getMensaje() != null && n.getMensaje().contains(nombreCuentaActiva)) {
                    filtradas.add(n);
                }
            }
        }

        listaNotificaciones.setItems(FXCollections.observableArrayList(filtradas));

        listaNotificaciones.setCellFactory(param -> new ListCell<Notificacion>() {
            @Override
            protected void updateItem(Notificacion item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String fechaStr = (item.getFechaGeneracion() != null) 
                                      ? item.getFechaGeneracion().format(fmt) 
                                      : "??/??/????";
                    
                    String mensaje = item.getMensaje();
                    
                    String prefijo = nombreCuentaActiva + "_";
                    if (mensaje.contains(prefijo)) {
                        mensaje = mensaje.replace(prefijo, "");
                    }
                    
                    setText("[" + fechaStr + "] " + mensaje);
                }
            }
        });
    }
    
    @FXML
    public void borrarNotificacion(ActionEvent event) {
        System.out.println(">>> Acción de Usuario: Click en 'Borrar Notificación'");
        
        List<Notificacion> seleccionadas = new ArrayList<>(listaNotificaciones.getSelectionModel().getSelectedItems());
        
        if (!seleccionadas.isEmpty()) {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.setTitle("Borrar Historial");
            confirm.setHeaderText("Vas a eliminar " + seleccionadas.size() + " notificación(es).");
            confirm.setContentText("¿Estás seguro de que quieres borrarlas permanentemente?");

            if (confirm.showAndWait().get() == ButtonType.OK) {
                Configuracion.getInstancia().getGestionGastos().borrarNotificacion(seleccionadas);
                cargarDatos(); 
            } else {
                System.out.println("Borrado cancelado por el usuario.");
            }
        } else {
            System.out.println("Intento de borrado sin selección.");
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setContentText("Por favor, selecciona al menos una notificación para borrar.");
            alert.showAndWait();
        }
    }

    @FXML
    public void cerrar(ActionEvent event) {
        System.out.println(">>> Acción de Usuario: Click en 'Cerrar Historial'");
        Stage stage = (Stage) listaNotificaciones.getScene().getWindow();
        stage.close();
    }
}