package tds.gestiongastos.vista;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.page.WeekPage;
import com.calendarfx.view.CalendarView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Gasto;

public class CalendarioControlador {

    @FXML private StackPane contenedorCalendario;
    
    @FXML private Label lblGastoTotal; 
    
    private CalendarView vistaMes;       
    private WeekPage vistaSemana;
    
    private boolean modoMensual = true; 

    @FXML
    public void initialize() {
    	
        configurarVistaMes();
        configurarVistaSemana();
 
        vistaMes.bind(vistaSemana, true);
        
        vistaMes.dateProperty().addListener((obs, viejaFecha, nuevaFecha) -> {
            actualizarTotal(); 
        });

        
        cargarDatos();
        mostrarVistaMes(null);
    }

    private void actualizarTotal() {
            double suma = 0.0;
            LocalDate fechaActual = vistaMes.getDate(); 
            var cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();

            if (cuenta != null) {
                List<Gasto> gastos = cuenta.getGastos();

                if (modoMensual) {
                    for (Gasto g : gastos) {
                        if (g.getFecha().getMonth() == fechaActual.getMonth() && 
                            g.getFecha().getYear() == fechaActual.getYear()) {
                            suma += g.getCantidad();
                        }
                    }
                } else {
                    LocalDate inicioSemana = fechaActual.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    LocalDate finSemana = fechaActual.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

                    for (Gasto g : gastos) {
                        LocalDate f = g.getFecha();
                        if (!f.isBefore(inicioSemana) && !f.isAfter(finSemana)) {
                            suma += g.getCantidad();
                        }
                    }
                }
            }

            lblGastoTotal.setText(String.format("%.2f €", suma));
    }

    private void configurarVistaMes() {
        vistaMes = new CalendarView();
        vistaMes.setShowAddCalendarButton(false);
        vistaMes.setShowPrintButton(false);
        vistaMes.setShowSearchField(false);
        vistaMes.setShowSourceTrayButton(false);
        vistaMes.setShowPageSwitcher(false); 
        vistaMes.getMonthPage().getMonthView().setShowWeekNumbers(false);
        vistaMes.showMonthPage();

        CalendarSource fuenteDatos = new CalendarSource("Datos");
        Calendar calendario = new Calendar("Gastos");
        calendario.setStyle(Calendar.Style.STYLE1);
        calendario.setReadOnly(true);
        
        fuenteDatos.getCalendars().add(calendario);
        vistaMes.getCalendarSources().setAll(fuenteDatos);
        vistaMes.setRequestedTime(LocalTime.now());
    }

    private void configurarVistaSemana() {
        vistaSemana = new WeekPage();
        
        vistaSemana.setShowLayoutButton(false);
        
        
        var vistaDetallada = vistaSemana.getDetailedWeekView();
        
        
        vistaDetallada.getWeekView().setVisible(false);
        vistaDetallada.getWeekView().setManaged(false); 
        vistaDetallada.getTimeScaleView().setVisible(false);
        vistaDetallada.getTimeScaleView().setManaged(false);
        
        vistaDetallada.setShowAllDayView(true);
        
        vistaSemana.setStyle("-fx-background-color: white;");
    }
    
    private void cargarDatos() {
        Calendar calendario = vistaMes.getCalendarSources().get(0).getCalendars().get(0);
        calendario.clear(); 

        var cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
        
        if (cuenta != null) {
            for (Gasto g : cuenta.getGastos()) {
                String nombreCat = g.getCategoria().getNombre();
                if (nombreCat.contains("_")) nombreCat = nombreCat.substring(nombreCat.indexOf("_") + 1);
                
                String texto = String.format("%s: %.2f€", nombreCat, g.getCantidad());
                Entry<String> entrada = new Entry<>(texto);
                entrada.changeStartDate(g.getFecha());
                entrada.changeEndDate(g.getFecha());
                entrada.setFullDay(true); 
                calendario.addEntry(entrada);
            }
        }
        actualizarTotal();
    }

    @FXML
    public void mostrarVistaMes(ActionEvent event) {
        modoMensual = true;
        contenedorCalendario.getChildren().clear();
        contenedorCalendario.getChildren().add(vistaMes);
        actualizarTotal();
    }

    @FXML
    public void mostrarVistaSemana(ActionEvent event) {
        modoMensual = false;
        contenedorCalendario.getChildren().clear();
        contenedorCalendario.getChildren().add(vistaSemana);
        actualizarTotal();
    }

    @FXML 
    public void botonVolver(ActionEvent event) { 
    	Configuracion.getInstancia().getSceneManager().showCuentaPersonal(); 
    }

    @FXML 
    public void botonCategorias(ActionEvent event) {
        Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
    }
    
    @FXML 
    public void botonNuevaAlerta(ActionEvent event) { 
        Configuracion.getInstancia().getSceneManager().showNuevaAlerta(); 
    }
    
    @FXML 
    public void botonVerLimites(ActionEvent event) { 
        Configuracion.getInstancia().getSceneManager().showEstadoLimites(); 
    }
    
    @FXML 
    public void botonGrafico(ActionEvent event) {
        
    }
    
    @FXML 
    public void botonVolverCuentaPersonal(ActionEvent event) { 
    	Configuracion.getInstancia().getSceneManager().showCuentaPersonal(); 
    }
    
    @FXML 
    public void botonCalendario(ActionEvent event) {
    }
}