package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.DivisaoPorZeroException;

public class CalculadoraTest {

	private Calculadora calc;

	@Before
	public void setup() {
		calc = new Calculadora();
	}

	@Test
	public void somarDoisValores() {
		// cenario
		int a = 5;
		int b = 3;

		// acao
		int resultado = calc.somar(a, b);

		// verificacao
		Assert.assertEquals(8, resultado);
	}

	@Test
	public void subtrairDoisValores() {

		int a = 8;
		int b = 5;

		int total = calc.subtrair(a, b);

		Assert.assertEquals(3, total);
	}

	@Test
	public void dividirDoisValores() throws DivisaoPorZeroException {

		int a = 6;
		int b = 3;

		int total = calc.dividir(a, b);

		Assert.assertEquals(2, total);
	}

	@Test(expected = DivisaoPorZeroException.class)
	public void lancarExcecaoDivisaoPorZero() throws DivisaoPorZeroException {

		int a = 6;
		int b = 0;

		calc.dividir(a, b);
	}

}
