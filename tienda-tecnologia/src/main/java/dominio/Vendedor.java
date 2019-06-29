package dominio;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;

public class Vendedor {

	public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
	public static final String EL_CODIGO_DEl_PRODUCTO_TIENE_TRES_VOCALES = "Este producto no cuenta con garantía extendida";

	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
		this.setRepositorioProducto(repositorioProducto);
		this.setRepositorioGarantia(repositorioGarantia);
	}

	public void generarGarantia(String codigo, String nombreCliente) {
		if (tieneGarantia(codigo)) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
		}
		
		if (contadorVocales(codigo) == 3) {
			throw new GarantiaExtendidaException(EL_CODIGO_DEl_PRODUCTO_TIENE_TRES_VOCALES);
		}

		Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
		GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto);
		garantiaExtendida.setNombreCliente(nombreCliente);
		garantiaExtendida.setFechaFinGarantia(
				calcularFechaGarantiaFinal(producto.getPrecio(), garantiaExtendida.getFechaSolicitudGarantia()));
		garantiaExtendida.setPrecioGarantia(calcularPrecioGarantia(producto.getPrecio()));
		repositorioGarantia.agregar(garantiaExtendida);

	}

	private Date calcularFechaGarantiaFinal(double precio, Date fechaSolicitudGarantia) {
		Date fechaGarantiaFinal = null;

		Calendar calendarioFechaGarantiaFinal = Calendar.getInstance();
		calendarioFechaGarantiaFinal.setTime(fechaSolicitudGarantia);

		if (precio <= 500000) {

			calendarioFechaGarantiaFinal.add(Calendar.DAY_OF_YEAR, 100);
			fechaGarantiaFinal = calendarioFechaGarantiaFinal.getTime();
			return fechaGarantiaFinal;
		}

		Calendar calendarioFechaSolicitud = Calendar.getInstance();
		calendarioFechaSolicitud.setTime(fechaSolicitudGarantia);
		calendarioFechaGarantiaFinal.add(Calendar.DAY_OF_YEAR, 200);

		int contadorLunes = contadorLunes(calendarioFechaGarantiaFinal, calendarioFechaSolicitud);

		calendarioFechaGarantiaFinal.add(Calendar.DAY_OF_YEAR, contadorLunes);

		if (calendarioFechaGarantiaFinal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendarioFechaGarantiaFinal.add(Calendar.DAY_OF_YEAR, 1);
		}

		fechaGarantiaFinal = calendarioFechaGarantiaFinal.getTime();
		return fechaGarantiaFinal;
	}

	private int contadorLunes(Calendar calendarioFechaGarantiaFinal, Calendar calendarioFechaSolicitud) {
		int contadorLunes = 0;

		while (calendarioFechaGarantiaFinal.after(calendarioFechaSolicitud)) {

			if (calendarioFechaSolicitud.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
				contadorLunes++;

			calendarioFechaSolicitud.add(Calendar.DAY_OF_YEAR, 1);
		}
		return contadorLunes;
	}

	private double calcularPrecioGarantia(double precio) {
		return precio > 500000 ? precio * 0.2 : precio * 0.1;
	}

	public boolean tieneGarantia(String codigo) {
		Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
		return producto != null ? true : false;
	}

	private long contadorVocales(String codigo) {
		String vocales = "AEIOU";
		return codigo.toUpperCase().chars().filter(c -> vocales.contains(String.valueOf((char) c)))
				.count();
	}

	public RepositorioProducto getRepositorioProducto() {
		return repositorioProducto;
	}

	public void setRepositorioProducto(RepositorioProducto repositorioProducto) {
		this.repositorioProducto = repositorioProducto;
	}

	public RepositorioGarantiaExtendida getRepositorioGarantia() {
		return repositorioGarantia;
	}

	public void setRepositorioGarantia(RepositorioGarantiaExtendida repositorioGarantia) {
		this.repositorioGarantia = repositorioGarantia;
	}

}
