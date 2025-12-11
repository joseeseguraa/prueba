package tds.gestiongastos.vista;

import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;

public class CuentaPersonalVistaControlador {

    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, Double> colImporte;

    @FXML
    public void initialize() {
        System.out.println("Inicializando tabla de gastos...");
        
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCategoria.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategoria().getNombre()));

        cargarDatosTabla();
    }

    private void cargarDatosTabla() {
        TipoCuenta cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        
        if (cuenta != null) {
            System.out.println("Cargando gastos de: " + cuenta.getNombre());
            ObservableList<Gasto> lista = FXCollections.observableArrayList(cuenta.getGastos());
            tablaGastos.setItems(lista);
        } else {
            System.out.println("AVISO: No hay cuenta activa seleccionada.");
        }
    }

    @FXML
    public void botonVolver(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    
    @FXML
    public void abrirVentanaNuevoGasto(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
    }

    @FXML public void botonImportarDatos(ActionEvent event) {}
    @FXML public void botonHistorialNotificaciones(ActionEvent event) {}
    @FXML public void botonVolverCuentaPersonal(ActionEvent event) {}
    @FXML public void botonAlertas(ActionEvent event) {}
    @FXML public void botonGrafico(ActionEvent event) {}
    @FXML public void botonCalendario(ActionEvent event) {}
    @FXML public void botonCategorias(ActionEvent event) {}
    @FXML public void botonEliminarGasto(ActionEvent event) {}
    @FXML public void botonAplicarFiltro(ActionEvent event) {}
}