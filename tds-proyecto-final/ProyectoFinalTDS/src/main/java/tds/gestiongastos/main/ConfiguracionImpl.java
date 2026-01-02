package tds.gestiongastos.main;

import tds.gestiongastos.adapters.repository.impl.RepositorioAlertasImpl; 
import tds.gestiongastos.adapters.repository.impl.RepositorioCategoriasImpl;
import tds.gestiongastos.adapters.repository.impl.RepositorioCuentasImpl;
import tds.gestiongastos.adapters.repository.impl.RepositorioGastosImpl;
import tds.gestiongastos.adapters.repository.impl.RepositorioNotificacionesImpl; 
import tds.gestiongastos.controlador.GestionGastos;

public class ConfiguracionImpl extends Configuracion {

	private GestionGastos gestionGastos;

    public ConfiguracionImpl() {

    	RepositorioCuentasImpl repoCuentas = new RepositorioCuentasImpl();
        RepositorioCategoriasImpl repoCategorias = new RepositorioCategoriasImpl();

        RepositorioGastosImpl repoGastos = new RepositorioGastosImpl(repoCuentas);

        RepositorioAlertasImpl repoAlertas = new RepositorioAlertasImpl();
        RepositorioNotificacionesImpl repoNotificaciones = new RepositorioNotificacionesImpl();

        this.gestionGastos = new GestionGastos(repoCuentas, repoGastos, repoCategorias, repoAlertas, repoNotificaciones);
    }

    @Override
    public GestionGastos getGestionGastos() {
        return gestionGastos;
    }
}