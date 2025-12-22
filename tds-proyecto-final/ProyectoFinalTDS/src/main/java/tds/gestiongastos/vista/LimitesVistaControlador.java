package tds.gestiongastos.vista;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tds.gestiongastos.main.Configuracion;
import tds.gestiongastos.modelo.Alerta;
import tds.gestiongastos.modelo.Categoria;
import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;

public class LimitesVistaControlador {

	@FXML
	private ListView<String> listaProgreso;
	@FXML
	private ComboBox<String> comboFiltroPeriodo;
	@FXML
	private ComboBox<Categoria> comboFiltroCategoria;

	@FXML
	public void initialize() {
		System.out.println("Abriendo ventana de Estado de Límites...");
		comboFiltroPeriodo.setItems(FXCollections.observableArrayList("Todas", "Mensual", "Semanal"));
		comboFiltroPeriodo.setValue("Todas");

		if (comboFiltroCategoria != null) {
			List<Categoria> categorias = Configuracion.getInstancia().getGestionGastos().getTodasCategorias();
			comboFiltroCategoria.getItems().add(null);
			comboFiltroCategoria.getItems().addAll(categorias);
			comboFiltroCategoria.setButtonCell(new ListCell<Categoria>() {
				@Override
				protected void updateItem(Categoria item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText("Todas las Categorías");
					} else {
						setText(item.getNombre());
					}
				}
			});

			comboFiltroCategoria.setCellFactory(lv -> new ListCell<Categoria>() {
				@Override
				protected void updateItem(Categoria item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText("Todas las Categorías");
					} else {
						setText(item.getNombre());
					}
				}
			});

			comboFiltroCategoria.getSelectionModel().selectFirst();
		}

		Platform.runLater(() -> {
			System.out.println("Cargando datos de alertas y gastos...");
			cargarEstadoLimites();
		});

	}

	@FXML
	public void aplicarFiltro(ActionEvent event) {
		String p = "N/A";
        if (comboFiltroPeriodo.getValue() != null) {
            p = comboFiltroPeriodo.getValue();
        }
        
        String c = "Todas";
        if (comboFiltroCategoria != null && comboFiltroCategoria.getValue() != null) {
            c = comboFiltroCategoria.getValue().getNombre();
        }
        
        System.out.println("Aplicando filtros -> Periodo: " + p + " | Categoría: " + c);
		
		cargarEstadoLimites();
	}

	private void cargarEstadoLimites() {
		TipoCuenta cuenta = Configuracion.getInstancia().getGestionGastos().getCuentaActiva();
		if (cuenta == null)
			return;

		List<Alerta> alertas = Configuracion.getInstancia().getGestionGastos().getAlertas();
		if (alertas == null)
			alertas = new ArrayList<>();

		List<Gasto> todosLosGastos = cuenta.getGastos();
		if (todosLosGastos == null)
			todosLosGastos = new ArrayList<>();

		String filtroPeriodicidad = "Todas";
		if (comboFiltroPeriodo.getValue() != null) {
			filtroPeriodicidad = comboFiltroPeriodo.getValue();
		}

		Categoria filtroCategoria = null;
		if (comboFiltroCategoria != null) {
			filtroCategoria = comboFiltroCategoria.getValue();
		}

		List<String> items = new ArrayList<>();

		for (Alerta alerta : alertas) {

			if (!filtroPeriodicidad.equals("Todas") && !alerta.getTipo().equalsIgnoreCase(filtroPeriodicidad)) {
				continue;
			}

			if (filtroCategoria != null) {
				if (alerta.getCategoria() == null)
					continue;
				if (!alerta.getCategoria().equals(filtroCategoria))
					continue;
			}

			double gastado = calcularGastoParaAlerta(alerta, todosLosGastos);

			String nombreCat = (alerta.getCategoria() != null) ? alerta.getCategoria().getNombre() : "General";

			String estado = String.format("[%s] %s: %.2f € / %.2f €", alerta.getTipo(), nombreCat, gastado, alerta.getLimite());

			if (gastado > alerta.getLimite()) {
				estado += " EXCEDIDO";
			} else {
				double porcentaje = (alerta.getLimite() > 0) ? (gastado / alerta.getLimite()) * 100 : 0;
				estado += String.format(" (%.0f%%)", porcentaje);
			}

			items.add(estado);
		}

		if (items.isEmpty()) {
            if (alertas.isEmpty()) {
                items.add("No hay límites configurados.");
            } else {
                items.add("No se encontraron alertas con estos filtros.");
            }
        }
		
		listaProgreso.setItems(FXCollections.observableArrayList(items));
	}

	private double calcularGastoParaAlerta(Alerta alerta, List<Gasto> gastos) {
		LocalDate hoy = LocalDate.now();
		LocalDate fechaInicio;
		LocalDate fechaFin = hoy;

		if (alerta.getTipo().equalsIgnoreCase("Mensual")) {
			fechaInicio = hoy.withDayOfMonth(1);
		} else {
			fechaInicio = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		}

		return gastos.stream().filter(g -> !g.getFecha().isBefore(fechaInicio) && !g.getFecha().isAfter(fechaFin)) // Rango
																													// fecha
				.filter(g -> {
					if (alerta.getCategoria() != null) {
						return g.getCategoria().equals(alerta.getCategoria());
					}
					return true;
				}).mapToDouble(Gasto::getCantidad).sum();
	}

	@FXML
	public void cerrar(ActionEvent event) {
		System.out.println("Cerrando la ventana de límites.");
		
		Stage stage = (Stage) listaProgreso.getScene().getWindow();
		stage.close();
	}

}
