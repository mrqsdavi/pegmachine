import java.util.ArrayList;


public class EstadoMaquina {

	private int p;
	private int i;
	private int tamanhoPilha;
	private EstadoMaquina pilhaEstados[];
	private ArrayList<String> capturas;
	
	public EstadoMaquina(){
		p = -1;
		i = -1;
		tamanhoPilha=0;
		pilhaEstados = null;
		capturas = new ArrayList<>();
	}
	
	public void inicializar(){
		pilhaEstados = new EstadoMaquina[100000000];
	}
	public int getP() {
		return p;
	}
	public void setP(int p) {
		this.p = p;
	}
	public int getI() {
		return i;
	}
	public void setI(int i) {
		this.i = i;
	}
	public ArrayList<String> getCapturas() {
		return capturas;
	}
	public void setCapturas(ArrayList<String> capturas) {
		this.capturas = capturas;
	}
	public void incI(){
		i++;
	}
	public void incP(){
		p++;
	}
	public void addEstado(EstadoMaquina estado){
		pilhaEstados[tamanhoPilha]=estado;
		tamanhoPilha++;
	}
	public EstadoMaquina popEstado(){
		
		if(tamanhoPilha>0){
			EstadoMaquina estado = pilhaEstados[tamanhoPilha-1];
			pilhaEstados[tamanhoPilha-1]=null;
			tamanhoPilha--;
			return estado;
		}
		return null;
	}
	
}
