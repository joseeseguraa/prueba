package tds.gestiongastos.vista;

public class ComandosGasto {
    
    public static final byte COM_INVALIDO = 0;
    public static final byte COM_SALIR = 1;
    public static final byte COM_AYUDA = 2;
    public static final byte COM_CUENTA = 3;
    public static final byte COM_PARTICIPANTES = 4;
    public static final byte COM_REGISTRAR = 5;
    public static final byte COM_EDITAR = 6;
    public static final byte COM_BORRAR = 7;
    public static final byte COM_LISTAR = 8;
    public static final byte COM_CREAR_CAT = 9;
    public static final byte COM_BORRAR_CAT = 10;
    public static final byte COM_CATEGORIAS = 11;

    private static final Byte[] _comandos_validos = { 
        COM_SALIR, COM_AYUDA, COM_CUENTA, 
        COM_PARTICIPANTES, COM_REGISTRAR, COM_EDITAR, 
        COM_BORRAR, COM_LISTAR, COM_CREAR_CAT,
        COM_BORRAR_CAT, COM_CATEGORIAS
    };

    private static final String[] _comandos_validos_str = {
        "salir", "ayuda", "cuenta",
        "participantes", "registrar", "editar", 
        "borrar", "listar", "nueva_cat",
        "borrar_cat", "categorias"
    };

    private static final String[] _comandos_ayuda = {
        "Cierra la aplicación",
        "Muestra esta ayuda",
        "Selecciona cuenta: cuenta <nombre> (o 'cuenta personal')",
        "Muestra los participantes de cada cuenta",
        "Registrar: registrar <cant> <dd/MM/yyyy> <cat> [PagadorSiCompartida] [desc]",
        "Edita: editar <ID> <cant> <fecha> <cat> [Pagador] [desc]",
        "Borra un gasto por ID: borrar <id_gasto>",
        "Lista todos los gastos de la cuenta actual",
        "Crea una nueva categoría: nueva_cat <nombre>",
        "Elimina una categoría: borrar_cat <nombre>",
        "Lista todas las categorías disponibles"
    };

    public static byte stringToCommand(String comStr) {
        for (int i = 0; i < _comandos_validos_str.length; i++) {
            if (_comandos_validos_str[i].equalsIgnoreCase(comStr)) {
                return _comandos_validos[i];
            }
        }
        return COM_INVALIDO;
    }

    public static String commandToString(byte command) {
        for (int i = 0; i < _comandos_validos.length; i++) {
            if (_comandos_validos[i] == command) {
                return _comandos_validos_str[i];
            }
        }
        return null;
    }

    public static void imprimirAyuda() {
        System.out.println("--- Comandos Disponibles ---");
        for (int i = 0; i < _comandos_validos.length; i++) {
            System.out.println(String.format(" %-15s %s", _comandos_validos_str[i], _comandos_ayuda[i]));
        }
    }
}