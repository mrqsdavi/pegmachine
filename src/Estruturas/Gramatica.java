package Estruturas;

public class Gramatica extends Padrao{

	private String nome;
	Padrao padrao;
	
	public Gramatica(String nome){
		setNome(nome);
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Padrao getPadrao(){
		return padrao;
	}
	
	public void setPadrao(Padrao padrao){
		this.padrao = padrao;
	}
	
	@Override
	public TipoPadrao getTipo() {
		// TODO Auto-generated method stub
		return TipoPadrao.GRAMATICA;
	}

	@Override
	public Gramatica gramatica() {
		// TODO Auto-generated method stub
		return this;
	}
	
	@Override
	public String toString(){
		return "("+nome+" <- "+padrao.toString()+")";
	}

}
