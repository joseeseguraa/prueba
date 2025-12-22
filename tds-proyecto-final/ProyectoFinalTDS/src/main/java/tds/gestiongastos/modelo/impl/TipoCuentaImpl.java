package tds.gestiongastos.modelo.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tds.gestiongastos.modelo.Gasto;
import tds.gestiongastos.modelo.TipoCuenta;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({ @JsonSubTypes.Type(value = CuentaPersonalImpl.class, name = "personal"),
		@JsonSubTypes.Type(value = CuentaCompartidaImpl.class, name = "compartida") })
public abstract class TipoCuentaImpl implements TipoCuenta {

	@JsonProperty("nombre")
	private String nombre;

	@JsonProperty("gastos")
	protected List<GastoImpl> gastos;

	public TipoCuentaImpl() {
		this.gastos = new ArrayList<>();
	}

	public TipoCuentaImpl(String nombre) {
		this();
		this.nombre = nombre;
	}

	@Override
	public String getNombre() {
		return nombre;
	}

	@Override
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public List<Gasto> getGastos() {
		return new ArrayList<>(gastos);
	}

	@Override
	public void agregarGasto(Gasto gasto) {
		if (gasto instanceof GastoImpl) {
			this.gastos.add((GastoImpl) gasto);
		}
	}

	@Override
	public void eliminarGasto(Gasto gasto) {
		this.gastos.remove(gasto);
	}

	@Override
    @JsonIgnore
    public double getSaldoTotal() {
        return gastos.stream().mapToDouble(GastoImpl::getCantidad).sum();
    }
}