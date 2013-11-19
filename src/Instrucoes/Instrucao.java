package Instrucoes;

public abstract class Instrucao {
	
	public TipoInstrucao getTipoInstrucao(){
		return null;
	}
	
	public IChar IChar(){
		return null;
	}
	public IChoice IChoice(){
		return null;
	}
	
	public ICommit ICommit(){
		return null;
	}
	
	public IBackCommit IBackCommit(){
		return null;
	}
	
	public IAny IAny(){
		return null;
	}
	
	public ICharset ICharset(){
		return null;
	}
	
	public IReturn IReturn(){
		return null;
	}
	
	public ICall ICall(){
		return null;
	}
	
	public IJump IJump(){
		return null;
	}
	
	public IEnd IEnd(){
		return null;
	}
	
	public IFail IFail(){
		return null;
	}
	
	public ICapture ICapture(){
		return null;
	}
	
	public ISpan ISpan(){
		return null;
	}
	
}
