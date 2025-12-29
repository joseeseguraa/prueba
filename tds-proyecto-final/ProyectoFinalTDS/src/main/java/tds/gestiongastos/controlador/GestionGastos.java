package tds.gestiongastos.controlador;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
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
import tds.gestiongastos.modelo.ImportadorGastos;
import tds.gestiongastos.modelo.Notificacion;
import tds.gestiongastos.modelo.ParticipanteCuenta;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.AlertaImpl;
import tds.gestiongastos.modelo.impl.CategoriaImpl;
import tds.gestiongastos.modelo.impl.CuentaCompartidaImpl;
import tds.gestiongastos.modelo.impl.FactoriaImportadoresImpl;
import tds.gestiongastos.modelo.impl.GastoImpl;

public class GestionGastos {

    private RepositorioCuentas repoCuentas;
    private RepositorioGastos repoGastos;
    private RepositorioCategorias repoCategorias;
    private RepositorioAlertas repoAlertas;
    private RepositorioNotificaciones repoNotificaciones;
    private TipoCuenta cuentaActiva;
    
    private static final String SEPARADOR = "--------------------------------------------------";

    public GestionGastos(RepositorioCuentas repoCuentas, RepositorioGastos repoGastos,
            RepositorioCategorias repoCategorias, RepositorioAlertas repoAlertas,
            RepositorioNotificaciones repoNotificaciones) {
        this.repoCuentas = repoCuentas;
        this.repoGastos = repoGastos;
        this.repoCategorias = repoCategorias;
        this.repoAlertas = repoAlertas;
        this.repoNotificaciones = repoNotificaciones;
    }

    public void setCuentaActiva(TipoCuenta cuenta) {
        this.cuentaActiva = cuenta;
        if (this.cuentaActiva != null) {
            System.out.println("Cuenta activa cambiada a: " + cuenta.getNombre());
            inicializarCategoriasPorDefecto();
            System.out.println(SEPARADOR);
        }
    }

    private void inicializarCategoriasPorDefecto() {
        registrarCategoria("Alimentación");
        registrarCategoria("Transporte");
        registrarCategoria("Entretenimiento");
    }

    public List<Categoria> getTodasCategorias() {
        if (cuentaActiva == null) return new ArrayList<>();
        String prefijo = cuentaActiva.getNombre() + "_";
        return repoCategorias.getAllCategorias().stream()
                .filter(c -> c.getNombre().startsWith(prefijo))
                .collect(Collectors.toList());
    }

    public boolean registrarCategoria(String nombre) {
        if (cuentaActiva == null) return false;
        String nombreUnico = cuentaActiva.getNombre() + "_" + nombre;
        if (repoCategorias.findByNombre(nombreUnico) != null) return false;
        
        CategoriaImpl nuevaCategoria = new CategoriaImpl(nombreUnico);
        repoCategorias.addCategoria(nuevaCategoria);
        
        System.out.println("Categoría CREADA: " + nombre);
        System.out.println(SEPARADOR);
        return true;
    }

    public List<String> registrarGasto(String descripcion, double cantidad, LocalDate fecha, String nombreCategoria, String pagador) {
        if (cuentaActiva == null) throw new IllegalStateException("No hay cuenta activa");

        Categoria categoria = repoCategorias.findByNombre(nombreCategoria);

        if (categoria == null) {
            String nombreUnico = cuentaActiva.getNombre() + "_" + nombreCategoria;
            categoria = repoCategorias.findByNombre(nombreUnico);
        }

        if (categoria == null) {
            throw new IllegalArgumentException("La categoría '" + nombreCategoria + "' no existe para esta cuenta.");
        }

        Gasto nuevoGasto = new GastoImpl(descripcion, cantidad, fecha, (CategoriaImpl) categoria);
        if (pagador != null) nuevoGasto.setPagador(pagador);

        repoGastos.addGasto(nuevoGasto);
        cuentaActiva.agregarGasto(nuevoGasto);
        repoCuentas.updateCuenta(cuentaActiva);
        
        System.out.println("Gasto CREADO: " + descripcion + " | " + cantidad + "€ | " + fecha + " | Cat: " + limpiarTexto(categoria.getNombre()));
        
        List<String> avisos = checkAlertas(nuevoGasto);
        System.out.println(SEPARADOR);
        return avisos;
    }

    public void modificarGasto(Gasto gasto, String nuevaDesc, double nuevaCant, LocalDate nuevaFecha, String nuevaCategoria, String pagador) {
        System.out.println("Editando Gasto ID: " + gasto.getDescripcion() + " (" + gasto.getCantidad() + "€)...");
        
        gasto.setDescripcion(nuevaDesc);
        gasto.setCantidad(nuevaCant);
        gasto.setFecha(nuevaFecha);
        
        Categoria cat = repoCategorias.findByNombre(nuevaCategoria);
        if (cat == null) {
            cat = repoCategorias.findByNombre(cuentaActiva.getNombre() + "_" + nuevaCategoria);
        }
        
        if (cat != null) gasto.setCategoria(cat);
        if (pagador != null) gasto.setPagador(pagador);
        
        repoGastos.updateGasto(gasto);
        repoCuentas.updateCuenta(cuentaActiva);
        
        System.out.println("...Gasto MODIFICADO a: " + nuevaDesc + " | " + nuevaCant + "€");
        System.out.println(SEPARADOR);
    }

    public void borrarGastos(List<Gasto> listaGastosABorrar) {
        if (cuentaActiva == null || listaGastosABorrar == null) return;
        
        for (Gasto g : listaGastosABorrar) {
            System.out.println("Eliminando Gasto: " + g.getDescripcion() + " (" + g.getCantidad() + "€)");
            cuentaActiva.eliminarGasto(g);
            repoGastos.removeGasto(g);
        }
        repoCuentas.updateCuenta(cuentaActiva);
        System.out.println(SEPARADOR);
    }

    public List<Gasto> filtrarGastos(LocalDate inicio, LocalDate fin, Categoria categoria, String pagador) {
        if (cuentaActiva == null) return List.of();
        return cuentaActiva.getGastos().stream()
                .filter(g -> (inicio == null || !g.getFecha().isBefore(inicio)))
                .filter(g -> (fin == null || !g.getFecha().isAfter(fin)))
                .filter(g -> (categoria == null || g.getCategoria().equals(categoria)))
                .filter(g -> (pagador == null || pagador.isEmpty() || 
                              (g.getPagador() != null && g.getPagador().equalsIgnoreCase(pagador))))
                .collect(Collectors.toList());
    }

    public Map<String, Double> obtenerGastosPorCategoria() {
        if (cuentaActiva == null) return new HashMap<>();
        return cuentaActiva.getGastos().stream().collect(
                Collectors.groupingBy(g -> g.getCategoria().getNombre(), Collectors.summingDouble(Gasto::getCantidad)));
    }

    public Map<String, Double> obtenerGastosPorFecha() {
        if (cuentaActiva == null) return new HashMap<>();
        return cuentaActiva.getGastos().stream().collect(
                Collectors.groupingBy(g -> g.getFecha().toString(), Collectors.summingDouble(Gasto::getCantidad)));
    }

    public void eliminarCategoria(Categoria categoria) {
        boolean tieneGastos = repoCuentas.getAllCuentas().stream()
                .flatMap(c -> c.getGastos().stream())
                .anyMatch(g -> g.getCategoria().equals(categoria));

        if (tieneGastos) {
            System.out.println("Intento fallido de borrar categoría " + limpiarTexto(categoria.getNombre()) + " (Tiene gastos)");
            System.out.println(SEPARADOR);
            throw new IllegalStateException("No se puede eliminar la categoría porque tiene gastos asociados.");
        }
        repoCategorias.borrarCategoria(categoria);
        System.out.println("Categoría ELIMINADA: " + limpiarTexto(categoria.getNombre()));
        System.out.println(SEPARADOR);
    }

    public void eliminarCategorias(List<Categoria> lista) {
        for (Categoria c : lista) {
            eliminarCategoria(c);
        }
    }

    public void crearCuentaCompartida(String nombre, List<ParticipanteCuenta> participantes, boolean esEquitativa) {
        CuentaCompartidaImpl nuevaCuenta = new CuentaCompartidaImpl(nombre, participantes);
        if (esEquitativa) nuevaCuenta.calculoDistribucionEquitativa();
        else nuevaCuenta.calculoPorcentajeGastoAsumido();
        repoCuentas.addCuenta(nuevaCuenta);
        System.out.println("Nueva Cuenta Compartida CREADA: " + nombre);
        System.out.println(SEPARADOR);
    }

    public List<TipoCuenta> getCuentasDisponibles() { return repoCuentas.getAllCuentas(); }
    public TipoCuenta getCuentaActiva() { return this.cuentaActiva; }
    
    public List<Alerta> getAlertas() { 
        if (cuentaActiva == null) return new ArrayList<>();
     
        return repoAlertas.getAllAlertas().stream()
                .filter(a -> a.getNombreCuenta() != null && 
                             a.getNombreCuenta().equals(cuentaActiva.getNombre()))
                .collect(Collectors.toList());
    }

    public boolean configurarAlerta(String tipo, double limite, Categoria categoria) {
        String cuentaActual = cuentaActiva.getNombre();

        Alerta existente = repoAlertas.getAllAlertas().stream()
            .filter(a -> a.getNombreCuenta() != null && a.getNombreCuenta().equals(cuentaActual))
            .filter(a -> a.getTipo().equalsIgnoreCase(tipo))
            .filter(a -> (a.getCategoria() == null && categoria == null) || 
                         (a.getCategoria() != null && a.getCategoria().equals(categoria)))
            .findFirst()
            .orElse(null);

        if (existente != null) {
            System.out.println("Alerta existente ACTUALIZADA: " + tipo + " -> Límite: " + limite + "€");
            ((AlertaImpl) existente).setLimite(limite);
            System.out.println(SEPARADOR);
            return true;
        }

        repoAlertas.addAlerta(new AlertaImpl(tipo, limite, (CategoriaImpl) categoria, cuentaActual));
        
        String nomCat = (categoria != null) ? limpiarTexto(categoria.getNombre()) : "General";
        System.out.println("Alerta NUEVA creada: " + tipo + " | Límite: " + limite + "€ | Cat: " + nomCat);
        System.out.println(SEPARADOR);
        return true;
    }

    public void borrarAlerta(List<Alerta> lista) {
        for (Alerta a : new ArrayList<>(lista)) {
            System.out.println("Alerta ELIMINADA: " + a.getTipo() + " (" + a.getLimite() + "€)");
            repoAlertas.removeAlerta(a);
        }
        System.out.println(SEPARADOR);
    }
    
    public void modificarAlerta(Alerta alerta, double nuevoLimite) {
        if (alerta != null && alerta instanceof AlertaImpl) {
            System.out.println("Alerta MODIFICADA: Límite anterior " + alerta.getLimite() + "€ -> Nuevo: " + nuevoLimite + "€");
            ((AlertaImpl) alerta).setLimite(nuevoLimite);
            repoCuentas.updateCuenta(cuentaActiva);
            System.out.println(SEPARADOR);
        }
    }
    
    public List<Alerta> getAlertasPorCuenta(String nombreCuenta) {
        List<Alerta> todas = getAlertas();
        List<Alerta> filtradas = new ArrayList<>();
        
        for (Alerta a : todas) {
            if (a.getNombreCuenta() != null && a.getNombreCuenta().equals(nombreCuenta)) {
                filtradas.add(a);
            }
        }
        return filtradas;
    }
    
    private List<String> checkAlertas(Gasto gasto) {
        List<String> avisos = new ArrayList<>();
        List<Alerta> misAlertas = getAlertas();
        
        for (Alerta a : misAlertas) {
            if (a.getCategoria() == null || a.getCategoria().equals(gasto.getCategoria())) {
                if (a.comprobar(gasto, cuentaActiva)) {
                    Notificacion n = a.crearNotificacion();
                    repoNotificaciones.addNotificacion(n);
                    avisos.add(n.getMensaje());
                    System.out.println("¡Límite superado! Generada notificación: " + limpiarTexto(n.getMensaje()));
                }
            }
        }
        return avisos;
    }

    public void anadirNotificacion(Notificacion notificacion) {
        if (repoNotificaciones != null) {
            repoNotificaciones.addNotificacion(notificacion);
            System.out.println("Notificación añadida manualmente: " + limpiarTexto(notificacion.getMensaje()));
            System.out.println(SEPARADOR);
        }
    }

    public List<Notificacion> revisarHistorialNotificaciones() { 
        return repoNotificaciones.getAllNotificaciones(); 
    }

    public void borrarNotificacion(List<Notificacion> lista) {
        for (Notificacion n : new ArrayList<>(lista)) {
            System.out.println("Notificación ELIMINADA del historial: " + limpiarTexto(n.getMensaje()));
            repoNotificaciones.removeNotificacion(n);
        }
        System.out.println(SEPARADOR);
    }

    public boolean importarDatos(File fichero) {
        if (cuentaActiva == null) return false;
        System.out.println("Iniciando IMPORTACIÓN desde: " + fichero.getName());
        System.out.println(SEPARADOR);
        
        String ext = fichero.getName().substring(fichero.getName().lastIndexOf(".") + 1).toLowerCase();
        ImportadorGastos adaptador = FactoriaImportadoresImpl.crearImportador(ext);
        if (adaptador != null) {
            List<Gasto> lista = adaptador.importarGastos(fichero);
            System.out.println("Archivo leído. Procesando " + lista.size() + " gastos...");
            
            for (Gasto g : lista) {
                String nomCat = g.getCategoria().getNombre();
                Categoria cat = repoCategorias.findByNombre(cuentaActiva.getNombre() + "_" + nomCat);
                if (cat == null) {
                   registrarCategoria(nomCat);
                    cat = repoCategorias.findByNombre(cuentaActiva.getNombre() + "_" + nomCat);
                }
                g.setCategoria(cat);
                repoGastos.addGasto(g);
                cuentaActiva.agregarGasto(g);
                
                System.out.println("Importando gasto: " + g.getDescripcion() + " (" + g.getCantidad() + "€)");
                checkAlertas(g);
            }
            repoCuentas.updateCuenta(cuentaActiva);
            System.out.println("Importación finalizada con éxito.");
            System.out.println(SEPARADOR);
            return true;
        }
        System.out.println("Error: No se encontró importador para extensión ." + ext);
        System.out.println(SEPARADOR);
        return false;
    }
    
    private String limpiarTexto(String texto) {
        if (cuentaActiva != null && texto != null) {
            String prefijo = cuentaActiva.getNombre() + "_";
            if (texto.contains(prefijo)) {
                return texto.replace(prefijo, "");
            }
        }
        return texto;
    }
}