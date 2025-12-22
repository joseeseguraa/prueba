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
    @FXML private TableColumn<Gasto, String> colId;
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

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
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
            
            //TODO Hay que implementar el fromString 
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
    	System.out.println("Abriendo formulario para crear nuevo gasto...");
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
        cargarDatosTabla(null);
        cargarCategoriasEnCombo();
    }


    @FXML
    public void botonEliminarGasto(ActionEvent event) {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
        	System.out.println("Eliminando gasto seleccionado: " + seleccionado.getDescripcion() + " (" + seleccionado.getCantidad() + "€)");
            Configuracion.getInstancia().getGestionGastos().borrarGasto(seleccionado);
            cargarDatosTabla(null);
        } else {
        	System.out.println("Intento de eliminar sin seleccionar nada.");
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

        String fInicio = "Cualquiera";
        if (inicio != null) {
            fInicio = inicio.toString();
        }

        String fFin = "Cualquiera";
        if (fin != null) {
            fFin = fin.toString();
        }

        String nombreCat = "Todas";
        if (cat != null) {
            nombreCat = cat.getNombre();
        }

        System.out.println("Aplicando filtro -> Desde: " + fInicio + " | Hasta: " + fFin + " | Categoría: " + nombreCat);
        
        List<Gasto> filtrados = Configuracion.getInstancia().getGestionGastos().filtrarGastos(inicio, fin, cat);
        cargarDatosTabla(filtrados);
    }
    
    @FXML
    public void botonLimpiarFiltros(ActionEvent event) {
    	System.out.println("Limpiando filtros: Mostrando todos los gastos.");
    	
        dateDesde.setValue(null);
        dateHasta.setValue(null);
        comboCategoriasFiltro.getSelectionModel().clearSelection(); 
        comboCategoriasFiltro.setValue(null);
        
        cargarDatosTabla(null);
    }
    
    
    @FXML 
    public void botonEditarGasto(ActionEvent event) {
        Gasto seleccionado = tablaGastos.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
        	System.out.println("Editando gasto: " + seleccionado.getDescripcion());
            Configuracion.getInstancia().getSceneManager().showEditarGasto(seleccionado);
            
            cargarDatosTabla(null);
            
        } else {
        	System.out.println("No se ha seleccionado ningún gasto para editar.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setContentText("Selecciona un gasto de la tabla para editar.");
            alert.showAndWait();
        }
    }
    
    
    @FXML 
    public void botonCategorias(ActionEvent event) {
    	System.out.println("Navegando a: Gestión de Categorías");
    	Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
    	cargarCategoriasEnCombo();
    	
    }

    
    @FXML 
    public void botonNuevaAlerta(ActionEvent event) {
    	System.out.println("Navegando a: Crear Nueva Alerta");
    	Configuracion.getInstancia().getSceneManager().showNuevaAlerta();
    }

    @FXML
    public void botonVerLimites(ActionEvent event) {
    	System.out.println("Navegando a: Estado de Límites");
    	Configuracion.getInstancia().getSceneManager().showEstadoLimites();
    }
    
    
    @FXML public void botonImportarDatos(ActionEvent event) {}
    @FXML public void botonHistorialNotificaciones(ActionEvent event) {}
    @FXML public void botonVolverCuentaPersonal(ActionEvent event) {}
    @FXML public void botonGrafico(ActionEvent event) {}
    @FXML public void botonCalendario(ActionEvent event) {}
}