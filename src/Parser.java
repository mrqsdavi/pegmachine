import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Estruturas.*;


public class Parser {

	
	private static ArrayList<Character> letrasMaiusculas = new ArrayList<Character>(  
			  Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')  
			  );
	private static ArrayList<Character> letrasMinusculas = new ArrayList<Character>(  
			  Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')  
			  );
	private static ArrayList<Character> numeros = new ArrayList<Character>(  
			  Arrays.asList('0', '1','2', '3', '4','5', '7', '8','9')  
			  );
	
	public Padrao parse(String text){
		
		return matchText(text);
	}

	private Padrao matchText(String text) {
		// TODO Auto-generated method stub
		

		ArrayList<String> lista =  new ArrayList<String>(Arrays.asList(text.split("<-")));		
		HashMap<String, String> gramaticasComPadrao = new HashMap<String, String>();
		
		if(lista.size() == 1){
			return padraoDoTexto(null, lista.get(0), gramaticasComPadrao);
		}
		
		String nomeNaoTerminalInicial = null;
		System.out.println("TESTE "+lista);
		
		for(int i = lista.size() - 1; i > 0; i = i - 1){
			
			String padraoTexto = lista.get(i).trim();
			String gramaticaSemCorrecao = lista.get(i-1).trim();
			
			int k = gramaticaSemCorrecao.length() - 1;
			while((letrasMinusculas.contains(gramaticaSemCorrecao.charAt(k))/* || letrasMinusculas.contains(gramaticaSemCorrecao.charAt(k)) */|| 
					numeros.contains(gramaticaSemCorrecao.charAt(k))) && k >= 0){
				k--;
			}
			
			String gramaticaComCorrecao = gramaticaSemCorrecao.substring(k, gramaticaSemCorrecao.length()).trim();
			System.out.println(gramaticaComCorrecao + " - "+padraoTexto);
			gramaticasComPadrao.put(gramaticaComCorrecao.trim(), padraoTexto.trim());
			
			nomeNaoTerminalInicial = gramaticaComCorrecao.trim();
			
			lista.remove(i);
			if(i - 1 > 0){
				String novoPadraoAnterior = gramaticaSemCorrecao.substring(0, k);
				lista.set(i-1, novoPadraoAnterior);
			}
			
		}
		
		Gramatica gramatica = padraoDaGramatica(nomeNaoTerminalInicial, gramaticasComPadrao.get(nomeNaoTerminalInicial), gramaticasComPadrao);
		
		return gramatica;
	}
	
	private Gramatica padraoDaGramatica(String nomeGramatica, String textoPadrao, HashMap<String, String> gramaticasComPadrao){
		Gramatica gramatica = new Gramatica(nomeGramatica);
		
		gramatica.setPadrao(padraoDoTexto(nomeGramatica, textoPadrao, gramaticasComPadrao));
		
		return gramatica;
	}
	
	private Padrao padraoDoTexto(String nomeGramatica, String textoPadrao, HashMap<String, String> gramaticasComPadrao){
		
		String texto = textoPadrao;
		int posicaoTexto = 0;
		Padrao padraoRetorno = null;
		Padrao ultimoSubpadrao = null;
		
		System.out.println("TEXTO DO PADRAO - "+texto);
		
		while(posicaoTexto < texto.length()){
			
			boolean padraoNot = false;
			Padrao subpadrao = null;
			
			char caractereAtual;
			caractereAtual = texto.charAt(posicaoTexto);
			
			if(caractereAtual == '!'){
				padraoNot = true;
				
				//Remove do texto os padroes ja identificados
				if(posicaoTexto < texto.length()){
					texto = texto.substring(1, texto.length()).trim();
					posicaoTexto = 0;
				}
				
			}
			
			caractereAtual = texto.charAt(posicaoTexto);
			
			if(letrasMaiusculas.contains(caractereAtual)){
				
				System.out.println("Encontou - "+caractereAtual);
				posicaoTexto++;
				
				while (posicaoTexto < texto.length() && (letrasMinusculas.contains(caractereAtual) ||
						numeros.contains(caractereAtual))){
					posicaoTexto++;
					
					if(posicaoTexto < texto.length()){
						caractereAtual = texto.charAt(posicaoTexto);
					}
				}
				
			}
			
			/*if(posicaoTexto >= texto.length()){
				posicaoTexto = texto.length() - 1;
			}*/
			
			caractereAtual = texto.charAt(posicaoTexto);
			
			if(posicaoTexto > 0){
				
				String nomeGramaticaChamada = texto.substring(0, posicaoTexto).trim();
				
				if(nomeGramaticaChamada.equals(nomeGramatica)){
					subpadrao = new Self();
				}else{
					String textoPadraoGramatica = gramaticasComPadrao.get(nomeGramaticaChamada);
					System.out.println("Encontrou gramatica: "+nomeGramaticaChamada+" - "+textoPadraoGramatica+gramaticasComPadrao);
					subpadrao = padraoDaGramatica(nomeGramaticaChamada, textoPadraoGramatica, gramaticasComPadrao);
				}
				
				//Remove do texto os padroes ja identificados
				if(posicaoTexto < texto.length()){
					System.out.println("Texto antigo "+ texto);
					texto = texto.substring(posicaoTexto, texto.length()).trim();
					posicaoTexto = 0;
					System.out.println("Texto melhorado "+ texto);
				}
				
				
			}else{
				
				if(caractereAtual == '.'){
					
					int numeroPontos = 1;
					posicaoTexto++;
					while(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) == '.'){
						posicaoTexto++;
						numeroPontos++;
					}
					
					Ponto ponto = new Ponto(numeroPontos);
					subpadrao = ponto;
					
				}
				
				//Sequencia comeca e termina com aspas simples ''
				if(caractereAtual == '\''){
					
					posicaoTexto++;
					while(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) != '\''){
						posicaoTexto++;
					}
					
					
					if(posicaoTexto == 1){
						
						System.out.println("CADEIA VAZIA "+ texto);
						
						CadeiaVazia cadeiaVazia = new CadeiaVazia();
						subpadrao = cadeiaVazia;
					}else{
						
						System.out.println("SEQUENCIA "+texto.substring(1, posicaoTexto));
						
						Sequencia sequencia = new Sequencia(texto.substring(1, posicaoTexto));
						subpadrao = sequencia;
					}
					
				}
				
				//Captura comeca e termina com chaves {}
				if(caractereAtual == '{'){
					
					posicaoTexto++;
					while(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) != '}'){
						posicaoTexto++;
					}
					
					Captura captura = new Captura(padraoDoTexto(nomeGramatica, texto.substring(1, posicaoTexto), gramaticasComPadrao));
					subpadrao = captura;
					
					System.out.println("Encontrou captura");
					
				}
				
				//Conjunto comeca e termina com colchetes []
				if(caractereAtual == '['){
					
					posicaoTexto++;
					while(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) != ']'){
						posicaoTexto++;
					}
					
					Conjunto conjunto = new Conjunto(texto.substring(1, posicaoTexto));
					subpadrao = conjunto;
					
				}
				
				//Agrupamento de padrao comeca e termina com parenteses()
				if(caractereAtual == '('){
					
					posicaoTexto++;
					while(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) != ')'){
						posicaoTexto++;
					}
					
					Padrao agrupamento = padraoDoTexto(nomeGramatica, texto.substring(1, posicaoTexto), gramaticasComPadrao);
					subpadrao = agrupamento;
					
				}
				
				
			}
			
			posicaoTexto++;
			
			//Repeticao ZERO ou MAIS
			if(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) == '*'){
				subpadrao = new Repeticao(subpadrao, TipoRepeticao.ZERO_OU_MAIS);
				posicaoTexto++;
			}
			
			//Repeticao UMA ou MAIS
			if(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) == '+'){
				subpadrao = new Repeticao(subpadrao, TipoRepeticao.UMA_OU_MAIS);
				posicaoTexto++;
			}
			
			//Deve ter NOT(subpadrao)
			if(padraoNot){
				subpadrao = new Nao(subpadrao);
			}
			
			//Remove do texto os padroes ja identificados
			if(posicaoTexto < texto.length()){
				texto = texto.substring(posicaoTexto, texto.length()).trim();
				posicaoTexto = 0;
			}
			
			if(subpadrao == null){
				System.out.println("NULL "+texto);
			}
			
			if(ultimoSubpadrao == null){
				ultimoSubpadrao = subpadrao;				
			}else{
				
				if(ultimoSubpadrao.getTipo() == TipoPadrao.ESCOLHA_ORDENADA){
					ultimoSubpadrao.escolhaOrdenada().addPadrao(subpadrao);
				}else if(ultimoSubpadrao.getTipo() == TipoPadrao.CONCATENACAO){
					System.out.println("CONCATENACAO "+ultimoSubpadrao.toString()+" MAIS "+subpadrao);
					ultimoSubpadrao.concatenacao().addPadrao(subpadrao);
				}else{
					ultimoSubpadrao = new Concatenacao(ultimoSubpadrao);
					ultimoSubpadrao.concatenacao().addPadrao(subpadrao);
				}
				
			}
			
			//Encontra escolha ordenada
			if(posicaoTexto < texto.length() && texto.charAt(posicaoTexto) == '/'){
				if(padraoRetorno == null){
					padraoRetorno = new EscolhaOrdenada(ultimoSubpadrao);
				}else{
					padraoRetorno.escolhaOrdenada().addPadrao(ultimoSubpadrao);
				}
				ultimoSubpadrao = null;
			}
			
			//Remove do texto os padroes ja identificados
			if(posicaoTexto < texto.length()){
				texto = texto.substring(posicaoTexto, texto.length()).trim();
				posicaoTexto = 0;
			}
			
		}
		
		if(padraoRetorno == null){
			padraoRetorno = ultimoSubpadrao;
		}else{
			padraoRetorno.escolhaOrdenada().addPadrao(ultimoSubpadrao);
		}
		
		System.out.println("padrao retorno "+padraoRetorno.toString());
		
		return padraoRetorno;		
	}

}
