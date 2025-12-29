package tds.gestiongastos.vista;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.ParticipanteCuenta;
import tds.gestiongastos.modelo.TipoCuenta;

public class CuentasCompartidasVistaControlador {

    @FXML private TableView<Gasto> tablaGastos;
    @FXML private TableColumn<Gasto, String> colId;
    @FXML private TableColumn<Gasto, String> colUsuario;
    @FXML private TableColumn<Gasto, LocalDate> colFecha;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, Double> colImporte;
    @FXML private DatePicker dateDesde;
    @FXML private DatePicker dateHasta;
    @FXML private ComboBox<Categoria> comboCategoriasFiltro;
    @FXML private ComboBox<ParticipanteCuenta> comboFiltroUsuario;
    @FXML private Label lblGastoTotal;

    private static final String SEPARADOR = "--------------------------------------------------";

    @FXML
    public void initialize() {
        System.out.println("Inicializando Vista Cuenta Compartida...");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPagador()));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colImporte.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        
        colCategoria.setCellValueFactory(cellData -> {
            String nombreInterno = cellData.getValue().getCategoria().getNombre();
            int index = nombreInterno.indexOf("_");
            return new SimpleStringProperty((index != -1) ? nombreInterno.substring(index + 1) : nombreInterno);
        });

        tablaGastos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tablaGastos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        
        cargarCategoriasEnCombo();
        cargarParticipantesEnCombo();
        cargarDatosTabla(null);
        System.out.println(SEPARADOR);
    }

    private void cargarParticipantesEnCombo() {
        TipoCuenta cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        if (cuenta instanceof CuentaCompartida) {
            comboFiltroUsuario.setItems(FXCollections.observableArrayList(((CuentaCompartida) cuenta).getParticipantes()));
            
            comboFiltroUsuario.setButtonCell(new ListCell<ParticipanteCuenta>() {
                @Override
                protected void updateItem(ParticipanteCuenta item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((empty || item == null) ? "Todos" : item.getNombre());
                }
            });
            
            comboFiltroUsuario.setConverter(new StringConverter<ParticipanteCuenta>() {
                @Override public String toString(ParticipanteCuenta p) { return p == null ? "Todos" : p.getNombre(); }
                @Override public ParticipanteCuenta fromString(String string) { return null; }
            });
        }
    }
    
    private void cargarCategoriasEnCombo() {
        List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        comboCategoriasFiltro.setItems(FXCollections.observableArrayList(categorias));
        
        comboCategoriasFiltro.setButtonCell(new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Todas");
                } else {
                    String nombre = item.getNombre();
                    int index = nombre.indexOf("_");
                    setText((index != -1) ? nombre.substring(index + 1) : nombre);
                }
            }
        });
        
        comboCategoriasFiltro.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                if (c == null) return "Todas";
                String nombre = c.getNombre();
                int index = nombre.indexOf("_");
                return (index != -1) ? nombre.substring(index + 1) : nombre;
            }
            @Override public Categoria fromString(String string) { return null; }
        });
    }

    private void cargarDatosTabla(List<Gasto> datosFiltrados) {
        TipoCuenta cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        ObservableList<Gasto> lista = FXCollections.observableArrayList(datosFiltrados != null ? datosFiltrados : (cuenta != null ? cuenta.getGastos() : List.of()));
        tablaGastos.setItems(lista);
        actualizarEtiquetaTotal(lista);
    }

    @FXML public void botonLimpiarFiltros(ActionEvent event) {
        System.out.println(">>> Acción: Limpiar Filtros");
        dateDesde.setValue(null);
        dateHasta.setValue(null);
        comboCategoriasFiltro.setValue(null);
        comboFiltroUsuario.setValue(null);
        cargarDatosTabla(null);
        System.out.println(SEPARADOR);
    }

    private void actualizarEtiquetaTotal(List<Gasto> listaGastos) {
        if (lblGastoTotal != null) {
            double total = listaGastos.stream().mapToDouble(Gasto::getCantidad).sum();
            lblGastoTotal.setText(String.format("%.2f €", total));
        }
    }

    @FXML public void botonVolver(ActionEvent event) {
        System.out.println(">>> Acción: Volver");
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal(); 
        System.out.println(SEPARADOR);
    }
    
    
    @FXML public void abrirVentanaNuevoGasto(ActionEvent event) {
        System.out.println(">>> Acción: Nuevo Gasto Compartido");
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
        cargarDatosTabla(null);     
        System.out.println(SEPARADOR);
    }
    
    @FXML public void botonEliminarGasto(ActionEvent event) {
        System.out.println(">>> Acción: Eliminar Gasto");
        var seleccionados = tablaGastos.getSelectionModel().getSelectedItems();
        if (seleccionados.isEmpty()) {
            mostrarAviso("Atención", "Selecciona al menos un gasto para eliminar.");
            System.out.println(SEPARADOR);
            return;
        }
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar los " + seleccionados.size() + " gastos seleccionados?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            Configuracion.getInstancia().getGestionGastos().borrarGastos(new ArrayList<>(seleccionados));
            cargarDatosTabla(null);
        }       
        System.out.println(SEPARADOR);
    }

    @FXML public void botonAplicarFiltro(ActionEvent event) {
        System.out.println(">>> Acción: Aplicar Filtro");
        ParticipanteCuenta p = comboFiltroUsuario.getValue();
        String nombrePagador = (p != null) ? p.getNombre() : null;
        
        cargarDatosTabla(Configuracion.getInstancia().getGestionGastos().filtrarGastos(dateDesde.getValue(), dateHasta.getValue(), comboCategoriasFiltro.getValue(), nombrePagador));
        System.out.println(SEPARADOR);
    }
    
    @FXML public void botonEditarGasto(ActionEvent event) {
        System.out.println(">>> Acción: Editar Gasto");
        ObservableList<Gasto> seleccionados = tablaGastos.getSelectionModel().getSelectedItems();

        if (seleccionados.isEmpty()) {
            mostrarAviso("Atención", "Por favor, selecciona un gasto de la lista para editar.");
            System.out.println(SEPARADOR);
            return;
        }

        if (seleccionados.size() > 1) {
            mostrarAviso("Selección Múltiple", "Has seleccionado " + seleccionados.size() + " gastos.\n" +
                          "La edición solo permite modificar los gastos de uno en uno.");
            System.out.println(SEPARADOR);
            return;
        }

        Gasto gastoAEditar = seleccionados.get(0);
        
        String usuarioActual = Configuracion.getInstancia().getGestionGastos().getCuentaActiva().getNombre();

        if (!gastoAEditar.getPagador().equals(usuarioActual)) {
            mostrarAviso("Permiso Denegado", "Solo puedes editar los gastos que has pagado tú.\n" +
                          "Este gasto pertenece a: " + gastoAEditar.getPagador());
            System.out.println(SEPARADOR);
            return;
        }

        Configuracion.getInstancia().getSceneManager().showEditarGasto(gastoAEditar);
        
        cargarDatosTabla(null);
        System.out.println(SEPARADOR);
    }

    @FXML public void botonCategorias(ActionEvent event) {
        System.out.println(">>> Acción: Categorías");
        Configuracion.getInstancia().getSceneManager().showNuevaCategoria(); 
        cargarCategoriasEnCombo(); 
        System.out.println(SEPARADOR);
    }
    
    
    @FXML public void botonNuevaAlerta(ActionEvent event) { 
        System.out.println(">>> Acción: Nueva Alerta");
        Configuracion.getInstancia().getSceneManager().showNuevaAlerta(); 
        System.out.println(SEPARADOR);
    }
    @FXML public void botonVerLimites(ActionEvent event) { 
        System.out.println(">>> Acción: Ver Límites");
        Configuracion.getInstancia().getSceneManager().showEstadoLimites(); 
        System.out.println(SEPARADOR);
    }
    @FXML public void botonHistorialNotificaciones(ActionEvent event) { 
        System.out.println(">>> Acción: Historial Notificaciones");
        Configuracion.getInstancia().getSceneManager().showHistorialNotificaciones(); 
        System.out.println(SEPARADOR);
    }
    
    @FXML public void botonImportarDatos(ActionEvent event) {
        System.out.println(">>> Acción: Importar Gastos");
        Configuracion.getInstancia().getSceneManager().showImportarGastos();
        cargarDatosTabla(null); 
        cargarCategoriasEnCombo();
        System.out.println(SEPARADOR);
    }
    
    
    private void mostrarAviso(String titulo, String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    @FXML public void botonGrafico(ActionEvent event) {
        System.out.println(">>> Acción: Ver Gráficas");
        System.out.println(SEPARADOR);
    }
    @FXML public void botonCalendario(ActionEvent event) {
        System.out.println(">>> Acción: Calendario");
        System.out.println(SEPARADOR);
    }
}