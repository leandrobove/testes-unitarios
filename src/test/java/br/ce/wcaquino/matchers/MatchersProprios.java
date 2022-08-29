package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}

	public static DiaSemanaMatcher caiEmUmaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DataDiferencaDiasMatcher ehHoje() {
		return new DataDiferencaDiasMatcher(0);
	}
	
	public static DataDiferencaDiasMatcher ehHojeComDiferencaDeDias(Integer qtdDias) {
		return new DataDiferencaDiasMatcher(qtdDias);
	}
}
