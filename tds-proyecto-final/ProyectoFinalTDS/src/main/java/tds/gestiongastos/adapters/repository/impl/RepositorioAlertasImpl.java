package tds.gestiongastos.adapters.repository.impl;

import java.util.ArrayList;
import java.util.List;

import tds.gestiongastos.adapters.repository.RepositorioAlertas;
import tds.gestiongastos.modelo.Alerta;

public class RepositorioAlertasImpl implements RepositorioAlertas {

    private List<Alerta> alertas;

    public RepositorioAlertasImpl() {
        this.alertas = new ArrayList<>();
    }

    @Override
    public void addAlerta(Alerta alerta) {
        this.alertas.add(alerta);
    }

	@Override
    public void removeAlerta(Alerta string) {
        
    }    

    @Override
    public List<Alerta> getAllAlertas() {
        return new ArrayList<>(this.alertas);
    }

}