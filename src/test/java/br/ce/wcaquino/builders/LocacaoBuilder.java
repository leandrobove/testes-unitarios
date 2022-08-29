package br.ce.wcaquino.builders;

import java.util.Arrays;
import java.util.Date;

import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoBuilder {
	
	private Locacao locacao;
	
	private LocacaoBuilder() {
	}
	
	public static LocacaoBuilder umaLocacao() {
		LocacaoBuilder builder = new LocacaoBuilder();
		
		builder.locacao = new Locacao();
		builder.locacao.setUsuario(UsuarioBuilder.umUsuario().agora());
		builder.locacao.setFilmes(Arrays.asList(FilmeBuilder.umFilme().agora()));
		builder.locacao.setDataLocacao(new Date());
		builder.locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(1));
		builder.locacao.setValor(4.0);
		
		return builder;
	}
	
	public LocacaoBuilder comValor(Double valor) {
		this.locacao.setValor(valor);
		
		return this;
	}
	
	public LocacaoBuilder comDataRetorno(Date data) {
		this.locacao.setDataRetorno(data);
		
		return this;
	}
	
	public LocacaoBuilder comUsuario(Usuario usuario) {
		this.locacao.setUsuario(usuario);
		
		return this;
	}
	
	public LocacaoBuilder atrasada() {
		this.locacao.setDataLocacao(DataUtils.obterDataComDiferencaDias(-4));
		this.locacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		
		return this;
	}
	
	public Locacao agora() {
		return locacao;
	}

}
