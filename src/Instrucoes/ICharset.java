package Instrucoes;

import java.util.Set;

public class ICharset implements Instrucao{

	private String texto;
	private Set<Character> set;
	
	public ICharset(String texto, Set<Character> set){
		setTexto(texto);
		setSet(set);
	}
	
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public Set<Character> getSet() {
		return set;
	}
	public void setSet(Set<Character> set) {
		this.set = set;
	}

	public boolean isCharecterIn(char caracter){
		return set.contains(caracter);
	}
	
	@Override
	public TipoInstrucao getTipoInstrucao() {
		// TODO Auto-generated method stub
		return TipoInstrucao.CHARSET;
	}

	@Override
	public Instrucoes.IChar IChar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.IChoice IChoice() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.ICommit ICommit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.IAny IAny() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICharset ICharset() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Instrucoes.IReturn IReturn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.ICall ICall() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.IJump IJump() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.IEnd IEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.IFail IFail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instrucoes.ICapture ICapture() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
