package tds.gestiongastos.vista;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;

public class GastoVistaControlador {

    @FXML private TextField txtGastoImporte;
    @FXML private TextField txtGastoDescripcion;
    @FXML private DatePicker dateGastoFecha;
    @FXML private ComboBox<Categoria> comboGastoCategoria;
    
    private Gasto gastoEnEdicion;

    @FXML
    public void initialize() {
    	System.out.println("Abriendo ventana de Detalle de Gasto...");
    	
        List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
        comboGastoCategoria.setItems(FXCollections.observableArrayList(categorias));


        comboGastoCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return c == null ? null : c.getNombre();
            }
            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });
    }

    public void setGasto(Gasto gasto) {
        this.gastoEnEdicion = gasto;
        
        if (gasto != null) {
        	System.out.println("Cargando datos para editar gasto: " + gasto.getDescripcion());
        	
            txtGastoImporte.setText(String.valueOf(gasto.getCantidad()));
            txtGastoDescripcion.setText(gasto.getDescripcion());
            dateGastoFecha.setValue(gasto.getFecha());
            comboGastoCategoria.setValue(gasto.getCategoria());
        }
    }

    @FXML
    public void guardarGasto(ActionEvent event) {
        try {
            if (txtGastoImporte.getText().isEmpty() || dateGastoFecha.getValue() == null || comboGastoCategoria.getValue() == null) {
                mostrarAlerta("Campos vacíos", "Por favor rellena importe, fecha y categoría.");
                return;
            }

            double importe = Double.parseDouble(txtGastoImporte.getText());
            String desc = (txtGastoDescripcion.getText() != null) ? txtGastoDescripcion.getText() : "Sin concepto"; 
            String nombreCat = comboGastoCategoria.getValue().getNombre();
            
            if (gastoEnEdicion == null) {
            	System.out.println("Registrando NUEVO gasto -> Concepto: " + desc + " | Importe: " + importe + "€ | Categoría: " + nombreCat);
                Configuracion.getInstancia().getGestionGastos().registrarGasto(desc, importe, dateGastoFecha.getValue(), nombreCat
                );
            } else {
            	System.out.println("Guardando CAMBIOS en gasto -> Concepto: " + desc + " | Importe: " + importe + "€");
                Configuracion.getInstancia().getGestionGastos().modificarGasto(gastoEnEdicion, desc, importe, dateGastoFecha.getValue(), nombreCat
                );
            }

            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "El importe debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelarRegistro(ActionEvent event) {
    	System.out.println("Operación cancelada. Cerrando ventana de gasto.");
        cerrarVentana();
    }

    @FXML
    public void botonCategorias(ActionEvent event) {
    	System.out.println("Navegando a gestión de categorías desde la ventana de gasto...");
        Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
        initialize();
    }

    private void cerrarVentana() {
		System.out.println("Cerrando la ventana de gastos.");
        Stage stage = (Stage) txtGastoImporte.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}