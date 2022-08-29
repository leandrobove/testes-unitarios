package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.FilmeBuilder;
import br.ce.wcaquino.builders.LocacaoBuilder;
import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.matchers.MatchersProprios;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {

	@InjectMocks
	private LocacaoService locacaoService;

	@Mock
	private SPCService spcService;
	@Mock
	private LocacaoDAO locacaoDAO;
	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void deveAlugarFilme() throws Exception {

		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		Usuario usuario = UsuarioBuilder.umUsuario().agora();

		Filme filme1 = FilmeBuilder.umFilme().comValor(4.0).agora();
		Filme filme2 = FilmeBuilder.umFilme().comValor(4.0).agora();

		List<Filme> filmes = Arrays.asList(filme1, filme2);

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		error.checkThat(locacao.getValor(), is(equalTo(8.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)),
				CoreMatchers.is(true));

		error.checkThat(locacao.getDataLocacao(), MatchersProprios.ehHoje());
		error.checkThat(locacao.getDataRetorno(), MatchersProprios.ehHojeComDiferencaDeDias(1));

	}

	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {

		Usuario usuario = UsuarioBuilder.umUsuario().agora();

		Filme filme1 = FilmeBuilder.umFilmeSemEstoque().agora();

		List<Filme> filmes = Arrays.asList(filme1);

		locacaoService.alugarFilme(usuario, filmes);

		// System.out.println("Forma Elegante");
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException {

		Usuario usuario = null;
		Filme filme1 = FilmeBuilder.umFilme().agora();
		Filme filme2 = FilmeBuilder.umFilme().agora();

		List<Filme> filmes = Arrays.asList(filme1, filme2);

		try {
			locacaoService.alugarFilme(usuario, filmes);

			Assert.fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário vazio"));
		}

		// System.out.println("Forma Robusta");

	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {

		Usuario usuario = UsuarioBuilder.umUsuario().agora();

		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");

		locacaoService.alugarFilme(usuario, null);

		// System.out.println("Forma Nova");
	}

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado()
			throws FilmeSemEstoqueException, LocadoraException, ParseException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

		Usuario usuario = UsuarioBuilder.umUsuario().agora();

		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		Assert.assertThat(locacao.getDataRetorno(), MatchersProprios.caiEmUmaSegunda());
	}

	@Test
	public void naoDeveAlugarFilmeParaUsuarioComNomeSujo() throws Exception {

		Usuario usuario = UsuarioBuilder.umUsuario().agora();

		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(), FilmeBuilder.umFilme().agora());

		Mockito.when(spcService.possuiNomeSujo(usuario)).thenReturn(true); // forçar lançar exceção quando verificar se
																			// o nome do usuário está sujo

		try {
			locacaoService.alugarFilme(usuario, filmes);
			Assert.fail();

		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("O usuário possui o nome sujo e não realizar a locação"));
		}

		Mockito.verify(spcService).possuiNomeSujo(usuario);
	}

	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {

		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().comNome("Usuário em dia").agora();
		Usuario usuario3 = UsuarioBuilder.umUsuario().comNome("Outro usuário atrasado").agora();

		List<Locacao> locacoesPendentes = Arrays.asList(
				LocacaoBuilder.umaLocacao().atrasada().comUsuario(usuario).agora(),
				LocacaoBuilder.umaLocacao().comUsuario(usuario2).agora(),
				LocacaoBuilder.umaLocacao().atrasada().comUsuario(usuario3).agora());

		Mockito.when(locacaoDAO.obterLocacoesAtrasadas()).thenReturn(locacoesPendentes);

		locacaoService.notificarLocacoesQueNaoForamDevolvidasNaDataDeRetorno();

		Mockito.verify(emailService).notificarAtraso(usuario);
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verify(emailService).notificarAtraso(usuario3);
		Mockito.verifyNoMoreInteractions(emailService);
	}

	@Test
	public void deveLancarErroAoConsultarNomeSujoNoSPCCasoOSistemaEstejaForaDoAr() throws Exception {

		Usuario usuario = UsuarioBuilder.umUsuario().comNome("Usuário 1").agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora(), FilmeBuilder.umFilme().agora());

		Mockito.when(spcService.possuiNomeSujo(usuario)).thenThrow(new Exception("Falha catastrófica"));

		exception.expect(LocadoraException.class);
		exception.expectMessage("Sistema do SPC está fora do ar");

		locacaoService.alugarFilme(usuario, filmes);
	}

	// mesmo teste mas de uma forma diferente do de cima
	@Test
	public void deveLancarErroAoConsultarNomeSujoNoSPCCasoOSistemaEstejaForaDoAr2()
			throws LocadoraException, FilmeSemEstoqueException {

		Usuario u1 = UsuarioBuilder.umUsuario().agora();
		List<Filme> filmes = Arrays.asList(FilmeBuilder.umFilme().agora());

		try {
			Mockito.when(spcService.possuiNomeSujo(u1)).thenThrow(Exception.class);

			locacaoService.alugarFilme(u1, filmes);

			Assert.fail();
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Sistema do SPC está fora do ar"));
		}
	}

	@Test
	public void deveProrrogarUmaLocacao() {
		// cenario
		Locacao locacao = LocacaoBuilder.umaLocacao().agora();

		// ação
		locacaoService.prorrogarLocacao(locacao, 3);

		// verificação
		ArgumentCaptor<Locacao> argCapt = ArgumentCaptor.forClass(Locacao.class); // serve para capturar a locação nova
																					// após a atualização no DAO

		Mockito.verify(locacaoDAO).salvar(argCapt.capture());

		Locacao locacaoRetornada = argCapt.getValue();

		//System.out.println(argCapt.getAllValues());

		/*
		 * Assert.assertThat(locacaoRetornada.getValor(), CoreMatchers.is(13.00));
		 * Assert.assertThat(locacaoRetornada.getDataLocacao(),
		 * CoreMatchers.is(MatchersProprios.ehHoje()));
		 * Assert.assertThat(locacaoRetornada.getDataRetorno(),
		 * CoreMatchers.is(MatchersProprios.ehHojeComDiferencaDeDias(3)));
		 */

		error.checkThat(locacaoRetornada.getValor(), CoreMatchers.is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), CoreMatchers.is(MatchersProprios.ehHoje()));
		error.checkThat(locacaoRetornada.getDataRetorno(),
				CoreMatchers.is(MatchersProprios.ehHojeComDiferencaDeDias(3)));
	}
}
