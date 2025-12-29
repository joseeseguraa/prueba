package tds.gestiongastos.vista;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.ParticipanteCuenta;
import tds.gestiongastos.modelo.CuentaCompartida;

public class GastoVistaControlador {

    @FXML private VBox contenedorPagador;
    @FXML private ComboBox<ParticipanteCuenta> comboGastoUsuario;
    @FXML private TextField txtGastoImporte;
    @FXML private TextField txtGastoDescripcion;
    @FXML private DatePicker dateGastoFecha;
    @FXML private ComboBox<Categoria> comboGastoCategoria;
    private Gasto gastoEnEdicion;

    @FXML
    public void initialize() {
        TipoCuenta cuentaActiva = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        if (contenedorPagador != null) {
            boolean esCompartida = cuentaActiva instanceof CuentaCompartida;
            contenedorPagador.setVisible(esCompartida);
            contenedorPagador.setManaged(esCompartida);
            if (esCompartida) configurarComboUsuarios(((CuentaCompartida) cuentaActiva).getParticipantes());
        }
        configurarComboCategorias();
        if (gastoEnEdicion == null) dateGastoFecha.setValue(LocalDate.now());
        Platform.runLater(() -> { if (txtGastoImporte.getParent() != null) txtGastoImporte.getParent().requestFocus(); });
    }

    private void configurarComboUsuarios(List<ParticipanteCuenta> participantes) {
        comboGastoUsuario.setItems(FXCollections.observableArrayList(participantes));
        
        comboGastoUsuario.setButtonCell(new ListCell<ParticipanteCuenta>() {
            @Override
            protected void updateItem(ParticipanteCuenta item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Seleccionar Pagador...");
                    setStyle("-fx-text-fill: gray;");
                } else {
                    setText(item.getNombre());
                    setStyle("-fx-text-fill: black;");
                }
            }
        });
        
        comboGastoUsuario.setConverter(new StringConverter<ParticipanteCuenta>() {
            @Override public String toString(ParticipanteCuenta p) { return p == null ? "" : p.getNombre(); }
            @Override public ParticipanteCuenta fromString(String s) { return null; }
        });
    }

    private void configurarComboCategorias() {
        comboGastoCategoria.setItems(FXCollections.observableArrayList(Configuracion.getInstancia().getGestionGastos().getTodasCategorias()));
        
        comboGastoCategoria.setButtonCell(new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Seleccionar...");
                    setStyle("-fx-text-fill: gray;");
                } else {
                	String nombreCompleto = item.getNombre();
                    int index = nombreCompleto.indexOf("_");
                    setText((index != -1) ? nombreCompleto.substring(index + 1) : nombreCompleto);
                    setStyle("-fx-text-fill: black;");
                }
            }
        });
        
        comboGastoCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    String nombre = item.getNombre();
                    int index = nombre.indexOf("_");
                    setText((index != -1) ? nombre.substring(index + 1) : nombre);
                }
            }
        });
        comboGastoCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                if (c == null) return "";
                String nombre = c.getNombre();
                int index = nombre.indexOf("_");
                return (index != -1) ? nombre.substring(index + 1) : nombre;
            }
            @Override public Categoria fromString(String s) { return null; }
        });
    }

    public void setGasto(Gasto gasto) {
        this.gastoEnEdicion = gasto;
        if (gasto != null) {
            txtGastoImporte.setText(String.valueOf(gasto.getCantidad()));
            txtGastoDescripcion.setText(gasto.getDescripcion());
            dateGastoFecha.setValue(gasto.getFecha());
            comboGastoCategoria.setValue(gasto.getCategoria());
            if (contenedorPagador != null && contenedorPagador.isManaged()) {
                comboGastoUsuario.getItems().stream()
                    .filter(p -> p.getNombre().equals(gasto.getPagador()))
                    .findFirst().ifPresent(comboGastoUsuario::setValue);
            }
        }
    }

    @FXML
    public void guardarGasto(ActionEvent event) {
        try {
            if (txtGastoImporte.getText().isEmpty() || dateGastoFecha.getValue() == null || comboGastoCategoria.getValue() == null) {
                mostrarAlerta("Campos vacíos", "Rellena los campos obligatorios.", AlertType.WARNING);
                return;
            }

            double importe = Double.parseDouble(txtGastoImporte.getText());
            String desc = txtGastoDescripcion.getText().isEmpty() ? "Sin concepto" : txtGastoDescripcion.getText();
            
            String nombreCatInterno = comboGastoCategoria.getValue().getNombre();
            
            String pagador = null;
            TipoCuenta cuentaActiva = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
            if (cuentaActiva instanceof CuentaCompartida) {
                if (comboGastoUsuario.getValue() == null) {
                    mostrarAlerta("Falta Pagador", "Selecciona quién pagó.", AlertType.WARNING);
                    return;
                }
                pagador = comboGastoUsuario.getValue().getNombre();
            }

            if (gastoEnEdicion == null) {
                List<String> avisos = Configuracion.getInstancia().getGestionGastos()
                        .registrarGasto(desc, importe, dateGastoFecha.getValue(), nombreCatInterno, pagador);
                if (!avisos.isEmpty()) {
                	String nombreCuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva().getNombre();
                    String prefijo = nombreCuenta + "_";

                    List<String> avisosLimpios = avisos.stream()
                            .map(msg -> msg.replace(prefijo, ""))
                            .collect(Collectors.toList());

                    mostrarAlerta("Límite Excedido", String.join("\n\n", avisosLimpios), AlertType.WARNING);
                }
            } else {
                Configuracion.getInstancia().getGestionGastos().modificarGasto(gastoEnEdicion, desc, importe, dateGastoFecha.getValue(), nombreCatInterno, pagador);
            }
            cerrarVentana();
        } catch (Exception e) { 
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage(), AlertType.ERROR); 
        }
    }

    @FXML public void cancelarRegistro(ActionEvent event) { cerrarVentana(); }
    @FXML public void botonCategorias(ActionEvent event) { Configuracion.getInstancia().getSceneManager().showNuevaCategoria(); configurarComboCategorias(); }
    private void cerrarVentana() { ((Stage) txtGastoImporte.getScene().getWindow()).close(); }
    private void mostrarAlerta(String titulo, String mensaje, AlertType tipo) {
        Alert a = new Alert(tipo); a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje); a.showAndWait();
    }
}