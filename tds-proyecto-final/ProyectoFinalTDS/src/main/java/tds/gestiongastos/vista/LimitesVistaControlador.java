package tds.gestiongastos.vista;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;

public class LimitesVistaControlador {

    @FXML private ListView<String> listaProgreso;
    @FXML private ComboBox<String> comboFiltroPeriodo;
    @FXML private ComboBox<Categoria> comboFiltroCategoria;

    @FXML
    public void initialize() {
        comboFiltroPeriodo.setItems(FXCollections.observableArrayList("Todas", "Mensual", "Semanal"));
        comboFiltroPeriodo.setValue("Todas");

        if (comboFiltroCategoria != null) {
            List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
            comboFiltroCategoria.getItems().add(null);
            comboFiltroCategoria.getItems().addAll(categorias);
            
            comboFiltroCategoria.setButtonCell(new ListCell<Categoria>() {
                @Override
                protected void updateItem(Categoria item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todas las Categorías");
                    } else {
                        String nombre = item.getNombre();
                        int index = nombre.indexOf("_");
                        setText((index != -1) ? nombre.substring(index + 1) : nombre);
                    }
                }
            });

            comboFiltroCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
                @Override
                protected void updateItem(Categoria item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todas las Categorías");
                    } else {
                        String nombre = item.getNombre();
                        int index = nombre.indexOf("_");
                        setText((index != -1) ? nombre.substring(index + 1) : nombre);
                    }
                }
            });
            
            comboFiltroCategoria.setConverter(new StringConverter<Categoria>() {
                @Override
                public String toString(Categoria c) {
                    if (c == null) return "Todas las Categorías";
                    String nombre = c.getNombre();
                    int index = nombre.indexOf("_");
                    return (index != -1) ? nombre.substring(index + 1) : nombre;
                }
                @Override public Categoria fromString(String s) { return null; }
            });
            
            comboFiltroCategoria.getSelectionModel().selectFirst();
        }

        Platform.runLater(this::cargarEstadoLimites);
    }

    @FXML
    public void aplicarFiltro(ActionEvent event) {
        cargarEstadoLimites();
    }

    private void cargarEstadoLimites() {
        TipoCuenta cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        if (cuenta == null) return;

        List<Alerta> alertas = Configuracion.getInstancia().getGestionGastos().getAlertas();
        List<Gasto> todosLosGastos = cuenta.getGastos();
        String filtroPeriodicidad = comboFiltroPeriodo.getValue();
        Categoria filtroCategoria = comboFiltroCategoria.getValue();

        List<String> items = new ArrayList<>();

        for (Alerta alerta : alertas) {
            if (!filtroPeriodicidad.equals("Todas") && !alerta.getTipo().equalsIgnoreCase(filtroPeriodicidad)) continue;
            if (filtroCategoria != null && (alerta.getCategoria() == null || !alerta.getCategoria().equals(filtroCategoria))) continue;

            double gastado = calcularGastoParaAlerta(alerta, todosLosGastos);
            
            String nombreCat = "General";
            if (alerta.getCategoria() != null) {
                String n = alerta.getCategoria().getNombre();
                int idx = n.indexOf("_");
                nombreCat = (idx != -1) ? n.substring(idx + 1) : n;
            }

            String estado = String.format("[%s] %s: %.2f € / %.2f €", alerta.getTipo(), nombreCat, gastado, alerta.getLimite());
            if (gastado > alerta.getLimite()) {
                estado += " EXCEDIDO";
            } else {
                double porcentaje = (alerta.getLimite() > 0) ? (gastado / alerta.getLimite()) * 100 : 0;
                estado += String.format(" (%.0f%%)", porcentaje);
            }
            items.add(estado);
        }

        if (items.isEmpty()) items.add("No se encontraron alertas.");
        listaProgreso.setItems(FXCollections.observableArrayList(items));
    }

    private double calcularGastoParaAlerta(Alerta alerta, List<Gasto> gastos) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = alerta.getTipo().equalsIgnoreCase("Mensual") ? 
                        hoy.withDayOfMonth(1) : hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        return gastos.stream()
                .filter(g -> !g.getFecha().isBefore(fechaInicio) && !g.getFecha().isAfter(hoy))
                .filter(g -> alerta.getCategoria() == null || g.getCategoria().equals(alerta.getCategoria()))
                .mapToDouble(Gasto::getCantidad).sum();
    }

    @FXML public void cerrar(ActionEvent event) { ((Stage) listaProgreso.getScene().getWindow()).close(); }
}