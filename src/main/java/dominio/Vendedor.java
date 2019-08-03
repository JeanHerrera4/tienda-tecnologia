package dominio;

import dominio.repositorio.RepositorioProducto;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

	public static final double PRECIO_MAXIMO = 500000;
    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_TIENE_GARANTIA = "Este producto no cuenta con garantía extendida";
     
    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) {
    	
    	Date fechaSolicitudGarantia = new Date();
    	double precioGarantia = 0;
    	
    	if(!tieneGarantia(codigo)){
    		if(cantidadDeVocales(codigo)==3){
    			throw new GarantiaExtendidaException(EL_PRODUCTO_NO_TIENE_GARANTIA);
    		}else{
    		    Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    		    if(producto != null && producto.getCodigo() != null){
    		    	precioGarantia = calcularPrecioGarantia(producto.getPrecio());
    		    	Date fechaFinGarantia =  fechaFinGarantiaExtendida(fechaSolicitudGarantia, precioGarantia);
    		    	
    		    	GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto, fechaSolicitudGarantia, fechaFinGarantia, precioGarantia, nombreCliente);
    		    	repositorioGarantia.agregar(garantiaExtendida);
    		    }
    		    
    		}
    		
    	}else{
    		throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
    	}

    }
    
    private long cantidadDeVocales(String codigo){
    	String cadena = codigo.toLowerCase();
    	long contador = cadena.chars().filter(caracter -> caracter == 'a' || caracter == 'e' || caracter == 'i' ||
    			caracter == 'o' || caracter == 'u').count();
    	return contador;
    }
    
    private double calcularPrecioGarantia(Double precio){
    	double precioGarantiaExtendida;
    	if(precio > PRECIO_MAXIMO){
    		precioGarantiaExtendida = (precio * 20)/100;
    	}else{
    		precioGarantiaExtendida = (precio * 10)/100;
    	}
    	return precioGarantiaExtendida;
    }
    
    public Date fechaFinGarantiaExtendida(Date fechaSolicitudGarantia, Double precioProducto){
    	Calendar fechaFinGarantia = Calendar.getInstance();
    	fechaFinGarantia.setTime(fechaSolicitudGarantia);
    	int dias = 0;
    	if (precioProducto > PRECIO_MAXIMO){
    		dias = 200;
    	}else{
    		dias = 100;
    	}
    	for(int i = 0; i < dias; i++){
    		if(fechaFinGarantia.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY){
    			fechaFinGarantia.add(Calendar.DAY_OF_YEAR, 1);
    		}
    	}
    	
    	if(fechaFinGarantia.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
    		fechaFinGarantia.add(Calendar.DAY_OF_YEAR, 1);
    	}
    	return fechaFinGarantia.getTime(); 
    }

    public boolean tieneGarantia(String codigo) {
    	Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);
    	if(producto == null){
    	    return false;
    	}
    		return true;   	
    }

}
