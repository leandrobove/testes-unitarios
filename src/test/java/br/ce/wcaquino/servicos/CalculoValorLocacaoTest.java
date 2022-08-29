package br.ce.wcaquino.servicos;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

	@Parameter(value = 0)
	public List<Filme> filmes;

	@Parameter(value = 1)
	public Double valorLocacao;

	@Parameter(value = 2)
	public String descricao;

	@InjectMocks
	private LocacaoService locacaoService;

	@Mock
	private LocacaoDAO dao;
	@Mock
	private SPCService spcService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	private static Filme filme1 = FilmeBuilder.umFilme().agora();
	private static Filme filme2 = FilmeBuilder.umFilme().agora();
	private static Filme filme3 = FilmeBuilder.umFilme().agora();
	private static Filme filme4 = FilmeBuilder.umFilme().agora();
	private static Filme filme5 = FilmeBuilder.umFilme().agora();
	private static Filme filme6 = FilmeBuilder.umFilme().agora();
	private static Filme filme7 = FilmeBuilder.umFilme().agora();

	@Parameters(name = "{2}")
	public static Collection<Object[]> getParametros() {

		return Arrays.asList(new Object[][] { { Arrays.asList(filme1, filme2), 8d, "2 Filmes: Sem Desconto" },
				{ Arrays.asList(filme1, filme2, filme3), 11d, "3 Filme 25% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4), 13d, "4 Filme 50% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14d, "5 Filme 75% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14d, "6 Filme 100% de desconto" },
				{ Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18d,
						"7 Filmes: Sem Desconto" } });
	}

	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {

		Usuario usuario = new Usuario("Leandro");

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// valor esperado: 4+4+3+2+1 = 14
		Assert.assertThat(locacao.getValor(), CoreMatchers.is(valorLocacao));
	}

}
