package tds.gestiongastos.main;

import tds.gestiongastos.controlador.GestionGastos;
import tds.gestiongastos.vista.SceneManager;

public abstract class Configuracion {

	private static Configuracion instancia;
	private final SceneManager sceneManager = new SceneManager();
	
	public static Configuracion getInstancia() {
		return Configuracion.instancia;
	}

	static void setInstancia(Configuracion impl) {
		Configuracion.instancia = impl; 
	}

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public abstract GestionGastos getGestionGastos();
}
