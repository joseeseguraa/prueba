package tds.gestiongastos.vista;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;

public class CuentaPersonalVistaControlador {

    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, Double> colImporte;

    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;
    @FXML private ComboBox<Categoria> comboCategoriasFiltro;
    
    @FXML private Label lblGastoTotal;
    
    @FXML
    public void initialize() {
        System.out.println("Inicializando tabla de gastos...");
        
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCategoria.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCategoria().getNombre()));

        cargarCategoriasEnCombo();
        cargarDatosTabla(null);
    }

    
    private void cargarCategoriasEnCombo() {
        List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        comboCategoriasFiltro.setItems(FXCollections.observableArrayList(categorias));
        
        comboCategoriasFiltro.setConverter(new StringConverter<Categoria>() {
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
    
    
    private void cargarDatosTabla(List<Gasto> datosFiltrados) {
        TipoCuenta cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        
        ObservableList<Gasto> lista;
        if (datosFiltrados != null) {
            lista = FXCollections.observableArrayList(datosFiltrados);
        } else if (cuenta != null) {
            lista = FXCollections.observableArrayList(cuenta.getGastos());
        } else {
            lista = FXCollections.observableArrayList();
        }
        tablaGastos.setItems(lista);

        actualizarEtiquetaTotal(lista);
    }
    
    private void actualizarEtiquetaTotal(List<Gasto> listaGastos) {
        if (lblGastoTotal != null) {
            double total = listaGastos.stream()
                                      .mapToDouble(Gasto::getCantidad)
                                      .sum();
            
            lblGastoTotal.setText(String.format("%.2f €", total));
        }
    }

    @FXML
    public void botonVolver(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }
    
    @FXML
    public void abrirVentanaNuevoGasto(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
        cargarDatosTabla(null);
    }
    
    
    @FXML 
    public void botonEliminarGasto(ActionEvent event) {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Configuracion.getInstancia().getGestionGastos().borrarGasto(seleccionado);
            cargarDatosTabla(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setContentText("Selecciona un gasto para eliminar.");
            alert.showAndWait();
        }
    }
    
    
    @FXML 
    public void botonAplicarFiltro(ActionEvent event) {
        LocalDate inicio = dateDesde.getValue();
        LocalDate fin = dateHasta.getValue();
        Categoria cat = comboCategoriasFiltro.getValue();
        
        List<Gasto> filtrados = Configuracion.getInstancia().getGestionGastos().filtrarGastos(inicio, fin, cat);
        cargarDatosTabla(filtrados);
    }
    

    @FXML public void botonImportarDatos(ActionEvent event) {}
    @FXML public void botonHistorialNotificaciones(ActionEvent event) {}
    @FXML public void botonVolverCuentaPersonal(ActionEvent event) {}
    @FXML public void botonAlertas(ActionEvent event) {}
    @FXML public void botonGrafico(ActionEvent event) {}
    @FXML public void botonCalendario(ActionEvent event) {}
    @FXML public void botonCategorias(ActionEvent event) {}
}