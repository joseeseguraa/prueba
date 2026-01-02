package tds.gestiongastos.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Gasto;

public class GraficaVistaControlador {

    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFin;
    @FXML private PieChart pieChartCategorias;
    @FXML private BarChart<String, Number> barChartEvolucion;

    @FXML
    public void initialize() {
        pieChartCategorias.setLegendSide(Side.BOTTOM);
        pieChartCategorias.setLabelsVisible(true);
        pieChartCategorias.setStartAngle(90);
        cargarDatosGraficos();
    }

    @FXML
    public void botonAplicarFiltro(ActionEvent event) {
        cargarDatosGraficos();
    }

    @FXML
    public void botonLimpiarFiltros(ActionEvent event) {
        dateInicio.setValue(null);
        dateFin.setValue(null);
        cargarDatosGraficos();
    }

    private void cargarDatosGraficos() {
        pieChartCategorias.getData().clear();
        barChartEvolucion.getData().clear();
        barChartEvolucion.setAnimated(false);

        LocalDate inicio = dateInicio.getValue();
        LocalDate fin = dateFin.getValue();

        if (inicio != null && fin != null && inicio.isAfter(fin)) {
            mostrarAviso("Fechas incorrectas", "La fecha de inicio no puede ser posterior a la fecha de fin.");
            return;
        }

        var listaGastos = Configuracion.getInstancia().getGestionGastos()
                .filtrarGastos(inicio, fin, null, null);

        if (listaGastos == null || listaGastos.isEmpty()) {
            return;
        }

        Map<String, Double> gastosPorCategoria = listaGastos.stream()
                .collect(Collectors.groupingBy(
                        g -> limpiarNombre(g.getCategoria().getNombre()),
                        Collectors.summingDouble(Gasto::getCantidad)
                ));

        ObservableList<PieChart.Data> datosPie = FXCollections.observableArrayList();
        gastosPorCategoria.forEach((cat, importe) ->
                datosPie.add(new PieChart.Data(cat, importe))
        );

        pieChartCategorias.setData(datosPie);
        instalarTooltipsTarta();

        Map<LocalDate, Double> gastosPorFecha = listaGastos.stream()
                .collect(Collectors.groupingBy(
                        Gasto::getFecha,
                        TreeMap::new,
                        Collectors.summingDouble(Gasto::getCantidad)
                ));

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        gastosPorFecha.forEach((fecha, importe) ->
                serie.getData().add(new XYChart.Data<>(fecha.format(fmt), importe))
        );

        barChartEvolucion.getData().add(serie);
        instalarTooltipsBarras(serie);
    }

    private void instalarTooltipsTarta() {
        Platform.runLater(() -> {
            double total = pieChartCategorias.getData().stream()
                    .mapToDouble(PieChart.Data::getPieValue)
                    .sum();

            for (PieChart.Data data : pieChartCategorias.getData()) {
                if (data.getNode() != null) {


                    double porcentaje = (data.getPieValue() / total) * 100;
                    String texto = String.format("%s\n%.2f € (%.1f%%)",
                            data.getName(), data.getPieValue(), porcentaje);

                    Tooltip tt = new Tooltip(texto);
                    tt.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #333;");
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
        });
    }

    private void instalarTooltipsBarras(XYChart.Series<String, Number> serie) {
        for (XYChart.Data<String, Number> data : serie.getData()) {
            Tooltip t = new Tooltip(data.getYValue() + " €");
            t.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: #333;");
            Tooltip.install(data.getNode(), t);
            data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-bar-fill: #ff9900;"));
            data.getNode().setOnMouseExited(e -> data.getNode().setStyle(""));
        }
    }

    private String limpiarNombre(String nombre) {
        if (nombre == null) return "Sin Cat.";
        int index = nombre.indexOf("_");
        return (index != -1) ? nombre.substring(index + 1) : nombre;
    }

    private void mostrarAviso(String titulo, String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML public void botonVolver(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }

    @FXML public void botonVolverCuentaPersonal(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showCuentaPersonal();
    }

    @FXML public void botonAlertas(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showNuevaAlerta();
    }

    @FXML public void botonGrafico(ActionEvent event) {
    }

    @FXML public void botonCalendario(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showCalendario();
    }

    @FXML public void botonCategorias(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
    }
}
