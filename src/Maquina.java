import java.util.ArrayList;

import Instrucoes.Instrucao;

public class Maquina {

	private String entrada;
	private ArrayList<Instrucao> instrucoes;
	EstadoMaquina estado;
	
	public Maquina(String entrada, ArrayList<Instrucao> instrucoes){
		this.setEntrada(entrada);
		this.setInstrucoes(instrucoes);
		estado = new EstadoMaquina();
		estado.inicializar();
		estado.setI(0);
		estado.setP(0);
	}

	public String getEntrada() {
		return entrada;
	}

	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}

	public ArrayList<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public void setInstrucoes(ArrayList<Instrucao> instrucoes) {
		this.instrucoes = instrucoes;
	}
	
	public void run(){
		
		while(estado.getP() < instrucoes.size()){

			Instrucao instrucao = instrucoes.get(estado.getP());
			
			boolean falhou = false;
			
			switch (instrucao.getTipoInstrucao()) {
			case CHAR:
				if(!instrucao.IChar().isVazio()){
					if(estado.getI() >= entrada.length()){
						//System.out.println("Texto Acabou");
						falhou = true;
					}else if(entrada.charAt(estado.getI()) == instrucao.IChar().getCaracter()){
						estado.incI();
						//System.out.println(estado.getI()+" Casou "+instrucao.IChar().getCaracter());
					}else{
						//System.out.println(estado.getI()+" Falhou ao comparar: "+entrada.charAt(estado.getI())+"\tcom:"+instrucao.IChar().getCaracter());
						falhou = true;
					}
				}
				estado.incP();
				break;
				
			case CHARSET:
				//System.out.println(estado.getI()+" ENTRADA: "+entrada);
				if(estado.getI() >= entrada.length()){
					falhou = true;
				}else if(instrucao.ICharset().isCharecterIn(entrada.charAt(estado.getI()))){
					//System.out.println("Caractere no conjunto "+entrada.charAt(estado.getI()));
					estado.incI();
				}else{
					//System.out.println("Falhou ao comparar "+entrada.charAt(estado.getI())+" no conjunto: "+entrada.charAt(estado.getI())+" "+instrucao.ICharset().getSet());
					falhou = true;
				}
				estado.incP();
				break;
				
			case ANY:
				if(estado.getI() >= entrada.length()){
					falhou = true;
				}else{
					estado.incI();
				}
				estado.incP();
				break;
				
			case CHOICE:{
				EstadoMaquina novoEstado = new EstadoMaquina();
				novoEstado.setP(instrucao.getIndexDesvio());
				novoEstado.setCapturas(estado.getCapturas());
				novoEstado.setI(estado.getI());
				estado.addEstado(novoEstado);
				estado.incP();
			}
				break;
				
			case COMMIT:
				estado.popEstado();
				estado.setP(instrucao.getIndexDesvio());
				break;
				
			case BACKCOMMIT:{
                System.out.println("BACKCOMMIT: "+estado.getI());
				EstadoMaquina estadoAntigo = estado.popEstado();
				estado.setCapturas(estadoAntigo.getCapturas());
				estado.setI(estadoAntigo.getI());
				estado.setP(instrucao.getIndexDesvio());
                System.out.println(""+estado.getI());
			}
				break;
				
			case PARTIALCOMMIT:{
				EstadoMaquina estadoAntigo = estado.popEstado();
				estado.setP(instrucao.getIndexDesvio());
				EstadoMaquina novoEstado = new EstadoMaquina();
				novoEstado.setI(estado.getI());
				novoEstado.setCapturas(estado.getCapturas());
				novoEstado.setP(estadoAntigo.getP());
				estado.addEstado(novoEstado);
			}
				break;
				
			case CALL:{
				EstadoMaquina novoEstado = new EstadoMaquina();
				novoEstado.setP(estado.getP()+1);
				estado.addEstado(novoEstado);
				estado.setP(instrucao.getIndexDesvio());
			}
				break;
				
			case SPAN:
				for(; estado.getI() < entrada.length(); estado.incI()){
					char c = entrada.charAt(estado.getI());
					if(instrucao.ISpan().contem(c)) break;
				}
				estado.incP();
				break;
				
			case JUMP:
				estado.setP(instrucao.getIndexDesvio());
				break;
				
			case FAIL:
				falhou = true;
				break;
				
			case RETURN:{
				EstadoMaquina estadoAntigo = estado.popEstado();
				estado.setP(estadoAntigo.getP());
				//System.out.println("Return "+estadoAntigo.getP());
			}
				break;
				
			case END:
				estado.incP();
				break;

			case CAPTURE:
				//REFAZER
				break;
				
			default:
				//estado.incP();
				break;
			}
			
			if(falhou){
				EstadoMaquina estadoAntigo = estado.popEstado();
				if(estadoAntigo!=null){
					estado.setP(estadoAntigo.getP());
					
					if(estadoAntigo.getI()>=0){
						estado.setI(estadoAntigo.getI());
					}
					
				}else{
					break;
				}
				
			}
			
		}
		
		System.out.println("CASADO :"+estado.getI());
	}
	
}
