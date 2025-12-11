package tds.gestiongastos.vista;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    	cargarYMostar("crearCompartida");
    }

    
    public void showNuevoGasto() {
    	cargarYMostarDialogo("nuevo_gasto", "Registrar Nuevo Gasto");
    }

    public void showNuevaCategoria() {
    	cargarYMostarDialogo("nueva_categoria", "Nueva Categoría");
    }

    public void showNuevaAlerta() {
    	cargarYMostarDialogo("nueva_alerta.fxml", "Crear Alerta");
    }

    
    
    private void cargarYMostarDialogo(String fxml, String titulo) {
        try {
        	DialogPane pane = (DialogPane) loadFXML(fxml);
            Dialog<Void> dialog = new Dialog<>();
            dialog.setDialogPane(pane);
            dialog.setTitle(titulo);
            dialog.initStyle(StageStyle.UTILITY);

            dialog.showAndWait();
		} catch (IOException e) {
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