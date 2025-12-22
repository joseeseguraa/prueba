package tds.gestiongastos.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

    	Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);

        configuracion.getSceneManager().inicializar(stage);
        configuracion.getSceneManager().showVentanaPrincipal();
    }

    public static void main(String[] args) {
        launch();
    }
}