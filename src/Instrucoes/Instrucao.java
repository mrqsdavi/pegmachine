package Instrucoes;

public interface Instrucao {
	
	public TipoInstrucao getTipoInstrucao();
	public IChar IChar();
	public IChoice IChoice();
	public ICommit ICommit();
	public IAny IAny();
	public ICharset ICharset();
	public IReturn IReturn();
	public ICall ICall();
	public IJump IJump();
	public IEnd IEnd();
	public IFail IFail();
	public ICapture ICapture();
	
}
