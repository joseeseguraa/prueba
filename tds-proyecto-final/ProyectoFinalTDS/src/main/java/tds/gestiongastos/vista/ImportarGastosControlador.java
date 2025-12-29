package tds.gestiongastos.vista;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.CuentaCompartida;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.ImportadorGastos;
import tds.gestiongastos.modelo.TipoCuenta;
import tds.gestiongastos.modelo.impl.FactoriaImportadoresImpl;

public class ImportarGastosControlador {

    @FXML private TextField txtRutaArchivo;
    private File archivoSeleccionado;

    @FXML public void initialize() { }

    @FXML
    void botonSeleccionarArchivo(ActionEvent event) {
        System.out.println(">>> Acción de Usuario: Click en 'Seleccionar Archivo'");
        
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar archivo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        archivoSeleccionado = fc.showOpenDialog(txtRutaArchivo.getScene().getWindow());
        if (archivoSeleccionado != null) {
            txtRutaArchivo.setText(archivoSeleccionado.getAbsolutePath());
            System.out.println("Archivo seleccionado: " + archivoSeleccionado.getName());
        } else {
            System.out.println("Selección de archivo cancelada por el usuario.");
        }
    }

    @FXML
    void botonImportar(ActionEvent event) {
        System.out.println(">>> Acción de Usuario: Click en 'Importar'");

        if (archivoSeleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un archivo primero.", AlertType.WARNING);
            return;
        }

        try {
            String nombre = archivoSeleccionado.getName();
            String extension = nombre.substring(nombre.lastIndexOf('.') + 1);

            ImportadorGastos importador = FactoriaImportadoresImpl.crearImportador(extension);
            if (importador == null) {
                mostrarAlerta("Error", "No hay importador para la extensión ." + extension, AlertType.ERROR);
                return;
            }

            List<Gasto> gastosCrudos = importador.importarGastos(archivoSeleccionado);
            if (gastosCrudos.isEmpty()) {
                mostrarAlerta("Aviso", "El archivo está vacío o no tiene gastos válidos.", AlertType.WARNING);
                return;
            }

            procesarYRegistrar(gastosCrudos);
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Fallo al importar: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void procesarYRegistrar(List<Gasto> gastos) {
        var gestionGastos = Configuracion.getInstancia().getGestionGastos();
        TipoCuenta cuentaActiva = gestionGastos.getCuentaActiva();
        
        int guardados = 0;
        List<String> errores = new ArrayList<>();
        Set<String> mensajesAlertasUnicos = new HashSet<>(); 
        
        List<Categoria> categoriasExistentes = gestionGastos.getTodasCategorias();

        for (Gasto g : gastos) {
            String pagadorCSV = g.getPagador();
            String pagadorFinal = null;
            String nombreCatCSV = g.getCategoria().getNombre().trim(); 
            String nombreCatFinal = nombreCatCSV; 

            if (cuentaActiva instanceof CuentaCompartida) {
                if ("Me".equalsIgnoreCase(pagadorCSV) || "Yo".equalsIgnoreCase(pagadorCSV)) {
                    pagadorFinal = "Yo";
                } else if (esPagadorValidoEnCompartida((CuentaCompartida) cuentaActiva, pagadorCSV)) {
                    pagadorFinal = pagadorCSV;
                } else {
                    errores.add("Ignorado: Usuario '" + pagadorCSV + "' no pertenece al grupo.");
                    continue; 
                }
            } else {
                pagadorFinal = cuentaActiva.getNombre();
            }

            Optional<Categoria> catCoincidente = categoriasExistentes.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombreCatCSV) || 
                             c.getNombre().endsWith("_" + nombreCatCSV))
                .findFirst();

            if (catCoincidente.isPresent()) {
                nombreCatFinal = catCoincidente.get().getNombre();
            } else {
                try {
                    gestionGastos.registrarCategoria(nombreCatCSV);
                    categoriasExistentes = gestionGastos.getTodasCategorias();
                    nombreCatFinal = nombreCatCSV; 
                } catch (Exception e) {
                    errores.add("Error creando cat. '" + nombreCatCSV + "': " + e.getMessage());
                    continue;
                }
            }

            try {
                // Al llamar a registrarGasto, GestionGastos ya imprimirá su propio log detallado
                List<String> alertasDeEsteGasto = gestionGastos.registrarGasto(g.getDescripcion(), g.getCantidad(), g.getFecha(), nombreCatFinal, pagadorFinal);
                
                if (alertasDeEsteGasto != null && !alertasDeEsteGasto.isEmpty()) {
                    mensajesAlertasUnicos.addAll(alertasDeEsteGasto);
                }
                guardados++;
            } catch (Exception e) {
                errores.add("Error guardando: " + e.getMessage());
            }
        }

        boolean hayAlertas = !mensajesAlertasUnicos.isEmpty();
        boolean hayErrores = !errores.isEmpty();

        if (hayAlertas || hayErrores) {
            StringBuilder reporte = new StringBuilder();

            if (hayAlertas) {
                reporte.append("=== LÍMITES SUPERADOS DURANTE LA IMPORTACIÓN ===\n");
                String nombreCuenta = cuentaActiva.getNombre() + "_";
                for (String msg : mensajesAlertasUnicos) {
                    reporte.append("- ").append(msg.replace(nombreCuenta, "")).append("\n");
                }
                reporte.append("\n");
            }

            if (hayErrores) {
                reporte.append("=== INCIDENCIAS EN IMPORTACIÓN ===\n");
                reporte.append(String.join("\n", errores));
            }

            mostrarAlertaDetallada("Resumen de Importación", 
                "Se han procesado " + guardados + " gastos.", 
                reporte.toString());
        } else {
            mostrarAlertaInfo("Éxito Total", "Importación completada (" + guardados + " gastos). Sin alertas.");
        }
    }

    private boolean esPagadorValidoEnCompartida(CuentaCompartida cuenta, String pagador) {
        return cuenta.getParticipantes().stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(pagador));
    }

    @FXML void botonCancelar(ActionEvent event) { 
        System.out.println(">>> Acción de Usuario: Click en 'Cancelar'");
        cerrarVentana(); 
    }
    
    private void cerrarVentana() { ((Stage) txtRutaArchivo.getScene().getWindow()).close(); }
    private void mostrarAlerta(String t, String m, AlertType tipo) { 
        Alert a = new Alert(tipo); a.setTitle(t); a.setContentText(m); a.showAndWait(); 
    }
    private void mostrarAlertaInfo(String t, String m) { mostrarAlerta(t, m, AlertType.INFORMATION); }

    private void mostrarAlertaDetallada(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setResizable(true);

        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 0);

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefSize(500, 400);

        alert.showAndWait();
    }
}