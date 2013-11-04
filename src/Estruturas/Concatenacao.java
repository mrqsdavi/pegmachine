package Estruturas;

import java.util.ArrayList;

public class Concatenacao extends Padrao{

	private ArrayList<Padrao> padroes;
	
	public Concatenacao(ArrayList<Padrao> padroes){
		setPadroes(padroes);
	}
	
	public Concatenacao(Padrao padrao){
		padroes = new ArrayList<Padrao>();
		addPadrao(padrao);
	}
	
	public ArrayList<Padrao> getPadroes() {
		return padroes;
	}

	public void setPadroes(ArrayList<Padrao> padroes) {
		this.padroes = padroes;
	}
	
	public void addPadrao(Padrao padrao){
		padroes.add(padrao);
	}
	
	@Override
	public TipoPadrao getTipo() {
		// TODO Auto-generated method stub
		return TipoPadrao.CONCATENACAO;
	}

	@Override
	public Concatenacao concatenacao() {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public String toString(){
		
		String retorno = "";
		
		for(int i = 0; i < padroes.size(); i++){
			Padrao padraoIteracao = padroes.get(i);
			retorno += padraoIteracao.toString();
		}
		
		return retorno;		
	}

}
