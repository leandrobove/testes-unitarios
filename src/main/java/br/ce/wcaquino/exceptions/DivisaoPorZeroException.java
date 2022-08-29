package br.ce.wcaquino.exceptions;

public class DivisaoPorZeroException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DivisaoPorZeroException(String msg) {
		super(msg);
	}

}
