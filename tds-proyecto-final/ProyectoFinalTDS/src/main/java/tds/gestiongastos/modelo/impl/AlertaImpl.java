package tds.gestiongastos.modelo.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.Notificacion;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class AlertaImpl implements Alerta {

    @JsonProperty("idAlerta")
    private String idAlerta;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("limite")
    private double limite;

    @JsonProperty("categoria")
    private CategoriaImpl categoria;

    @JsonProperty("activa")
    private boolean esActiva;
    
    public AlertaImpl() {
    }

    public AlertaImpl(String tipo, double limite, CategoriaImpl categoria) {
        this.idAlerta = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.limite = limite;
        this.categoria = categoria;
        this.esActiva = true;
    }

    @Override
    public String getIdAlerta() {
        return idAlerta;
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    @Override
    public double getLimite() {
        return limite;
    }

    @Override
    public Categoria getCategoriaMonitorizada() {
        return categoria;
    }

    public boolean isEsActiva() { 
        return esActiva; 
    }
    
    public void activar() {
        this.esActiva = true;
    }

    public void desactivar() {
        this.esActiva = false;
    }

    @Override
    public boolean comprobar(Gasto gastoNuevo, TipoCuenta cuenta) {
        if (!this.esActiva) return false;

        if (!gastoNuevo.getCategoria().getNombre().equals(this.categoria.getNombre())) {
            return false;
        }

        List<Gasto> gastos = cuenta.getGastos();
        double totalAcumulado = 0.0;
        LocalDate fechaReferencia = gastoNuevo.getFecha();

        for (Gasto g : gastos) {
            if (g.getCategoria().getNombre().equals(this.categoria.getNombre())) {
                if (esMismoPeriodo(g.getFecha(), fechaReferencia)) {
                    totalAcumulado += g.getCantidad();
                }
            }
        }

        return (totalAcumulado + gastoNuevo.getCantidad()) > this.limite;
    }

    @Override
    public Notificacion crearNotificacion() {
        String mensaje = "Límite " + tipo + " superado en " + categoria.getNombre() + 
                         " (Límite: " + limite + ")";
        return new NotificacionImpl(mensaje, LocalDate.now());
    }


    private boolean esMismoPeriodo(LocalDate fechaGasto, LocalDate fechaActual) {
        if ("Mensual".equalsIgnoreCase(this.tipo)) {
            return fechaGasto.getMonth() == fechaActual.getMonth() &&
                   fechaGasto.getYear() == fechaActual.getYear();
        } 
        else if ("Semanal".equalsIgnoreCase(this.tipo)) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int semanaGasto = fechaGasto.get(weekFields.weekOfWeekBasedYear());
            int semanaActual = fechaActual.get(weekFields.weekOfWeekBasedYear());
            return semanaGasto == semanaActual && 
                   fechaGasto.getYear() == fechaActual.getYear();
        }
        return false;
    }
}