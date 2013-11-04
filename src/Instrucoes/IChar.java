package Instrucoes;


public class IChar implements Instrucao{

	private char caracter;
	private boolean isVazio;
	
	public IChar(char caracter){
		this.caracter = caracter;
		setVazio(false);
	}
	
	public IChar(boolean isVazio){
		setVazio(isVazio);
	}

	public char getCaracter() {
		return caracter;
	}

	public void setCaracter(char caracter) {
		this.caracter = caracter;
	}
	
	public boolean isVazio() {
		return isVazio;
	}

	public void setVazio(boolean isVazio) {
		this.isVazio = isVazio;
	}

	@Override
	public TipoInstrucao getTipoInstrucao() {
		// TODO Auto-generated method stub
		return TipoInstrucao.CHAR;
	}

	@Override
	public IChar IChar() {
		// TODO Auto-generated method stub
		return this;
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
	public Instrucoes.ICharset ICharset() {
		// TODO Auto-generated method stub
		return null;
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
