package tds.gestiongastos.controlador;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import tds.gestiongastos.adapters.repository.RepositorioAlertas;
import tds.gestiongastos.adapters.repository.RepositorioCategorias;
import tds.gestiongastos.adapters.repository.RepositorioCuentas;
import tds.gestiongastos.adapters.repository.RepositorioGastos;
import tds.gestiongastos.adapters.repository.RepositorioNotificaciones;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.Notificacion;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.AlertaImpl;
import tds.gestiongastos.modelo.impl.CategoriaImpl;
import tds.gestiongastos.modelo.impl.CuentaCompartidaImpl;
import tds.gestiongastos.modelo.impl.GastoImpl;

public class GestionGastos {

    private RepositorioCuentas repoCuentas;
    @SuppressWarnings("unused")
	private RepositorioGastos repoGastos;
    private RepositorioCategorias repoCategorias;
    private RepositorioAlertas repoAlertas;
    private RepositorioNotificaciones repoNotificaciones;

    private TipoCuenta cuentaActiva;

    public GestionGastos(RepositorioCuentas repoCuentas, RepositorioGastos repoGastos, RepositorioCategorias repoCategorias, RepositorioAlertas repoAlertas, RepositorioNotificaciones repoNotificaciones) {
        this.repoCuentas = repoCuentas;
        this.repoGastos = repoGastos;
        this.repoCategorias = repoCategorias;
        this.repoAlertas = repoAlertas;
        this.repoNotificaciones = repoNotificaciones;
    }

    public TipoCuenta crearCuentaCompartida(String nombre) {
    	TipoCuenta nuevaCuenta = new CuentaCompartidaImpl(nombre, null);
        repoCuentas.addCuenta(nuevaCuenta);
        return nuevaCuenta;
    }

    public List<TipoCuenta> getCuentasDisponibles() {
        return repoCuentas.getAllCuentas();
    }

    public void setCuentaActiva(TipoCuenta cuenta) {
        this.cuentaActiva = cuenta;
    }

    public TipoCuenta getCuentaActiva() {
        return this.cuentaActiva;
    }

    public List<Categoria> getTodasCategorias() {
        return repoCategorias.getAllCategorias();
    }

    public void registrarGasto(String descripcion, double cantidad, LocalDate fecha, String nombreCategoria) {
        if (cuentaActiva == null) {
			throw new IllegalStateException("No hay cuenta activa seleccionada");
		}

        Categoria categoria = repoCategorias.findByNombre(nombreCategoria);

        Gasto nuevoGasto = new GastoImpl(descripcion, cantidad, fecha, (CategoriaImpl) categoria);

        cuentaActiva.agregarGasto(nuevoGasto);
        repoCuentas.updateCuenta(cuentaActiva);

        checkAlertas(nuevoGasto);
    }

    public void borrarGasto(Gasto gasto) {
        if (cuentaActiva != null) {
            cuentaActiva.eliminarGasto(gasto);
            repoCuentas.updateCuenta(cuentaActiva);
        }
    }

    public void modificarGasto(Gasto gasto, String nuevaDesc, double nuevaCant, LocalDate nuevaFecha, String nuevaCategoria) {
        gasto.setDescripcion(nuevaDesc);
        gasto.setCantidad(nuevaCant);
        gasto.setFecha(nuevaFecha);
        
        Categoria cat = repoCategorias.findByNombre(nuevaCategoria);
        if (cat != null) {
            gasto.setCategoria(cat);
        }
        repoCuentas.updateCuenta(cuentaActiva);
    }


    public List<Gasto> filtrarGastos(LocalDate inicio, LocalDate fin, Categoria categoria) {
        if (cuentaActiva == null) {
			return List.of();
		}

        return cuentaActiva.getGastos().stream()
                .filter(g -> (inicio == null || !g.getFecha().isBefore(inicio)))
                .filter(g -> (fin == null || !g.getFecha().isAfter(fin)))
                .filter(g -> (categoria == null || g.getCategoria().equals(categoria)))
                .collect(Collectors.toList());
    }


    public Map<String, Double> obtenerGastosPorCategoria() {
        if (cuentaActiva == null) {
			return new HashMap<>();
		}

        return cuentaActiva.getGastos().stream()
            .collect(Collectors.groupingBy(
                g -> g.getCategoria().getNombre(),
                Collectors.summingDouble(Gasto::getCantidad)
            ));
    }

    public Map<String, Double> obtenerGastosPorFecha() {
        if (cuentaActiva == null) {
			return new HashMap<>();
		}

        return cuentaActiva.getGastos().stream()
            .collect(Collectors.groupingBy(
                g -> g.getFecha().toString(),
                Collectors.summingDouble(Gasto::getCantidad)
            ));
    }


    public boolean registrarCategoria(String nombre) {
        if (repoCategorias.findByNombre(nombre) != null) {
            return false;
        }
        CategoriaImpl nuevaCategoria = new CategoriaImpl(nombre);
        repoCategorias.addCategoria(nuevaCategoria);
        return true;
    }

    
    public void eliminarCategoria(Categoria categoria) {
    	boolean enGastos = repoCuentas.getAllCuentas().stream()
                .flatMap(c -> c.getGastos().stream())
                .anyMatch(g -> g.getCategoria().equals(categoria));

        if (enGastos) {
            throw new IllegalStateException("No se puede eliminar: Hay gastos asociados.");
        }

        boolean enAlertas = repoAlertas.getAllAlertas().stream()
                .anyMatch(a -> a.getCategoria() != null && a.getCategoria().equals(categoria));

        if (enAlertas) {
            throw new IllegalStateException("No se puede eliminar: Hay una alerta configurada para esta categoría.");
        }
        
        repoCategorias.borrarCategoria(categoria); 
    }
    

    public List<Alerta> getAlertas() {
        return repoAlertas.getAllAlertas();
    }

    public void borrarAlerta(Alerta alerta) {
    	repoAlertas.removeAlerta(alerta); 
    }
    
    
    public void configurarAlerta(String tipo, double limite, Categoria categoria) {
        Alerta nuevaAlerta = new AlertaImpl(tipo, limite, (tds.gestiongastos.modelo.impl.CategoriaImpl) categoria);
        repoAlertas.addAlerta(nuevaAlerta);
    }

    private void checkAlertas(Gasto gasto) {
        List<Alerta> alertas = repoAlertas.getAllAlertas();
        for (Alerta alerta : alertas) {
            if (alerta.comprobar(gasto, cuentaActiva)) {
                Notificacion notificacion = alerta.crearNotificacion();
                repoNotificaciones.addNotificacion(notificacion);
            }
        }
    }

    public List<Notificacion> revisarHistorialNotificaciones() {
        return repoNotificaciones.getAllNotificaciones();
    }

    public void importarDatos(String rutaFichero) {
       //falta la implementación de esta parte, usando el patron Adaptador
    }
}