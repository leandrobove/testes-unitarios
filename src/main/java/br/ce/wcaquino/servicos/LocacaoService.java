package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	private LocacaoDAO dao;
	private SPCService spcService;
	private EmailService emailService;

	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		if (usuario == null) {
			throw new LocadoraException("Usuário vazio");
		}

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio");
		}

		for (Filme objFilme : filmes) {
			if (objFilme.getEstoque() <= 0) {
				throw new FilmeSemEstoqueException();
			}
		}

		// verifica se o usuario está no spc
		boolean possuiNomeSujo;
		try {
			possuiNomeSujo = spcService.possuiNomeSujo(usuario);
		} catch (Exception e) {
			throw new LocadoraException("Sistema do SPC está fora do ar");
		}
		if (possuiNomeSujo) {
			throw new LocadoraException("O usuário possui o nome sujo e não realizar a locação");
		}

		Locacao locacao = new Locacao();

		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		Double valorTotal = 0d;

		for (int i = 0; i < filmes.size(); i++) {
			Filme filme = filmes.get(i);

			Double valorFilme = filme.getPrecoLocacao();

			switch (i) {
			case 2:
				valorFilme = valorFilme * 0.75;
				break;
			case 3:
				valorFilme = valorFilme * 0.50;
				break;
			case 4:
				valorFilme = valorFilme * 0.25;
				break;
			case 5:
				valorFilme = 0d;
				break;
			}

			valorTotal = valorTotal + valorFilme;
		}
		locacao.setValor(valorTotal);

		// Entrega no dia seguinte
		Date dataEntrega = new Date();

		dataEntrega = adicionarDias(dataEntrega, 1);

		// verifica se o retorno é no DOMINGO
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}

	public void notificarLocacoesQueNaoForamDevolvidasNaDataDeRetorno() {
		List<Locacao> locacoes = dao.obterLocacoesAtrasadas();

		for (Locacao locacao : locacoes) {
			// enviar e-mail para o usuario
			if (locacao.getDataRetorno().before(new Date())) {
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}

	public void prorrogarLocacao(Locacao locacao, int qtdDias) {
		Locacao novaLocacao = new Locacao();

		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(qtdDias));
		novaLocacao.setValor(locacao.getValor() * qtdDias);

		dao.salvar(novaLocacao);
	}

}