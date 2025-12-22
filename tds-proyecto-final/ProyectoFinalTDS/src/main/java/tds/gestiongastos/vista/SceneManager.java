package tds.gestiongastos.vista;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tds.gestiongastos.modelo.Gasto;

public class SceneManager {

	private Stage stage;
    private Scene scenaActual;

    public void inicializar(Stage stage) {
        this.stage = stage;
    }

    public void showVentanaPrincipal() {
        cargarYMostar("main");
    }

    public void showCuentaPersonal() {
    	cargarYMostar("cuenta_personal");
    }

    public void showCrearCuentaCompartida() {
    	cargarYMostarDialogo("crearCompartida", "Nueva Cuenta Compartida");
    }

    public void showNuevoGasto() {
    	cargarYMostarDialogo("nuevo_gasto", "Registrar Nuevo Gasto");
    }

    public void showNuevaCategoria() {
    	cargarYMostarDialogo("nueva_categoria", "Nueva Categoría");
    }

    public void showNuevaAlerta() {
    	cargarYMostarDialogo("nueva_alerta", "Crear Alerta");
    }

    public void showEstadoLimites() {
        cargarYMostarDialogo("verLimitesYAlertas", "Estado de Límites");
    }
    
    public void showCalendario() {
    	cargarYMostarDialogo("calendario", "Abrir Calendario");
    }

    public void showEditarGasto(Gasto gastoAEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/nuevo_gasto.fxml"));
            Parent root = loader.load();
            
            GastoVistaControlador controller = loader.getController();
            controller.setGasto(gastoAEditar);
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Editar Gasto");
            dialog.initStyle(StageStyle.UTILITY);
            dialog.getDialogPane().setContent(root);
            dialog.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarYMostarDialogo(String fxml, String titulo) {
        try {
            Parent root = loadFXML(fxml);

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle(titulo);
            dialog.initStyle(StageStyle.UTILITY);

            dialog.getDialogPane().setContent(root);

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

	private void cargarYMostar(String fxml) {
        try {
        	Parent root = loadFXML(fxml);
        	if (scenaActual == null) {
    			scenaActual = new Scene(root);
    	        stage.setScene(scenaActual);
    	        stage.show();
        	} else {
        		scenaActual.setRoot(root);
        	}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

	private Parent loadFXML(String fxml) throws IOException {
	    String ruta = "/" + fxml + ".fxml";

	    URL url = getClass().getResource(ruta);

	    if (url == null) {
	        url = getClass().getResource("/tds/gestiongastos/vista/" + fxml + ".fxml");
	    }

	    if (url == null) {
	        throw new IllegalStateException("No se encuentra el archivo FXML: " + fxml);
	    }

	    FXMLLoader loader = new FXMLLoader(url);
	    return loader.load();
	}
}