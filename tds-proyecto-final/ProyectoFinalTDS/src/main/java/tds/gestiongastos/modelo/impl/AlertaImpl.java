package tds.gestiongastos.modelo.impl;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.EstrategiaTemporal;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.Notificacion;
import tds.gestiongastos.modelo.TipoCuenta;

public class AlertaImpl implements Alerta {

    @JsonProperty("idAlerta")
    private String idAlerta;

    @JsonProperty("tipo")
    private String tipo;

    @JsonProperty("limite")
    private double limite;

    @JsonProperty("categoria")
    private CategoriaImpl categoria;

    @JsonProperty("nombreCuenta")
    private String nombreCuenta;
    
    @JsonProperty("activa")
    private boolean esActiva;
    
    @JsonIgnore
    private EstrategiaTemporal estrategia;
    
    public AlertaImpl() {
    }

    public AlertaImpl(String tipo, double limite, CategoriaImpl categoria, String nombreCuenta) {
        this.idAlerta = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.limite = limite;
        this.categoria = categoria;
        this.nombreCuenta = nombreCuenta;
        this.esActiva = true;
    }

    @Override
    public String getIdAlerta() {
        return this.idAlerta;
    }

    @Override
    public String getTipo() {
        return this.tipo;
    }

    @Override
    public double getLimite() {
        return this.limite;
    }

    @Override
    public Categoria getCategoria() {
        return this.categoria;
    }

    public String getNombreCuenta() { 
        return nombreCuenta; 
    }
    
    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta; 
    }
    
    public boolean isEsActiva() {
        return this.esActiva;
    }

    public void setIdAlerta(String idAlerta) {
        this.idAlerta = idAlerta;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
        actualizarEstrategia();
    }
    
    private void actualizarEstrategia() {
        if ("Semanal".equalsIgnoreCase(this.tipo)) {
            this.estrategia = new EstrategiaSemanal();
        } else {
            this.estrategia = new EstrategiaMensual();
        }
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }
    
    public void setCategoria(CategoriaImpl categoria) {
        this.categoria = categoria;
    }

    public void setEsActiva(boolean esActiva) {
        this.esActiva = esActiva;
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

        if (this.estrategia == null) {
            actualizarEstrategia();
        }

        if (this.categoria != null && !gastoNuevo.getCategoria().getNombre().equals(this.categoria.getNombre())) {
            return false;
        }

        List<Gasto> gastos = cuenta.getGastos();
        double totalAcumulado = 0.0;
        LocalDate fechaReferencia = gastoNuevo.getFecha();

        for (Gasto g : gastos) {
            boolean categoriaValida = (this.categoria == null) || g.getCategoria().getNombre().equals(this.categoria.getNombre());
            
            if (categoriaValida && estrategia.esMismoPeriodo(g.getFecha(), fechaReferencia)) {
                totalAcumulado += g.getCantidad();
            }
        }

        return (totalAcumulado + gastoNuevo.getCantidad()) > this.limite;
    }

    
    @Override
    public Notificacion crearNotificacion() {
    	String nombreCat = (this.categoria != null) ? this.categoria.getNombre() : "General";
        String prefijo = (this.nombreCuenta != null) ? this.nombreCuenta + "_" : "";
        
        String mensaje = prefijo + "Límite " + tipo + " superado en " + nombreCat +
                         " (Límite: " + limite + "€)";
                         
        return new NotificacionImpl(mensaje, LocalDate.now());
    }
}