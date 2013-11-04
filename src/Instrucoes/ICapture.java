package Instrucoes;

public class ICapture implements Instrucao{

	private TipoCapture tipo;
	
	public ICapture(TipoCapture tipo){
		setTipo(tipo);
	}
	
	public TipoCapture getTipo() {
		return tipo;
	}

	public void setTipo(TipoCapture tipo) {
		this.tipo = tipo;
	}
	
	@Override
	public TipoInstrucao getTipoInstrucao() {
		// TODO Auto-generated method stub
		return TipoInstrucao.CAPTURE;
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
	public ICapture ICapture() {
		// TODO Auto-generated method stub
		return this;
	}

}
