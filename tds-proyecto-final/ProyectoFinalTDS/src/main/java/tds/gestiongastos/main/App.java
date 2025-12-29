package tds.gestiongastos.main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

import tds.gestiongastos.vista.ConsolaGastos;

public class App extends Application {


    @Override
    public void start(Stage stage) throws IOException {

        Configuracion configuracion = new ConfiguracionImpl();
        Configuracion.setInstancia(configuracion);

        configuracion.getSceneManager().inicializar(stage);
        configuracion.getSceneManager().showVentanaPrincipal();
    }


    public static void main(String[] args) {
        List<String> listaArgs = Arrays.asList(args);

        if (listaArgs.contains("--terminal")) {
            System.out.println("Iniciando modo consola...");
            
            Configuracion configuracion = new ConfiguracionImpl();
            Configuracion.setInstancia(configuracion);
            
            new ConsolaGastos(configuracion.getGestionGastos()).ejecutar();
            
            System.exit(0); 
        } else {
            launch(args);
        }
    }
}