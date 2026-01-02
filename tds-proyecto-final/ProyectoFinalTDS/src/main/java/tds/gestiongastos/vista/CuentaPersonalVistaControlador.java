package tds.gestiongastos.vista;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.chart.PieChart;
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
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;

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
    @FXML private PieChart graficoCategorias;

    private static final String SEPARADOR = "--------------------------------------------------";

    @FXML
    public void initialize() {
        System.out.println("Inicializando Vista Cuenta Personal...");
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
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
        cargarDatosTabla(null);
        System.out.println(SEPARADOR);
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
        
        comboCategoriasFiltro.setCellFactory(lv -> new ListCell<Categoria>() {
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
            @Override
            public Categoria fromString(String string) { return null; }
        });
    }
    
    private void actualizarGrafico() {
        Map<String, Double> totalesPorCategoria = tablaGastos.getItems().stream()
            .collect(Collectors.groupingBy(
                g -> {
                    String nombre = g.getCategoria().getNombre();
                    int index = nombre.indexOf("_");
                    return (index != -1) ? nombre.substring(index + 1) : nombre;
                },
                Collectors.summingDouble(Gasto::getCantidad)
            ));

        ObservableList<PieChart.Data> datosGrafico = FXCollections.observableArrayList();
        totalesPorCategoria.forEach((nombre, total) -> {
            datosGrafico.add(new PieChart.Data(nombre, total));
        });

        graficoCategorias.setAnimated(false); 
        graficoCategorias.setLegendVisible(true);
        graficoCategorias.setLegendSide(Side.BOTTOM);

        graficoCategorias.setLabelsVisible(true); 

        graficoCategorias.setLabelLineLength(10.0);
        graficoCategorias.setStartAngle(90);
        
        graficoCategorias.setData(datosGrafico);

        Platform.runLater(() -> {
            double totalGlobal = datosGrafico.stream().mapToDouble(PieChart.Data::getPieValue).sum();

            for (PieChart.Data data : graficoCategorias.getData()) {
                if (data.getNode() != null) {

                    double porcentaje = (data.getPieValue() / totalGlobal) * 100;
                    String textoTooltip = String.format("%s\n%.2f €\n(%.1f%%)", 
                                                        data.getName(), 
                                                        data.getPieValue(), 
                                                        porcentaje);
                    
                    Tooltip tt = new Tooltip(textoTooltip);
                    tt.setStyle("-fx-font-size: 14px; -fx-background-color: #333; -fx-text-fill: white; -fx-font-weight: bold;");
                    Tooltip.install(data.getNode(), tt);
                    
                    data.getNode().setOnMouseEntered(e -> {
                        data.getNode().setOpacity(0.7); 
                        data.getNode().setCursor(Cursor.HAND);
                    });

                    data.getNode().setOnMouseExited(e -> {
                        data.getNode().setOpacity(1.0);
                        data.getNode().setCursor(Cursor.DEFAULT);
                    });
                    
                }
            }
            
            graficoCategorias.setLabelsVisible(false);
            graficoCategorias.setLabelsVisible(true);
        });
    }


    private void cargarDatosTabla(List<Gasto> datosFiltrados) {
        var cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        var lista = (datosFiltrados != null) 
            ? FXCollections.observableArrayList(datosFiltrados) 
            : (cuenta != null ? FXCollections.observableArrayList(cuenta.getGastos()) : FXCollections.<Gasto>observableArrayList());
        
        boolean hanCambiadoLosDatos = !lista.equals(tablaGastos.getItems());
        
        tablaGastos.setItems(lista);
        actualizarEtiquetaTotal(lista);
        if(hanCambiadoLosDatos) {
        	System.out.println("Actualizamos Gráfica");
        	actualizarGrafico();
        }
    }

    private void actualizarEtiquetaTotal(List<Gasto> listaGastos) {
        if (lblGastoTotal != null) {
            double total = listaGastos.stream().mapToDouble(Gasto::getCantidad).sum();
            lblGastoTotal.setText(String.format("%.2f €", total));
        }
    }

    @FXML public void botonVolver(ActionEvent event) {
        System.out.println(">>> Acción: Volver al menú principal");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal(); 
    }

    @FXML public void abrirVentanaNuevoGasto(ActionEvent event) {
        System.out.println(">>> Acción: Abrir ventana de nuevo gasto");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
        cargarDatosTabla(null);
        cargarCategoriasEnCombo();
    }

    @FXML public void botonEliminarGasto(ActionEvent event) {
        System.out.println(">>> Acción: Click en Eliminar Gasto");
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
            System.out.println("Confirmada eliminación de " + seleccionados.size() + " gastos.");
            Configuracion.getInstancia().getGestionGastos().borrarGastos(new ArrayList<>(seleccionados));
            cargarDatosTabla(null);
        } else {
            System.out.println("Eliminación cancelada.");
        }
        System.out.println(SEPARADOR);
    }

    @FXML public void botonAplicarFiltro(ActionEvent event) {
        System.out.println(">>> Acción: Aplicar filtros de búsqueda");
        List<Gasto> filtrados = Configuracion.getInstancia().getGestionGastos().filtrarGastos(dateDesde.getValue(), dateHasta.getValue(), comboCategoriasFiltro.getValue(), null);
        cargarDatosTabla(filtrados);
        System.out.println(SEPARADOR);
    }
    
    @FXML public void botonLimpiarFiltros(ActionEvent event) {
        System.out.println(">>> Acción: Limpiar filtros");
        dateDesde.setValue(null);
        dateHasta.setValue(null);
        comboCategoriasFiltro.getSelectionModel().clearSelection(); 
        comboCategoriasFiltro.setValue(null);
        cargarDatosTabla(null);
        System.out.println(SEPARADOR);
    }
    
    @FXML public void botonEditarGasto(ActionEvent event) {
        System.out.println(">>> Acción: Click en Editar Gasto");
        var seleccionados = tablaGastos.getSelectionModel().getSelectedItems();

        if (seleccionados.isEmpty()) {
            mostrarAviso("Atención", "Por favor, selecciona un gasto de la lista para editar.");
            System.out.println(SEPARADOR);
            return;
        }

        if (seleccionados.size() > 1) {
            mostrarAviso("Selección Múltiple", "Has seleccionado " + seleccionados.size() + " gastos.\nLa edición solo permite modificar los gastos de uno en uno.");
            System.out.println(SEPARADOR);
            return;
        }

        Gasto gastoAEditar = seleccionados.get(0);
        System.out.println("Abriendo editor para: " + gastoAEditar.getDescripcion());
        Configuracion.getInstancia().getSceneManager().showEditarGasto(gastoAEditar);
        cargarDatosTabla(null);
        System.out.println(SEPARADOR);
    }

    @FXML public void botonCategorias(ActionEvent event) {
        System.out.println(">>> Acción: Gestionar Categorías");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
        cargarCategoriasEnCombo();
    }

    @FXML public void botonNuevaAlerta(ActionEvent event) { 
        System.out.println(">>> Acción: Gestionar Alertas");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showNuevaAlerta(); 
    }
    
    @FXML public void botonVerLimites(ActionEvent event) { 
        System.out.println(">>> Acción: Ver Estado Límites");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showEstadoLimites(); 
    }
    
    @FXML public void botonHistorialNotificaciones(ActionEvent event) { 
        System.out.println(">>> Acción: Historial Notificaciones");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showHistorialNotificaciones(); 
    }
    
    @FXML public void botonImportarDatos(ActionEvent event) {
        System.out.println(">>> Acción: Importar Gastos");
        System.out.println(SEPARADOR);
        Configuracion.getInstancia().getSceneManager().showImportarGastos();
        cargarDatosTabla(null);
        cargarCategoriasEnCombo();
    }

    @FXML public void botonGrafico(ActionEvent event) {
        System.out.println(">>> Acción: Ver Gráficas (Botón pulsado)");
        Configuracion.getInstancia().getSceneManager().showGrafico();        
        System.out.println(SEPARADOR);
    }
    
    @FXML public void botonCalendario(ActionEvent event) {
        System.out.println(">>> Acción: Ver Calendario (Botón pulsado)");
        Configuracion.getInstancia().getSceneManager().showCalendario();
        System.out.println(SEPARADOR);
    }

    private void mostrarAviso(String titulo, String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}