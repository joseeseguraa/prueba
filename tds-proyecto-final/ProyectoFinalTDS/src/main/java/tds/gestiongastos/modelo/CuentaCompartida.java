package tds.gestiongastos.modelo;

import java.util.List;

public interface CuentaCompartida extends TipoCuenta {
    
    List<ParticipanteCuenta> getParticipantes();

    void calculoDistribucionEquitativa();

    void calculoPorcentajeGastoAsumido();
}