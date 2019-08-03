package dominio.integracion;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String NOMBRE_CLIENTE = "Jean";
	private static final String CODIGO = "abc34dei";
	private static final double PRECIO_MENOR = 200000;
	private static final double PRECIO_MAYOR = 700000;
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
		try {
			
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}
	
	@Test
	public void productoNoTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).conCodigo(CODIGO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		try {
			
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_NO_TIENE_GARANTIA, e.getMessage());
		}
	}
	
	@Test
	public void generarGarantiaPrecioMenorMaximoTest(){
		//arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(NOMBRE_CLIENTE).conPrecio(PRECIO_MENOR).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);
		
		//act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
		
		//assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

	}
	
	@Test
	public void generarGarantiaPrecioMayorMaximoTest(){
		//arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(NOMBRE_CLIENTE).conPrecio(PRECIO_MAYOR).build();
				
		repositorioProducto.agregar(producto);
				
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);
				
		//act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
				
		//assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
	}
	
	@Test
	public void fechaFinGarantiaExtendidaTest(){
		//arrange
		Date fechaSolicitudGarantia = new Date();
		Producto producto = new ProductoTestDataBuilder().conNombre(NOMBRE_CLIENTE).conPrecio(PRECIO_MAYOR).build();
						
		repositorioProducto.agregar(producto);
						
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);
						
		//act
		Calendar fechaInicioVigencia = Calendar.getInstance();
		fechaInicioVigencia.set(2019, 07, 02);
		Date fecha = vendedor.fechaFinGarantiaExtendida(fechaInicioVigencia.getTime(), producto.getPrecio());
		
		//assert
		Assert.assertNotNull(fecha);
		
	}
	
}
