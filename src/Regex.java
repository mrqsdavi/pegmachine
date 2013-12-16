import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import Estruturas.*;
import Instrucoes.*;


public class Regex {
	
	private RegexDelegate delegate;
	
	private ArrayList<String> captures = new ArrayList<String>();
	
	private HashMap<Instrucao, String> labelsIntrucao;
	private HashMap<String, Instrucao> instrucoesLabel;
	
	private ArrayList<String> nextLabels = new ArrayList<String>();
	private ArrayList<String> previousLabels = new ArrayList<String>();
	private ArrayList<String> gramaticasCriadasLabels = new ArrayList<String>();
	/*private ArrayList<String> labelsPosteriores = new ArrayList<String>();
	private ArrayList<String> labelsIdentificacao = new ArrayList<String>();*/
	
	private String labelUltimaGramatica = "";
	
	public boolean useHeadFailOptimization = false;
	public boolean usePartialCommitOptimization = false;
	
	public ArrayList<String> getCaptures() {
		return captures;
	}

	public void setCaptures(ArrayList<String> captures) {
		this.captures = captures;
	}
	
	public String lastCapture(){
		if(captures.size() == 0){
			return null;
		}
		
		return captures.get(captures.size() - 1);
	}

	public RegexDelegate getDelegate() {
		return delegate;
	}

	public void setDelegate(RegexDelegate delegate) {
		this.delegate = delegate;
	}

	public ArrayList<Instrucao> instrucoes(Padrao padrao){
		
		String primeiroLabel = null;
		IFail failTemp = new IFail();
		if(padrao.getTipo() != TipoPadrao.GRAMATICA){
			primeiroLabel = "L"+(labelsIntrucao.size()+1);
			previousLabels.add(primeiroLabel);
			labelsIntrucao.put(failTemp, primeiroLabel);
		}
		
		ArrayList<Instrucao> retorno = instrucoesDoPadrao(padrao);
		
		if(primeiroLabel != null && retorno.size() > 0){
			Instrucao primeiraInstrucao = retorno.get(0);
			
			labelsIntrucao.remove(failTemp);
			labelsIntrucao.put(primeiraInstrucao, primeiroLabel);
		}
		
		return retorno;
		
	}
	
	public ArrayList<Instrucao> instrucoesDoPadrao(Padrao padrao){
		
		ArrayList<Instrucao> retorno = new ArrayList<Instrucao>();
		
		if(padrao.getTipo() == TipoPadrao.GRAMATICA && !gramaticasCriadasLabels.contains(padrao.gramatica().getNome())){
			
			labelUltimaGramatica = padrao.gramatica().getNome();
			
			gramaticasCriadasLabels.add(padrao.gramatica().getNome());
			retorno.add(new ICall(padrao.gramatica().getNome()));
			
			String returnLabel = "R"+padrao.gramatica().getNome();
			IReturn iReturn = new IReturn();
			labelsIntrucao.put(iReturn, returnLabel);
			
			String jumpNextLabel = nextLabels.get(nextLabels.size() - 1);
			IJump iJump = new IJump(jumpNextLabel);
			retorno.add(iJump);
			
			nextLabels.add(returnLabel);
			previousLabels.add(padrao.gramatica().getNome());
			ArrayList<Instrucao> instrucoesDaGramatica = instrucoesDoPadrao(padrao.gramatica().getPadrao());
			if(instrucoesDaGramatica.size() > 0){
				Instrucao primeiraInstrucao = instrucoesDaGramatica.get(0);				
				if(!labelsIntrucao.containsValue(primeiraInstrucao)){
					labelsIntrucao.put(primeiraInstrucao, padrao.gramatica().getNome());
				}				
				retorno.addAll(instrucoesDaGramatica);
			}
			previousLabels.remove(padrao.gramatica().getNome());
			
			nextLabels.remove(returnLabel);
			retorno.add(iReturn);
			
			for(int i = 0; i < padrao.gramatica().getSubgramaticas().size(); i++){
				Gramatica subgramatica = padrao.gramatica().getSubgramaticas().get(i);
				retorno.addAll(instrucoesDoPadrao(subgramatica));
			}
			
		}else if(padrao.getTipo() == TipoPadrao.CAPTURA){
			
			ICapture beginCapture = new ICapture(TipoCapture.BEGIN);
			ICapture endCapture = new ICapture(TipoCapture.END);
			
			String labelPrimeiraInstrucao = "L"+(labelsIntrucao.size() + 1);
			String labelEndCapture = "L"+(labelsIntrucao.size() + 2);
			labelsIntrucao.put(beginCapture, labelPrimeiraInstrucao);
			labelsIntrucao.put(endCapture, labelEndCapture);
			previousLabels.add(labelPrimeiraInstrucao);
			nextLabels.add(labelEndCapture);
			
			ArrayList<Instrucao> instrucoes = instrucoesDoPadrao(padrao.captura().getPadrao());
			
			labelsIntrucao.remove(beginCapture);
			
			if(instrucoes.size() > 0){
				Instrucao primeiraInstrucao = instrucoes.get(0);
				labelsIntrucao.put(primeiraInstrucao, labelPrimeiraInstrucao);
			}
					
			
			retorno.add(beginCapture);
			retorno.addAll(instrucoes);
			retorno.add(endCapture);
			
			previousLabels.remove(labelPrimeiraInstrucao);
			nextLabels.remove(labelEndCapture);
			
		}else if(padrao.getTipo() == TipoPadrao.REPETICAO){
			
			String commitPreviousLabel = null;
			if(padrao.repeticao().getTipoRepeticao() == TipoRepeticao.UMA_OU_MAIS){
				retorno.addAll(instrucoesDoPadrao(padrao.repeticao().getPadrao()));
				commitPreviousLabel = "L"+(labelsIntrucao.size() + 1);
			}
			
			String choiceNextLabel = nextLabels.get(nextLabels.size() - 1);
			IChoice iChoice = new IChoice(choiceNextLabel);
		
			if(previousLabels.size() > 0 && commitPreviousLabel == null){
				commitPreviousLabel = previousLabels.get(previousLabels.size() - 1);
			}
			
			if(commitPreviousLabel == null){
				commitPreviousLabel = "L"+(labelsIntrucao.size() + 1);
			}
			
			ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.repeticao().getPadrao());
			
			retorno.add(iChoice);
			retorno.addAll(instrucoesRepeticao);
			
			labelsIntrucao.put(iChoice, commitPreviousLabel);
			
			if(usePartialCommitOptimization){
				
				String labelPartialCommit = "L"+(labelsIntrucao.size() + 1);
				
				Instrucao primeiraInstrucao = instrucoesRepeticao.get(0);
				labelsIntrucao.put(primeiraInstrucao, labelPartialCommit);
				
				IPartialCommit partialCommit = new IPartialCommit(labelPartialCommit);
				retorno.add(partialCommit);
				
			}else{
				
				ICommit iCommit = new ICommit(commitPreviousLabel);
				retorno.add(iCommit);
			}
			
		}else if(padrao.getTipo() == TipoPadrao.CONCATENACAO){

			ArrayList<String> registroLabels = new ArrayList<>();
			ArrayList<Instrucao> instrucoesTemporarias = new ArrayList<>();
			
			registroLabels.add(previousLabels.get(previousLabels.size() - 1));
			IChar tempInicial = new IChar('*');
			instrucoesTemporarias.add(tempInicial);
			
			int indexInstrucao = nextLabels.size();
			nextLabels.add(indexInstrucao, previousLabels.get(previousLabels.size() - 1));
			
			
			for(int i = 1; i < padrao.concatenacao().getPadroes().size(); i++){
				
				String label = "L"+(labelsIntrucao.size() + 1);
				
				
				IChar temp = new IChar('*');
				instrucoesTemporarias.add(temp);
				registroLabels.add(label);
				labelsIntrucao.put(temp, label);
				nextLabels.add(indexInstrucao, label);
			}
			
			for(int i = 0; i < padrao.concatenacao().getPadroes().size(); i++){
				Padrao padraoAtual = padrao.concatenacao().getPadroes().get(i);
				
				String labelPrimeiraInstrucao = registroLabels.get(i);
				
				nextLabels.remove(labelPrimeiraInstrucao);
				previousLabels.add(labelPrimeiraInstrucao);
				
				ArrayList<Instrucao> instrucoesPadraoAtual = instrucoesDoPadrao(padraoAtual);
				if(labelPrimeiraInstrucao != null && instrucoesPadraoAtual.size() > 0){
					Instrucao primeiraInstucao = instrucoesPadraoAtual.get(0);
					labelsIntrucao.remove(instrucoesTemporarias.get(i));
					labelsIntrucao.put(primeiraInstucao, labelPrimeiraInstrucao);
				}
				retorno.addAll(instrucoesPadraoAtual);
				previousLabels.remove(labelPrimeiraInstrucao);
			}
			
			/*String labelPrimeiraInstrucao = null;
			String labelPosterior = null;
			
			for(int i = 0; i < padrao.concatenacao().getPadroes().size(); i++){
				Padrao padraoAtual = padrao.concatenacao().getPadroes().get(i);
				labelPrimeiraInstrucao = labelPosterior;
				
				if(padraoAtual.getTipo() == TipoPadrao.REPETICAO && padraoAtual.repeticao().getTipoRepeticao() == TipoRepeticao.UMA_OU_MAIS){
					labelPosterior = "L"+(labelsIntrucao.size() + 2);
				}else if(padraoAtual.getTipo() == TipoPadrao.REPETICAO || padraoAtual.getTipo() == TipoPadrao.ESCOLHA_ORDENADA){
					labelPosterior = "L"+(labelsIntrucao.size() + 1);
				}
				
				if(labelPosterior != null && i < padrao.concatenacao().getPadroes().size() - 1){
					nextLabels.add(labelPosterior);
					
					if(labelPrimeiraInstrucao != null){
						previousLabels.add(labelPosterior);
					}
					
				}
				
				ArrayList<Instrucao> instrucoesPadraoAtual = instrucoesDoPadrao(padraoAtual);
				if(labelPrimeiraInstrucao != null && instrucoesPadraoAtual.size() > 0){
					Instrucao primeiraInstucao = instrucoesPadraoAtual.get(0);
					labelsIntrucao.put(primeiraInstucao, labelPrimeiraInstrucao);
				}
				retorno.addAll(instrucoesPadraoAtual);
				
				if(labelPosterior != null){
					nextLabels.remove(labelPosterior);
					previousLabels.add(labelPosterior);
				}
				
			}
			
			nextLabels.remove(labelPosterior);
			nextLabels.remove(labelPrimeiraInstrucao);*/
			
		}else if(padrao.getTipo() == TipoPadrao.ESCOLHA_ORDENADA){

			for(int i = 0; i < padrao.escolhaOrdenada().getPadroes().size() - 1; i++){
				if(previousLabels.size() > 0){
					
					Padrao padraoAtualEO = padrao.escolhaOrdenada().getPadroes().get(i);
					
					boolean usarHeadFail = useHeadFailOptimization && padraoAtualEO.getTipo() == TipoPadrao.SEQUENCIA;
					
					String labelInstrucao = previousLabels.get(previousLabels.size() - 1);
					previousLabels.remove(previousLabels.size() - 1);
					
					String labelChoice = "L"+(labelsIntrucao.size() + 1);
					IChoice choice = new IChoice(labelChoice);
					ICommit commit = new ICommit(nextLabels.get(nextLabels.size() - 1));
					
					if(usarHeadFail){
						ITestChar testChar = new ITestChar(padraoAtualEO.sequencia().getTexto().charAt(0), labelChoice);
						labelsIntrucao.put(choice, labelInstrucao);
						retorno.add(testChar);
						
						String textoSequencia = padraoAtualEO.sequencia().getTexto();
						
						padraoAtualEO = new Sequencia(textoSequencia.substring(1, textoSequencia.length()));
					}else{
						System.out.println(padraoAtualEO+" Não usou headFail "+useHeadFailOptimization);
						labelsIntrucao.put(choice, labelInstrucao);
					}
					
					retorno.add(choice);
					
					String labelCommit = "L"+(labelsIntrucao.size()+1);
					labelsIntrucao.put(commit, labelCommit);
					nextLabels.add(labelCommit);
					
					String labelPrimeiraInstrucao = "L"+(labelsIntrucao.size() + 1);
					labelsIntrucao.put(null, labelPrimeiraInstrucao);
					previousLabels.add(labelPrimeiraInstrucao);
					
					ArrayList<Instrucao> instrucoesPardraoAtual = instrucoesDoPadrao(padraoAtualEO);
					
					if(padraoAtualEO.getTipo() == TipoPadrao.REPETICAO && padraoAtualEO.repeticao().getTipoRepeticao() == TipoRepeticao.ZERO_OU_MAIS){
						//instrucoesPardraoAtual.remove(0);
						//instrucoesPardraoAtual.remove(instrucoesPardraoAtual.size() - 1);
					}
					
					nextLabels.remove(labelCommit);
					
					retorno.addAll(instrucoesPardraoAtual);
					retorno.add(commit);
					previousLabels.remove(labelInstrucao);
					previousLabels.add(labelChoice);
				}
			}
			
			String labelInstrucao = previousLabels.get(previousLabels.size() - 1);
			ArrayList<Instrucao> instrucoesUltimoPadrao = instrucoesDoPadrao(padrao.escolhaOrdenada().getPadroes().get(padrao.escolhaOrdenada().getPadroes().size() - 1));
			Instrucao primeiraInstrucaoEO = instrucoesUltimoPadrao.get(0);
			labelsIntrucao.put(primeiraInstrucaoEO, labelInstrucao);
			retorno.addAll(instrucoesUltimoPadrao);
			
			
		}else if(padrao.getTipo() == TipoPadrao.E){
			
			String commitNextLabel = nextLabels.get(nextLabels.size() - 1);		
			String choiceNextLabel = "L"+(labelsIntrucao.size() + 1); 
			
			IChoice iChoice = new IChoice(choiceNextLabel);
			IBackCommit iBackCommit = new IBackCommit(commitNextLabel);
			IFail iFail = new IFail();
			labelsIntrucao.put(iFail, choiceNextLabel);
			
			ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.e().getPadrao());
			
			
			retorno.add(iChoice);
			retorno.addAll(instrucoesRepeticao);
			retorno.add(iBackCommit);
			retorno.add(iFail);
			
			
		}else if(padrao.getTipo() == TipoPadrao.NAO){
			
			String commitNextLabel = "L"+(labelsIntrucao.size() + 1);			
			String choiceNextLabel = nextLabels.get(nextLabels.size() - 1);
			
			IChoice iChoice = new IChoice(choiceNextLabel);
			ICommit iCommit = new ICommit(commitNextLabel);
			IFail iFail = new IFail();
			labelsIntrucao.put(iFail, commitNextLabel);

			nextLabels.add(commitNextLabel);
			
			ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.nao().getPadrao());
			
			nextLabels.remove(commitNextLabel);
			
			retorno.add(iChoice);
			retorno.addAll(instrucoesRepeticao);
			retorno.add(iCommit);
			retorno.add(iFail);
			
			
			
		}else if(padrao.getTipo() == TipoPadrao.OPCIONAL){
					
			String choiceNextLabel = nextLabels.get(nextLabels.size() - 1);
			String labelProximaInstrucao = "L"+(labelsIntrucao.size() + 1);
			previousLabels.add(labelProximaInstrucao);
			
			
			IChoice iChoice = new IChoice(choiceNextLabel);
			ICommit iCommit = new ICommit(choiceNextLabel);

			
			ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.opcional().getPadrao());
			
			if(instrucoesRepeticao.size() > 0){
				Instrucao primeiraInstrucao = instrucoesRepeticao.get(0);
				labelsIntrucao.put(primeiraInstrucao, labelProximaInstrucao);
			}
			
			previousLabels.remove(labelProximaInstrucao);
			
			retorno.add(iChoice);
			retorno.addAll(instrucoesRepeticao);
			retorno.add(iCommit);		
			
			
		}else if(padrao.getTipo() == TipoPadrao.ATE){
			retorno.add(new ISpan(padrao.ate().getPadrao().conjunto().getConjuntoCaracteres()));
		}else if(padrao.getTipo() == TipoPadrao.SEQUENCIA){
			
			String texto = padrao.sequencia().getTexto();
			for(int i = 0; i < texto.length(); i++){					
				retorno.add(new IChar(texto.charAt(i)));
			}
			
		}else if(padrao.getTipo() == TipoPadrao.CADEIA_VAZIA){
			retorno.add(new IChar(true));
		}else if(padrao.getTipo() == TipoPadrao.SELF){
			retorno.add(new ICall(labelUltimaGramatica));
		}else if(padrao.getTipo() == TipoPadrao.CHAMADA){
			retorno.add(new ICall(padrao.chamada().getLabel()));
		}else if(padrao.getTipo() == TipoPadrao.PONTO){
			
			retorno.add(new IAny(padrao.ponto().getNumero()));
			
		}else if(padrao.getTipo() == TipoPadrao.CONJUNTO){
			//Ainda deve ser destrinchado para intervalos tipo [a-zA-z]
			retorno.add(new ICharset(padrao.conjunto().getTexto(), padrao.conjunto().getConjuntoCaracteres()));
			
		}
		
		return retorno;		
	}
	
	public void imprimirInstrucoes(ArrayList<Instrucao> instrucoes, HashMap<Instrucao, String> labelsIntrucao){
		
		for(int i = 0; i < instrucoes.size(); i++){
			Instrucao instrucao = instrucoes.get(i);
			
			if(labelsIntrucao.containsKey(instrucao)){
				String labelInstrucao = labelsIntrucao.get(instrucao);
				System.out.print(labelInstrucao+":");
			}
			
			switch (instrucao.getTipoInstrucao()) {
			case CHAR:
				System.out.println("\tChar \'"+ instrucao.IChar().getCaracter() +"\'");
				break;
				
			case ANY:
				System.out.println("\tAny "+ instrucao.IAny().getN());
				break;
				
			case CHOICE:
				System.out.println("\tChoice "+instrucao.IChoice().getLabel());
				break;
				
			case COMMIT:
				System.out.println("\tCommit "+instrucao.ICommit().getLabel());
				break;
				
			case BACKCOMMIT:
				System.out.println("\tBackCommit "+instrucao.IBackCommit().getLabel());
				break;
				
			case PARTIALCOMMIT:
				System.out.println("\tPartialCommit "+instrucao.IPartialCommit().getLabel());
				break;
				
			case CHARSET:
				System.out.println("\tCharset ["+instrucao.ICharset().getTexto()+"]");
				break;
				
			case CALL:
				System.out.println("\tCall "+instrucao.ICall().getLabel());
				break;
				
			case JUMP:
				System.out.println("\tJump "+instrucao.IJump().getLabel());
				break;
				
			case FAIL:
				System.out.println("\tFail");
				break;
				
			case FAILTWICE:
				System.out.println("\tFailTwice");
				break;
			
			case RETURN:
				System.out.println("\tReturn");
				break;
				
			case END:
				System.out.println("\tEnd");
				break;
				
			case SPAN:
				System.out.println("\tSpan "+instrucao.ISpan().getSet());
				break;
				
			case CAPTURE:
				if(instrucao.ICapture().getTipo() == TipoCapture.BEGIN){
					System.out.println("\tCapture begin");
				}else{
					System.out.println("\tCapture end");
				}
				break;
				
			case TESTCHAR:
				System.out.println("\tTestChar "+instrucao.ITestChar().getCaracter()+" "+instrucao.ITestChar().getLabel());
				break;
				
			case TESTCHARSET:
				System.out.println("\tTestCharset "+instrucao.ITestCharset().getSet()+" "+instrucao.ITestChar().getLabel());
				break;
			
			case TESTANY:
				System.out.println("\tTestAny "+instrucao.ITestAny().getLabel());
				break;

			default:
				break;
			}
			
		}
		
	}
	
	public Integer rodarInstrucoes(ArrayList<Instrucao> instrucoes, String texto, HashMap<String, Instrucao> instrucoesLabel){
		
		EstadoMaquina estadoMaquina = new EstadoMaquina();
		estadoMaquina.setEstadoCommit(EstadoMaquina.EstadoCommit.COMMIT);
		int posicaoNoTexto = 0;
		//int tamanhoTextoCasadoCommitado = 0;
		int tamanhoTextoCasado = 0;
		
		ArrayList<EstadoMaquina> pilhaRetorno = new ArrayList<EstadoMaquina>();
		ArrayList<String> desvioFalha = new ArrayList<String>();
		ArrayList<Integer> desvioChamada = new ArrayList<Integer>();
		ArrayList<Integer> pilhaCaptures = new ArrayList<Integer>();
		
		if(instrucoes == null || instrucoes.size() == 0){
			return 0;
		}
		
		int i = 0;
		Instrucao instrucaoAtual = instrucoes.get(0);
		while(instrucaoAtual !=null && instrucaoAtual.getTipoInstrucao() != TipoInstrucao.END){
			
			boolean falhou = false;
			
			if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.CHAR){
				
				if(posicaoNoTexto >= texto.length()){
					falhou = true;
				}else if(!instrucaoAtual.IChar().isVazio()){
					if(texto.charAt(posicaoNoTexto) == instrucaoAtual.IChar().getCaracter()){
						tamanhoTextoCasado++;
						posicaoNoTexto++;
						System.out.println(posicaoNoTexto+" Casou "+instrucaoAtual.IChar().getCaracter());
					}else{
						System.out.println("Falhou ao comparar: "+texto.charAt(posicaoNoTexto)+"\tcom:"+instrucaoAtual.IChar().getCaracter());
						falhou = true;
					}
				}
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.CHARSET){
				
				if(posicaoNoTexto >= texto.length()){
					falhou = true;
				}else if(instrucaoAtual.ICharset().isCharecterIn(texto.charAt(posicaoNoTexto))){
					System.out.println("Caractere no conjunto "+texto.charAt(posicaoNoTexto));
					tamanhoTextoCasado++;
					posicaoNoTexto++;
				}else{
					System.out.println("Falhou ao comparar "+texto.charAt(posicaoNoTexto)+" no conjunto: "+texto.charAt(posicaoNoTexto)+" "+instrucaoAtual.ICharset().getSet());
					falhou = true;
				}
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.ANY){
				
				if(posicaoNoTexto >= texto.length()){
					falhou = true;
				}else{
					tamanhoTextoCasado++;
					posicaoNoTexto++;
				}
				
				//System.out.println("Casou qualquer "+posicaoNoTexto);
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.CHOICE){
				
				//System.out.println("CHOICE "+estadoMaquina.getTamanhoTextoCasado());
				
				pilhaRetorno.add(estadoMaquina);
				int posicaoInstrucao = i;
				EstadoMaquina newEstadoMaquina = new EstadoMaquina();
				newEstadoMaquina.setEstadoCommit(EstadoMaquina.EstadoCommit.NO_COMMIT);
				newEstadoMaquina.setTamanhoTextoCasado(estadoMaquina.getTamanhoTextoCasado());
				newEstadoMaquina.setPosicaoInstrucao(posicaoInstrucao);
				newEstadoMaquina.setPosicaoTexto(posicaoNoTexto);
				estadoMaquina = newEstadoMaquina;
				desvioFalha.add(instrucaoAtual.IChoice().getLabel());
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.COMMIT){
				
				//System.out.println("COMMIT "+estadoMaquina.getTamanhoTextoCasado());

				EstadoMaquina ultimoEstadoMaquina = pilhaRetorno.get(pilhaRetorno.size() - 1);
				pilhaRetorno.remove(pilhaRetorno.size() - 1);
				desvioFalha.remove(desvioFalha.size() - 1);
				estadoMaquina.setEstadoCommit(ultimoEstadoMaquina.getEstadoCommit());
				estadoMaquina.setTamanhoTextoCasado(tamanhoTextoCasado);
				estadoMaquina.setPosicaoTexto(posicaoNoTexto);
				//pilhaRetorno.add(estadoMaquina);
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				instrucaoAtual =  instrucoesLabel.get(instrucaoAtual.ICommit().getLabel());
				
				i = instrucoes.indexOf(instrucaoAtual);
				continue;
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.BACKCOMMIT){
				
				
				EstadoMaquina ultimoEstadoMaquina = pilhaRetorno.get(pilhaRetorno.size() - 1);
				pilhaRetorno.remove(pilhaRetorno.size() - 1);
				desvioFalha.remove(desvioFalha.size() - 1);
				estadoMaquina.setEstadoCommit(ultimoEstadoMaquina.getEstadoCommit());
				estadoMaquina.setTamanhoTextoCasado(ultimoEstadoMaquina.getTamanhoTextoCasado());
				estadoMaquina.setPosicaoTexto(ultimoEstadoMaquina.getPosicaoTexto());
				
				tamanhoTextoCasado = ultimoEstadoMaquina.getTamanhoTextoCasado();
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				instrucaoAtual =  instrucoesLabel.get(instrucaoAtual.IBackCommit().getLabel());
				
				i = instrucoes.indexOf(instrucaoAtual);
				continue;
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.PARTIALCOMMIT){

				pilhaRetorno.get(pilhaRetorno.size() - 1).setTamanhoTextoCasado(tamanhoTextoCasado);
				pilhaRetorno.get(pilhaRetorno.size() - 1).setPosicaoTexto(posicaoNoTexto);
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				instrucaoAtual =  instrucoesLabel.get(instrucaoAtual.IPartialCommit().getLabel());
				
				i = instrucoes.indexOf(instrucaoAtual);
				continue;
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.CALL){
				desvioChamada.add(i+1);
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				instrucaoAtual =  instrucoesLabel.get(instrucaoAtual.ICall().getLabel());
				i = instrucoes.indexOf(instrucaoAtual);
				continue;
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.SPAN){
				
				//posicaoNoTexto++;
				
				for(; posicaoNoTexto < texto.length(); posicaoNoTexto++, tamanhoTextoCasado++){
					char c = texto.charAt(posicaoNoTexto);
					//System.out.println("Analizou "+c);
					if(instrucaoAtual.ISpan().contem(c)) break;
				}
				
				/*if(posicaoNoTexto >= texto.length()){
					break;
				}*/
				
				i++;
				instrucaoAtual = instrucoes.get(i);
				
				estadoMaquina.setTamanhoTextoCasado(tamanhoTextoCasado);
				estadoMaquina.setPosicaoTexto(posicaoNoTexto);
				estadoMaquina.setPosicaoInstrucao(i);
				continue;
				
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.JUMP){
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				instrucaoAtual =  instrucoesLabel.get(instrucaoAtual.IJump().getLabel());
				i = instrucoes.indexOf(instrucaoAtual);
				continue;
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.FAIL){
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				falhou = true;
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.RETURN){
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
				i = desvioChamada.get(desvioChamada.size() - 1);
				instrucaoAtual = instrucoes.get(i);
				
				desvioChamada.remove(desvioChamada.size() - 1);
				
				estadoMaquina.setPosicaoInstrucao(i);
				estadoMaquina.setPosicaoTexto(posicaoNoTexto);
				estadoMaquina.setTamanhoTextoCasado(tamanhoTextoCasado);
				continue;
			}else if(instrucaoAtual.getTipoInstrucao() == TipoInstrucao.CAPTURE){
				
				if(instrucaoAtual.ICapture().getTipo() == TipoCapture.BEGIN){
					pilhaCaptures.add(posicaoNoTexto);
				}else{
					int posicaoInicialCapture = pilhaCaptures.get(pilhaCaptures.size() - 1);
					pilhaCaptures.remove(pilhaCaptures.size() - 1);
					
					if(posicaoNoTexto <= posicaoInicialCapture){
						captures.add("");
					}else{
						captures.add(texto.substring(posicaoInicialCapture, posicaoNoTexto));
					}
				}
				
				if(delegate != null){
					delegate.rodouInstrucao(instrucaoAtual);
				}
				
			}
			
			if(falhou){
				
				if(pilhaRetorno.size() > 0){
					estadoMaquina = pilhaRetorno.get(pilhaRetorno.size() - 1);
					pilhaRetorno.remove(pilhaRetorno.size() - 1);
					
					tamanhoTextoCasado = estadoMaquina.getTamanhoTextoCasado();
					posicaoNoTexto = estadoMaquina.getPosicaoTexto();
					
					if(desvioFalha.size() > 0){
						String labelDesvio = desvioFalha.get(desvioFalha.size() - 1);
						desvioFalha.remove(desvioFalha.size() - 1);
						instrucaoAtual = instrucoesLabel.get(labelDesvio);
						i = instrucoes.indexOf(instrucaoAtual);
						
						
						//System.out.println(instrucaoAtual.toString()+" Tamanho "+estadoMaquina.getTamanhoTextoCasado());
						continue;
					}
				}else{
					tamanhoTextoCasado = -1;
					estadoMaquina.setTamanhoTextoCasado(tamanhoTextoCasado);
					break;
				}
			
				
			}
			
			if(estadoMaquina.getEstadoCommit() == EstadoMaquina.EstadoCommit.COMMIT){
				estadoMaquina.setPosicaoTexto(posicaoNoTexto);
				estadoMaquina.setTamanhoTextoCasado(tamanhoTextoCasado);
			}
			
			i++;
			if(i < instrucoes.size()){
				instrucaoAtual = instrucoes.get(i);
			}
			
		}
		
		if(instrucaoAtual != null && (instrucaoAtual.getTipoInstrucao() == TipoInstrucao.END || (posicaoNoTexto == texto.length() && estadoMaquina.getEstadoCommit() == EstadoMaquina.EstadoCommit.COMMIT))){
			estadoMaquina.setPosicaoTexto(posicaoNoTexto);
			estadoMaquina.setTamanhoTextoCasado(tamanhoTextoCasado);
		}
		
		/*if(posicaoNoTexto == texto.length() && i < instrucoes.size() - 1){
			estadoMaquina.setTamanhoTextoCasado(-1);
			System.out.println("Corre��ao");
		}*/

		
		//System.out.println("Texto Casado: "+ (estadoMaquina.getTamanhoTextoCasado()+1));
		
		if(estadoMaquina.getTamanhoTextoCasado() == -1 || desvioChamada.size() > 0){
			//System.out.println(desvioChamada.size()+" Retornou aqui "+estadoMaquina.getTamanhoTextoCasado());
			return null;
		}
		
		return estadoMaquina.getPosicaoTexto()+1;
	}

	public Integer match(String padraoString, String texto){

		
		InputStream is = new ByteArrayInputStream(padraoString.getBytes());
		AnalizadorLexico lexico = new AnalizadorLexico(is);
		parser parser = new parser(lexico);
		try {
			parser.parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Padrao Final: "+parser.padraoFinal.toString());
		
		return match(parser.padraoFinal, texto);
	}
	
	public Integer match(Padrao padrao, String texto){
		
		labelsIntrucao = new HashMap<Instrucao, String>();
		instrucoesLabel = new HashMap<String, Instrucao>();
		nextLabels = new ArrayList<String>();
		previousLabels = new ArrayList<String>();
		gramaticasCriadasLabels = new ArrayList<String>();
		
		String labelEnd = "E";
		IEnd end = new IEnd();
		labelsIntrucao.put(end, labelEnd);
		nextLabels.add(labelEnd);
		ArrayList<Instrucao> instrucoes = instrucoes(padrao);
		instrucoes.add(end);
		
		for(int i = 0; i < labelsIntrucao.keySet().size(); i++){
			Instrucao instrucao = (Instrucao) labelsIntrucao.keySet().toArray()[i];
			String label = labelsIntrucao.get(instrucao);
			instrucoesLabel.put(label, instrucao);
		}
		
		imprimirInstrucoes(instrucoes, labelsIntrucao);
		return rodarInstrucoes(instrucoes, texto, instrucoesLabel);
	}
	
	public static void main(String[] args) {
		
		Regex regex = new Regex();
		regex.useHeadFailOptimization = true;
		//System.out.println("Texto Casado " + regex.match("S <- 'davi'D'bola'*\nD <- 'teste'*?", "davitestebola"));
		
		//HeadFail example
		//System.out.println("Texto Casado " + regex.match("S <- 'ana' / .S", "tetstetgbshsghsghsghanajkjdkjskskjs"));
		
		//PartialCommit example
		//regex.usePartialCommitOptimization = true;
		System.out.println("Texto Casado " + regex.match("Soma <- Produto (('+' / '-') Produto)*\n"
				+ "Produto <- Valor (('*' / '/') Valor)*\n"
				+ "Valor <- [0-9]+ / '(' Expr ')'", "1+2*2"));
	}
	
}
