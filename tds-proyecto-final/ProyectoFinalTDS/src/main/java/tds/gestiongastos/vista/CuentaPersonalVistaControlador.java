package tds.gestiongastos.vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import tds.gestiongastos.main.Configuracion;

public class CuentaPersonalVistaControlador {

    @FXML
    public void initialize() {
        System.out.println("Vista Personal cargada con éxito.");
    }

    @FXML
    public void botonVolver(ActionEvent event) {
        System.out.println("Volviendo al menú principal...");
        Configuracion.getInstancia().getSceneManager().showVentanaPrincipal();
    }

    @FXML
    public void botonImportarDatos(ActionEvent event) {
        System.out.println("Importar datos...");
    }

    @FXML
    public void botonHistorialNotificaciones(ActionEvent event) {
        System.out.println("Ver notificaciones...");
    }

    @FXML
    public void botonVolverCuentaPersonal(ActionEvent event) {
        System.out.println("Ya estás en el Dashboard.");
    }

    @FXML
    public void botonAlertas(ActionEvent event) {
        System.out.println("Ir a Alertas...");
        // Configuracion.getInstancia().getSceneManager().showAlertas();
    }

    @FXML
    public void botonGrafico(ActionEvent event) {
        System.out.println("Ir a Gráficos...");
    }

    @FXML
    public void botonCalendario(ActionEvent event) {
        System.out.println("Ir a Calendario...");
    }

    @FXML
    public void botonCategorias(ActionEvent event) {
        System.out.println("Ir a Categorías...");
    }

    @FXML
    public void botonEliminarGasto(ActionEvent event) {
        System.out.println("Eliminar gasto seleccionado...");
    }

    @FXML
    public void abrirVentanaNuevoGasto(ActionEvent event) {
        System.out.println("Abriendo diálogo nuevo gasto...");
        Configuracion.getInstancia().getSceneManager().showNuevoGasto();
    }

    @FXML
    public void botonAplicarFiltro(ActionEvent event) {
        System.out.println("Aplicando filtros...");
    }
}