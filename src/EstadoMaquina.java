
public class EstadoMaquina {
	
	public enum EstadoCommit{
		NO_COMMIT,
		COMMIT
	}

	private int posicaoInstrucao;
	private int posicaoTexto;
	private int tamanhoTextoCasado;
	private EstadoCommit estadoCommit;
	
	public int getPosicaoInstrucao() {
		return posicaoInstrucao;
	}
	public void setPosicaoInstrucao(int posicaoInstrucao) {
		this.posicaoInstrucao = posicaoInstrucao;
	}
	public EstadoCommit getEstadoCommit() {
		return estadoCommit;
	}
	public void setEstadoCommit(EstadoCommit estadoCommit) {
		this.estadoCommit = estadoCommit;
	}
	public int getTamanhoTextoCasado() {
		return tamanhoTextoCasado;
	}
	public void setTamanhoTextoCasado(int tamanhoTextoCasado) {
		this.tamanhoTextoCasado = tamanhoTextoCasado;
	}
	public int getPosicaoTexto() {
		return posicaoTexto;
	}
	public void setPosicaoTexto(int posicaoTexto) {
		this.posicaoTexto = posicaoTexto;
	}
	
}
