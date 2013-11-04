package Instrucoes;

public class IChoice implements Instrucao{

	private String label;
	
	public IChoice(String label){
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public TipoInstrucao getTipoInstrucao() {
		// TODO Auto-generated method stub
		return TipoInstrucao.CHOICE;
	}

	@Override
	public Instrucoes.IChar IChar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IChoice IChoice() {
		// TODO Auto-generated method stub
		return this;
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
