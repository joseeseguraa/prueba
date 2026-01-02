package tds.gestiongastos.vista;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import tds.gestiongastos.controlador.GestionGastos;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.CuentaPersonalImpl;

public class ConsolaGastos {

    private Scanner reader;
    private byte comandoActual = ComandosGasto.COM_INVALIDO;
    private String[] argumentosComando = new String[0];
    private GestionGastos controlador;

    public ConsolaGastos(GestionGastos gestor) {
        this.controlador = gestor;
        this.reader = new Scanner(System.in);
    }

    public void ejecutar() {
        System.out.println("-----------------------------------------");
        System.out.println(" Bienvenido a Gestión de Gastos (Modo Texto) ");
        System.out.println("-----------------------------------------");
        System.out.println("Escribe 'ayuda' para ver los comandos.");
        
        mostrarCuentasDisponibles();

        boolean salir = false;
        while (!salir) {
            leerComandoGeneral();
            byte comando = getComando();
            String[] args = getArgumentos();

            try {
                switch (comando) {
                    case ComandosGasto.COM_AYUDA:
                        ComandosGasto.imprimirAyuda();
                        break;
                    case ComandosGasto.COM_SALIR:
                        salir = true;
                        System.out.println("Cerrando aplicación. ¡Hasta pronto!");
                        break;
                    case ComandosGasto.COM_CUENTA:
                        if (args.length == 0) {
                            seleccionarCuenta();
                        } else {
                            cambiarCuenta(args[0]);
                        }
                        break;
                    case ComandosGasto.COM_PARTICIPANTES:
                        listarParticipantes();
                        break;
                    case ComandosGasto.COM_REGISTRAR:
                        registrarGasto(args);
                        break;
                    case ComandosGasto.COM_LISTAR:
                        listarGastos();
                        break;
                    case ComandosGasto.COM_BORRAR:
                        borrarGasto(args[0]);
                        break;
                    case ComandosGasto.COM_CATEGORIAS:
                        listarCategorias();
                        break;
                    case ComandosGasto.COM_CREAR_CAT:
                        if (controlador.registrarCategoria(args[0])) {
                            System.out.println(">> Categoría '" + args[0] + "' creada con éxito.");
                        } else {
                            System.out.println("Error: La categoría ya existe.");
                        }
                        break;
                    case ComandosGasto.COM_BORRAR_CAT:
                        ejecutarBorradoCategoria(args[0]);
                        break;
                    case ComandosGasto.COM_EDITAR:
                        editarGasto(args);
                        break;
                    default:
                        System.out.println("Comando desconocido. Escribe 'ayuda'.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void listarCategorias() {
        System.out.println("--- Categorías Disponibles ---");
        controlador.getTodasCategorias().forEach(c -> {
            String nombre = c.getNombre();
            int index = nombre.indexOf("_");
            System.out.println(" - " + ((index != -1) ? nombre.substring(index + 1) : nombre));
        });
    }

    private void ejecutarBorradoCategoria(String nombreBusqueda) {
        Optional<Categoria> catObj = controlador.getTodasCategorias().stream()
            .filter(c -> {
                String n = c.getNombre();
                int idx = n.indexOf("_");
                String limpio = (idx != -1) ? n.substring(idx + 1) : n;
                return limpio.equalsIgnoreCase(nombreBusqueda);
            }).findFirst();

        if (catObj.isPresent()) {
            try {
                controlador.eliminarCategoria(catObj.get());
                System.out.println(">> Categoría eliminada.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Error: No existe la categoría '" + nombreBusqueda + "'.");
        }
    }
    
    private void seleccionarCuenta() {
        System.out.println("\n--- SELECCIÓN DE CUENTA ---");
        List<TipoCuenta> cuentas = controlador.getCuentasDisponibles();
        
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas disponibles. Crea una nueva.");
            return;
        }

        for (int i = 0; i < cuentas.size(); i++) {
            String tipo = (cuentas.get(i) instanceof CuentaCompartida) ? " (Compartida)" : " (Personal)";
            System.out.println((i + 1) + ". " + cuentas.get(i).getNombre() + tipo);
        }
        
        System.out.print("Elige el número de la cuenta para operar: ");
        try {
            String linea = reader.nextLine().trim();
            
            if (linea.isEmpty()) {
                System.out.println("Operación cancelada.");
                return;
            }

            int indice = Integer.parseInt(linea) - 1;

            if (indice >= 0 && indice < cuentas.size()) {
                TipoCuenta seleccionada = cuentas.get(indice);
                controlador.setCuentaActiva(seleccionada);
                System.out.println(">> Cuenta activa cambiada a: " + seleccionada.getNombre());
            } else {
                System.out.println("Error: El número " + (indice + 1) + " no es válido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debes introducir un número válido.");
        }
    }

    private void cambiarCuenta(String nombreBusqueda) {
        List<TipoCuenta> cuentas = controlador.getCuentasDisponibles();
        Optional<TipoCuenta> encontrada;
        if (nombreBusqueda.equalsIgnoreCase("personal")) {
            encontrada = cuentas.stream().filter(c -> c instanceof CuentaPersonalImpl).findFirst();
        } else {
            encontrada = cuentas.stream().filter(c -> c.getNombre().equalsIgnoreCase(nombreBusqueda)).findFirst();
        }

        if (encontrada.isPresent()) {
            controlador.setCuentaActiva(encontrada.get());
            System.out.println(">> CUENTA ACTIVA: " + encontrada.get().getNombre());
        } else {
            System.out.println("Error: No se encontró la cuenta '" + nombreBusqueda + "'.");
            mostrarCuentasDisponibles();
        }
    }

    private void registrarGasto(String[] args) {
        if (controlador.getCuentaActiva() == null) {
            System.out.println("Error: Primero selecciona una cuenta.");
            return;
        }

        try {
            double cantidad = Double.parseDouble(args[0]);
            
            LocalDate fecha;
            if (args[1].equalsIgnoreCase("hoy")) {
                fecha = LocalDate.now();
            } else {
                fecha = LocalDate.parse(args[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            
            String categoria = args[2];
            String restoTexto = (args.length > 3) ? args[3] : "";
            
            String pagador = "Yo";
            String descripcion = "";
            
            TipoCuenta cuentaActual = controlador.getCuentaActiva();

            if (cuentaActual instanceof CuentaCompartida) {
                if (restoTexto.isEmpty()) {
                    System.out.println("Error: En cuentas compartidas DEBES indicar el PAGADOR.");
                    System.out.println("Ejemplo: registrar 10 01/01/2025 Cine Juan Entradas");
                    return;
                }
                String[] partes = restoTexto.split(" ", 2);
                String inputPagador = partes[0];
                
                Optional<String> nombreReal = ((CuentaCompartida) cuentaActual).getParticipantes().stream()
                        .map(p -> p.getNombre())
                        .filter(nombre -> nombre.equalsIgnoreCase(inputPagador)) 
                        .findFirst(); 
                
                if (!nombreReal.isPresent()) {
                    System.out.println("ERROR: La persona '" + inputPagador + "' no pertenece a esta cuenta.");
                    System.out.println("Participantes válidos:");
                    ((CuentaCompartida) cuentaActual).getParticipantes()
                            .forEach(p -> System.out.println(" - " + p.getNombre()));
                    return; 
                }
                
                pagador = nombreReal.get(); 

                descripcion = (partes.length > 1) ? partes[1] : "Gasto compartido";
                
            } else {
                descripcion = restoTexto.isEmpty() ? "Gasto personal" : restoTexto;
            }

            controlador.registrarGasto(descripcion, cantidad, fecha, categoria, pagador);
            System.out.println(">> Gasto registrado con éxito. Pagador: " + pagador);

        } catch (Exception e) {
            System.out.println("Error al registrar (Revise el formato): " + e.getMessage());
        }
    }
    
    
    private void editarGasto(String[] args) {
        if (controlador.getCuentaActiva() == null) {
            System.out.println("Error: Selecciona una cuenta primero.");
            return;
        }

        String idGasto = args[0];
        
        Optional<Gasto> gastoOpt = controlador.getCuentaActiva().getGastos().stream()
                .filter(g -> g.getId().equals(idGasto))
                .findFirst();

        if (!gastoOpt.isPresent()) {
            System.out.println("Error: No se encontró ningún gasto con ID: " + idGasto);
            return;
        }

        Gasto gastoAEditar = gastoOpt.get();

        try {
            double cantidad = Double.parseDouble(args[1]);
            
            LocalDate fecha;
            if (args[2].equalsIgnoreCase("hoy")) {
                fecha = LocalDate.now();
            } else {
                fecha = LocalDate.parse(args[2], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            
            String categoria = args[3];
            String restoTexto = (args.length > 4) ? args[4] : "";
            
            String pagador = "Yo";
            String descripcion = "";
            
            TipoCuenta cuentaActual = controlador.getCuentaActiva();

            if (cuentaActual instanceof CuentaCompartida) {
                if (restoTexto.isEmpty()) {
                    System.out.println("Error: Al editar en compartida debes indicar el PAGADOR.");
                    return;
                }
                String[] partes = restoTexto.split(" ", 2);
                String inputPagador = partes[0];
                
                Optional<String> nombreReal = ((CuentaCompartida) cuentaActual).getParticipantes().stream()
                        .map(p -> p.getNombre())
                        .filter(n -> n.equalsIgnoreCase(inputPagador))
                        .findFirst();
                
                if (!nombreReal.isPresent()) {
                    System.out.println("ERROR: El participante '" + inputPagador + "' no existe.");
                    return;
                }
                pagador = nombreReal.get();
                descripcion = (partes.length > 1) ? partes[1] : "Gasto editado";
                
            } else {
                descripcion = restoTexto.isEmpty() ? "Gasto editado" : restoTexto;
            }

            controlador.modificarGasto(gastoAEditar, descripcion, cantidad, fecha, categoria, pagador);
            
        } catch (Exception e) {
            System.out.println("Error al editar: " + e.getMessage());
        }
    }
    
    
    private void borrarGasto(String idBorrar) {
        if (controlador.getCuentaActiva() == null) {
            System.out.println("Error: Selecciona una cuenta primero.");
            return;
        }
        Optional<Gasto> target = controlador.getCuentaActiva().getGastos().stream()
            .filter(g -> g.getId().equals(idBorrar)).findFirst();

        if (target.isPresent()) {
            controlador.borrarGastos(Arrays.asList(target.get()));
            System.out.println(">> Gasto eliminado.");
        } else {
            System.out.println("Error: No existe el gasto con ID: " + idBorrar);
        }
    }

    private void listarParticipantes() {
        System.out.println("\n--- PERSONAS POR CUENTA ---");
        List<TipoCuenta> cuentas = controlador.getCuentasDisponibles();
        
        if (cuentas.isEmpty()) {
            System.out.println("No hay cuentas creadas.");
            return;
        }

        for (TipoCuenta c : cuentas) {
            System.out.print("> " + c.getNombre());
            if (c instanceof CuentaCompartida) {
                System.out.print(" (Compartida): ");
                CuentaCompartida cc = (CuentaCompartida) c;
                String nombres = cc.getParticipantes().stream()
                        .map(p -> p.getNombre())
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Sin participantes");
                System.out.println(nombres);
            } else {
                System.out.println(" (Personal): Solo tú");
            }
        }
        System.out.println("---------------------------");
    }
    
    
    private void listarGastos() {
        if (controlador.getCuentaActiva() == null) {
            System.out.println("Error: Selecciona una cuenta primero.");
            return;
        }

        List<Gasto> gastos = controlador.filtrarGastos(null, null, null, null);
        
        if (gastos.isEmpty()) {
            System.out.println("La cuenta no tiene gastos.");
        } else {
            System.out.printf("%-36s %-12s %-12s %-15s %-15s %s\n", 
                "ID", "IMPORTE", "FECHA", "CATEGORIA", "PAGADOR", "DESCRIPCION");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            
            for (Gasto g : gastos) {
                String nombreInterno = (g.getCategoria() != null) ? g.getCategoria().getNombre() : "SIN CATEGORIA";
                int index = nombreInterno.indexOf("_");
                String nomCat = (index != -1) ? nombreInterno.substring(index + 1) : nombreInterno;
                
                String pagador = (g.getPagador() != null) ? g.getPagador() : "Yo";

                System.out.printf("%-36s %-12.2f %-12s %-15s %-15s %s\n", 
                    g.getId(), 
                    g.getCantidad(), 
                    g.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                    nomCat, 
                    pagador, 
                    g.getDescripcion());
            }
        }
    }

    private void mostrarCuentasDisponibles() {
        System.out.println("--- Cuentas Disponibles ---");
        controlador.getCuentasDisponibles().forEach(c -> {
            String tipo = (c instanceof CuentaPersonalImpl) ? "(Personal)" : "(Compartida)";
            System.out.println(" * " + c.getNombre() + " " + tipo);
        });
        System.out.println("---------------------------");
    }

    public byte getComando() { return comandoActual; }
    public String[] getArgumentos() { return argumentosComando; }

    public void leerComandoGeneral() {
        do {
            System.out.print("> ");
            argumentosComando = leerEntradaEstandar();
        } while (!validarArgumentos(argumentosComando));
    }

    private String[] leerEntradaEstandar() {
        if (!reader.hasNextLine()) return new String[0];
        String linea = reader.nextLine();
        StringTokenizer st = new StringTokenizer(linea);
        if (!st.hasMoreTokens()) return new String[0];

        this.comandoActual = ComandosGasto.stringToCommand(st.nextToken());
        Vector<String> vargs = new Vector<>();
        
        int argsFijos = -1;
        if (comandoActual == ComandosGasto.COM_REGISTRAR) argsFijos = 3; 
        if (comandoActual == ComandosGasto.COM_EDITAR) argsFijos = 4;    
        
        if (argsFijos != -1) {
            
            for(int i=0; i < argsFijos && st.hasMoreTokens(); i++) vargs.add(st.nextToken());
            
            StringBuilder desc = new StringBuilder();
            while(st.hasMoreTokens()) desc.append(st.nextToken()).append(" ");
            if (desc.length() > 0) vargs.add(desc.toString().trim());
            
        } else {
            while (st.hasMoreTokens()) vargs.add(st.nextToken());
        }
        return vargs.toArray(new String[0]);
    }

    private boolean validarArgumentos(String[] args) {
        if (this.comandoActual == ComandosGasto.COM_INVALIDO) return false;
        
        switch (this.comandoActual) {
            case ComandosGasto.COM_REGISTRAR:
                if (args.length < 3) {
                    System.out.println("Uso: registrar <cantidad> <dd/MM/yyyy> <categoria> [Pagador] [descripcion]");
                    return false;
                }
                break; 
                
            case ComandosGasto.COM_CUENTA:
                 if (args.length > 1) {
                     System.out.println("Uso: cuenta [nombre_cuenta]");
                     return false;
                }
                break; 
                
            case ComandosGasto.COM_PARTICIPANTES: 
            case ComandosGasto.COM_LISTAR:
            case ComandosGasto.COM_AYUDA:
            case ComandosGasto.COM_SALIR:
            case ComandosGasto.COM_CATEGORIAS:
                if (args.length != 0) {
                     String nombre = ComandosGasto.commandToString(this.comandoActual);
                     System.out.println(">> AVISO: El comando '" + nombre + "' no requiere argumentos. Se ignorarán.");
                }
                break;
            case ComandosGasto.COM_BORRAR:
            case ComandosGasto.COM_CREAR_CAT:
            case ComandosGasto.COM_BORRAR_CAT:
                if (args.length != 1) {
                    System.out.println("Uso: comando <parametro>");
                    return false;
                }
                break;
            case ComandosGasto.COM_EDITAR:
                if (args.length < 4) {
                    System.out.println("Uso: editar <ID> <cantidad> <fecha> <categoria> [Pagador] [descripcion]");
                    return false;
                }
                break;
        }
        return true;
    }
}