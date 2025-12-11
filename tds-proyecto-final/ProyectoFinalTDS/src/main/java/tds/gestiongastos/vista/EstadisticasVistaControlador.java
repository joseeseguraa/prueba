package tds.gestiongastos.vista;

import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import tds.gestiongastos.main.Configuracion;

public class EstadisticasVistaControlador {

    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFin;
    @FXML private PieChart pieChartCategorias;
    @FXML private BarChart<String, Number> barChartEvolucion;

    @FXML
    public void initialize() {
        actualizarGraficos(null);
    }

    @FXML
    public void actualizarGraficos(ActionEvent event) {
        pieChartCategorias.getData().clear();
        barChartEvolucion.getData().clear();

        // gastos por categoria en el piechart
        Map<String, Double> gastosPorCategoria = Configuracion.getInstancia().getGestionGastos().obtenerGastosPorCategoria();
        ObservableList<PieChart.Data> datosPie = FXCollections.observableArrayList();
        
        gastosPorCategoria.forEach((cat, importe) -> {
            datosPie.add(new PieChart.Data(cat, importe));
        });
        pieChartCategorias.setData(datosPie);

        // por fecha en barchart
        Map<String, Double> gastosPorFecha = Configuracion.getInstancia().getGestionGastos().obtenerGastosPorFecha();
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Gastos Totales");
        
        gastosPorFecha.forEach((fecha, importe) -> {
            serie.getData().add(new XYChart.Data<>(fecha, importe));
        });
        
        barChartEvolucion.getData().add(serie);
    }

    @FXML public void botonVolver(ActionEvent event) { Configuracion.getInstancia().getSceneManager().showCuentaPersonal(); }
    @FXML public void botonVolverCuentaPersonal(ActionEvent event) { Configuracion.getInstancia().getSceneManager().showCuentaPersonal(); }
    @FXML public void botonAlertas(ActionEvent event) { Configuracion.getInstancia().getSceneManager().showNuevaAlerta(); }
    @FXML public void botonGrafico(ActionEvent event) { /* Ventana actual */ }
    @FXML public void botonCalendario(ActionEvent event) { Configuracion.getInstancia().getSceneManager().showCalendario(); }
    @FXML public void botonCategorias(ActionEvent event) { Configuracion.getInstancia().getSceneManager().showNuevaCategoria(); }
}