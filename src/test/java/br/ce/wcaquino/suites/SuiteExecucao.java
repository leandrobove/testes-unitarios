package br.ce.wcaquino.suites;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Suite.SuiteClasses;

import br.ce.wcaquino.servicos.CalculadoraTest;
import br.ce.wcaquino.servicos.CalculoValorLocacaoTest;
import br.ce.wcaquino.servicos.LocacaoServiceTest;

/*
 * SUITE serve para executar todos as classes de testes informadas abaixo de uma só vez.
 */

//@RunWith(Suite.class)
@SuiteClasses(value = { CalculadoraTest.class, CalculoValorLocacaoTest.class, LocacaoServiceTest.class})
public class SuiteExecucao {
	
	
	@BeforeClass
	public static void before() {
		System.out.println("Antes");
	}
	
	@AfterClass
	public static void after() {
		System.out.println("Depois");
	}

}
