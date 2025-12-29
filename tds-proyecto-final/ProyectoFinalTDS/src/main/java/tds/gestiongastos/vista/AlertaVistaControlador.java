package tds.gestiongastos.vista;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;

public class AlertaVistaControlador {

	@FXML private TextField txtNombreAlerta;
	@FXML private TextField txtAlertaLimite;
	@FXML private ListView<Alerta> listaAlertas;
	@FXML private ComboBox<String> comboPeriodicidad;
	@FXML private ComboBox<Categoria> comboAlertaCategoria;
	@FXML private Button btnNuevaCategoria;
	@FXML private Button btnGuardar;

	private Alerta alertaEnEdicion = null;

	@FXML
	public void initialize() {
		comboPeriodicidad.setItems(FXCollections.observableArrayList("Mensual", "Semanal"));

		comboPeriodicidad.setButtonCell(new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText("Seleccionar...");
					setStyle("-fx-text-fill: gray;");
				} else {
					setText(item);
					setStyle("-fx-text-fill: black;");
				}
			}
		});

		List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
		comboAlertaCategoria.setItems(FXCollections.observableArrayList(categorias));

		comboAlertaCategoria.setButtonCell(new ListCell<Categoria>() {
			@Override
			protected void updateItem(Categoria item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText("Seleccionar...");
					setStyle("-fx-text-fill: gray;");
				} else {
					String n = item.getNombre();
					int idx = n.indexOf("_");
					setText((idx != -1) ? n.substring(idx + 1) : n);
					setStyle("-fx-text-fill: black;");
				}
			}
		});

		comboAlertaCategoria.setConverter(new StringConverter<Categoria>() {
			@Override
			public String toString(Categoria c) {
				if (c == null) return "";
				String n = c.getNombre();
				int idx = n.indexOf("_");
				return (idx != -1) ? n.substring(idx + 1) : n;
			}
			@Override
			public Categoria fromString(String string) { return null; }
		});

		listaAlertas.setCellFactory(param -> new ListCell<Alerta>() {
			@Override
			protected void updateItem(Alerta item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					String catLimpia = "General";
					if (item.getCategoria() != null) {
						String n = item.getCategoria().getNombre();
						int idx = n.indexOf("_");
						catLimpia = (idx != -1) ? n.substring(idx + 1) : n;
					}
					setText(item.getTipo() + ": " + item.getLimite() + "€ (" + catLimpia + ")");
				}
			}
		});

		listaAlertas.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				botonEditarAlerta(null);
			}
		});

		listaAlertas.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		cargarListaAlertas();
	}

	private void cargarListaAlertas() {
		List<Alerta> alertas = Configuracion.getInstancia().getGestionGastos().getAlertas();
		listaAlertas.setItems(FXCollections.observableArrayList(alertas));
	}

	@FXML
	public void guardarAlerta(ActionEvent event) {
		try {
			if (txtAlertaLimite.getText().isEmpty()) {
				mostrarMensaje("Debes introducir un límite.");
				return;
			}

			double limite = Double.parseDouble(txtAlertaLimite.getText());

			if (alertaEnEdicion != null) {
				Configuracion.getInstancia().getGestionGastos().modificarAlerta(alertaEnEdicion, limite);
				mostrarMensaje("Alerta actualizada correctamente.");
			} else {
				String tipo = comboPeriodicidad.getValue();
				Categoria cat = comboAlertaCategoria.getValue();

				if (tipo == null) {
					mostrarMensaje("Selecciona un tipo de alerta (Mensual/Semanal)");
					return;
				}

				boolean exito = Configuracion.getInstancia().getGestionGastos().configurarAlerta(tipo, limite, cat);
				if (!exito) {
					mostrarMensaje("Ya existe una alerta " + tipo + " para esta categoría.");
				}
			}

			cargarListaAlertas();
			limpiarCampos();

		} catch (NumberFormatException e) {
			mostrarMensaje("El límite debe ser un número válido.");
		}
	}

	@FXML
	public void botonEditarAlerta(ActionEvent event) {
		Alerta seleccionada = listaAlertas.getSelectionModel().getSelectedItem();
		if (seleccionada != null) {
			this.alertaEnEdicion = seleccionada;
			txtAlertaLimite.setText(String.valueOf(seleccionada.getLimite()));
			comboPeriodicidad.setValue(seleccionada.getTipo());
			comboAlertaCategoria.setValue(seleccionada.getCategoria());
			
			comboPeriodicidad.setDisable(true);      // Bloquea el combo de tiempo
            comboAlertaCategoria.setDisable(true);   // Bloquea el combo de categoría
            
            if (btnNuevaCategoria != null) {
                btnNuevaCategoria.setDisable(true);  // Bloquea el botón de crear categoría (+)
            }
			
			if (btnGuardar != null) {
				btnGuardar.setText("Actualizar Alerta"); 
			}
			txtAlertaLimite.requestFocus();
		} else {
			mostrarMensaje("Por favor, selecciona una alerta de la lista para editar.");
		}
	}

	@FXML
	public void eliminarAlerta(ActionEvent event) {
		List<Alerta> seleccionadas = new ArrayList<>(listaAlertas.getSelectionModel().getSelectedItems());
		if (seleccionadas.isEmpty()) {
			mostrarMensaje("Por favor, selecciona al menos una alerta para eliminar.");
			return;
		}

		Alert confirm = new Alert(AlertType.CONFIRMATION);
		confirm.setTitle("Eliminar Alertas");
		confirm.setHeaderText("Vas a eliminar " + seleccionadas.size() + " alerta(s).");
		confirm.setContentText("¿Estás seguro?");

		if (confirm.showAndWait().get() == ButtonType.OK) {
			Configuracion.getInstancia().getGestionGastos().borrarAlerta(seleccionadas);
			cargarListaAlertas();
			if (alertaEnEdicion != null) limpiarCampos();
		}
	}

	private void limpiarCampos() {
		Platform.runLater(() -> {
			if (txtNombreAlerta != null) txtNombreAlerta.clear();
			txtAlertaLimite.clear();
			comboPeriodicidad.setValue(null);
			comboAlertaCategoria.setValue(null);
			
			comboPeriodicidad.setDisable(false);
            comboAlertaCategoria.setDisable(false);
            
            if (btnNuevaCategoria != null) {
                btnNuevaCategoria.setDisable(false);
            }
			
			alertaEnEdicion = null;
			if (btnGuardar != null) btnGuardar.setText("Guardar Alerta");
			if (txtAlertaLimite.getParent() != null) txtAlertaLimite.getParent().requestFocus();
		});
	}

	private void mostrarMensaje(String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	@FXML
	public void cancelarRegistro(ActionEvent event) {
		((Stage) txtAlertaLimite.getScene().getWindow()).close();
	}

	@FXML
	public void botonCategorias(ActionEvent event) {
		Configuracion.getInstancia().getSceneManager().showNuevaCategoria();
		List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
		comboAlertaCategoria.setItems(FXCollections.observableArrayList(categorias));
	}
}