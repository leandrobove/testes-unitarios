package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraMockTest {

	@Mock
	private Calculadora calculadoraMock;
	
	@Spy
	private Calculadora calculadoraSpy;
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void devoMostrarDiferencaEntreMockESpy() {
		
		Mockito.when(calculadoraMock.somar(5, 6)).thenCallRealMethod();
		Mockito.when(calculadoraMock.somar(1, 3)).thenReturn(8);
		Mockito.when(calculadoraSpy.somar(1, 3)).thenReturn(8);
		Mockito.when(calculadoraSpy.somar(9, 10)).thenCallRealMethod();
		
		System.out.println("Mock usando o método real: " + calculadoraMock.somar(5, 6));
		System.out.println("Mock: " + calculadoraMock.somar(1, 2));
		System.out.println("Spy: " + calculadoraSpy.somar(1, 2));
		System.out.println("Spy usando o método real: " + calculadoraSpy.somar(9, 10));
		
	}

	@Test
	public void teste() {

		Calculadora calc = Mockito.mock(Calculadora.class);

		ArgumentCaptor<Integer> argCaptor = ArgumentCaptor.forClass(Integer.class);

		//força a função somar() retornar 5 independentemente dos valores passados
		Mockito.when(calc.somar(argCaptor.capture(), argCaptor.capture())).thenReturn(5); 

		//compara o retorno da funcao somar que previamente foi mockada
		Assert.assertEquals(5, calc.somar(200, 3));
		
		//System.out.println(argCaptor.getAllValues());

	}

}
