import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import Estruturas.*;
import Instrucoes.*;


public class Regex {
	
	private Instrucao end;
	
	private HashMap<Instrucao, String> labelsIntrucao;
	private HashMap<String, Instrucao> instrucoesLabel;
	
	private String labelUltimaGramatica = "";
	
	private Instrucao next = null;
	private Instrucao previous = null;
	private HashMap<String, Instrucao> instrucaoGramatica;
	
	public boolean useHeadFailOptimization = false;
	public boolean usePartialCommitOptimization = false;

	public ArrayList<Instrucao> instrucoes(Padrao padrao){

		instrucaoGramatica = new HashMap<>();
		end = new IEnd();
		next = end;
		ArrayList<Instrucao> retorno = instrucoesDoPadrao(padrao);
		retorno.add(end);

		for(int i = 0; i < retorno.size(); i++){
			Instrucao instrucaoAtual = retorno.get(i);
			if(instrucaoAtual.getInstrucaoDesvio()!=null){
				instrucaoAtual.setIndexDesvio(retorno.indexOf(instrucaoAtual.getInstrucaoDesvio()));
			}
		}
		
		return retorno;
		
	}
	
	public ArrayList<Instrucao> instrucoesDoPadrao(Padrao padrao){
		
		ArrayList<Instrucao> retorno = new ArrayList<Instrucao>();
		
		Instrucao backupNext = next;
		Instrucao backupPrevious = previous;
		
		switch (padrao.getTipo()) {
		case GRAMATICA:{
			
				ICall callGramatica = new ICall(padrao.gramatica().getNome());
				IJump jumpGramatica = new IJump("");
				IReturn returnGramatica = new IReturn();
				
				Instrucao previousTemporaria = new ICall("PT");
				
				jumpGramatica.setInstrucaoDesvio(end);
				next = returnGramatica;
				previous = previousTemporaria;
				
				instrucaoGramatica.put(padrao.gramatica().getNome(), previous);
				
				ArrayList<Instrucao> instrucoes = instrucoesDoPadrao(padrao.gramatica().getPadrao());
				
				previous = instrucoes.get(0);
				callGramatica.setInstrucaoDesvio(previous);
				
				for(int i = 0; i < instrucoes.size(); i++){
					if(instrucoes.get(i).getInstrucaoDesvio() == previousTemporaria){
						instrucoes.get(i).setInstrucaoDesvio(previous);
					}
				}
				
				retorno.add(callGramatica);
				retorno.add(jumpGramatica);
				retorno.addAll(instrucoes);
				retorno.add(returnGramatica);
				
				for(int i = 0; i < padrao.gramatica().getSubgramaticas().size(); i++){
					Gramatica subgramatica = padrao.gramatica().getSubgramaticas().get(i);
					ArrayList<Instrucao> instrucoesSubgramatica = instrucoesDoPadrao(subgramatica);
					instrucoesSubgramatica.remove(0);
					instrucoesSubgramatica.remove(0);
					retorno.addAll(instrucoesSubgramatica);
				}
			
		}
		break;
			
		case CAPTURA:{
			ICapture beginCapture = new ICapture(TipoCapture.BEGIN);
			ICapture endCapture = new ICapture(TipoCapture.END);
			
			previous = beginCapture;
			next = endCapture;
			
			ArrayList<Instrucao> instrucoes = instrucoesDoPadrao(padrao.captura().getPadrao());
			
			retorno.add(beginCapture);
			retorno.addAll(instrucoes);
			retorno.add(endCapture);
		}
		break;
		
		case REPETICAO:{
			
			if(padrao.repeticao().getTipoRepeticao() == TipoRepeticao.UMA_OU_MAIS){
				Instrucao previousTemporaria = new ICall("");
				previous = previousTemporaria;
				
				ArrayList<Instrucao> instrucoes = instrucoesDoPadrao(padrao.repeticao().getPadrao());
				previous = instrucoes.get(0);
				for(int i = 0; i < instrucoes.size(); i++){
					if(instrucoes.get(i).getInstrucaoDesvio() == previousTemporaria){
						instrucoes.get(i).setInstrucaoDesvio(previous);
					}
				}
				
				retorno.addAll(instrucoes);
			}
			
			IChoice choiceRepeticao = new IChoice("");
			ICommit commitRepeticao = new ICommit("");
			
			choiceRepeticao.setInstrucaoDesvio(next);
			commitRepeticao.setInstrucaoDesvio(choiceRepeticao);
			
			previous = choiceRepeticao;
			
			ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.repeticao().getPadrao());
			
			retorno.add(choiceRepeticao);
			retorno.addAll(instrucoesRepeticao);
			
			if(usePartialCommitOptimization){
				for(int i = 0; i < instrucoesRepeticao.size(); i++){
					if(instrucoesRepeticao.get(i).getInstrucaoDesvio() == previous){
						instrucoesRepeticao.get(i).setInstrucaoDesvio(instrucoesRepeticao.get(0));
					}
				}
				
				IPartialCommit partialCommit = new IPartialCommit("");
				partialCommit.setInstrucaoDesvio(next);
				retorno.add(partialCommit);
				
			}else{
				retorno.add(commitRepeticao);
			}
		}
		break;
		
		case CONCATENACAO:{
			
			ArrayList<Instrucao> instrucoesTemporarias = new ArrayList<>();
			ArrayList<Instrucao> instrucoesReais = new ArrayList<>();
			
			for(int i = 0; i<padrao.concatenacao().getPadroes().size();i++){
				instrucoesTemporarias.add(new ICall("L"+i));
			}
			
			for(int i = 0; i<padrao.concatenacao().getPadroes().size();i++){
				
				Padrao padraoAtual = padrao.concatenacao().getPadroes().get(i);
				Instrucao instrucaoPrevious = null;
				Instrucao instrucaoNext = null;
				
				if(i==0){
					instrucaoPrevious = backupPrevious;
				}else{
					instrucaoPrevious = instrucoesTemporarias.get(i-1);
				}
				
				if(i==padrao.concatenacao().getPadroes().size()-1){
					instrucaoNext = backupNext;
				}else{
					instrucaoNext = instrucoesTemporarias.get(i+1);
				}
				
				previous = instrucaoPrevious;
				next = instrucaoNext;

				ArrayList<Instrucao> instrucoesPadraoAtual = instrucoesDoPadrao(padraoAtual);
				instrucoesReais.add(instrucoesPadraoAtual.get(0));
				retorno.addAll(instrucoesPadraoAtual);
				
			}
			
			for(int i = 0; i < retorno.size(); i++){
				Instrucao instrucaoAtual = retorno.get(i);
				if(instrucaoAtual.getInstrucaoDesvio()!=null && instrucoesTemporarias.contains(instrucaoAtual.getInstrucaoDesvio())){
					int index = instrucoesTemporarias.indexOf(instrucaoAtual.getInstrucaoDesvio());
					retorno.get(i).setInstrucaoDesvio(instrucoesReais.get(index));
				}
			}
		}
			break;

		case ESCOLHA_ORDENADA:{
			ArrayList<Instrucao> instrucoesTemporarias = new ArrayList<>();
			ArrayList<Instrucao> instrucoesReais = new ArrayList<>();
			
			for(int i = 0; i<padrao.escolhaOrdenada().getPadroes().size();i++){
				instrucoesTemporarias.add(new ICall("L"+i));
			}
			
			for(int i = 0; i<padrao.escolhaOrdenada().getPadroes().size();i++){
				
				Padrao padraoAtual = padrao.escolhaOrdenada().getPadroes().get(i);
				Instrucao instrucaoNext = null;
				
				if(i==padrao.escolhaOrdenada().getPadroes().size()-1){
					instrucaoNext = backupNext;
				}else{
					instrucaoNext = instrucoesTemporarias.get(i+1);
				}
				
				next = instrucaoNext;
				
				ArrayList<Instrucao> instrucoesPadraoAtual = instrucoesDoPadrao(padraoAtual);
				retorno.addAll(instrucoesPadraoAtual);
				
				//FAZER AQUI O HEAD FAIL
				
				if(i!=padrao.escolhaOrdenada().getPadroes().size()-1){
					IChoice choiceEscolha = new IChoice("");
					ICommit commitEscolha = new ICommit("");
					
					choiceEscolha.setInstrucaoDesvio(instrucaoNext);
					commitEscolha.setInstrucaoDesvio(backupNext);
					
					instrucoesReais.add(choiceEscolha);
					
					retorno.add(0, choiceEscolha);
					retorno.add(commitEscolha);
				}else{
					instrucoesReais.add(instrucoesPadraoAtual.get(0));
				}
				
			}
			
			for(int i = 0; i < retorno.size(); i++){
				Instrucao instrucaoAtual = retorno.get(i);
				if(instrucaoAtual.getInstrucaoDesvio()!=null && instrucoesTemporarias.contains(instrucaoAtual.getInstrucaoDesvio())){
					int index = instrucoesTemporarias.indexOf(instrucaoAtual.getInstrucaoDesvio());
					retorno.get(i).setInstrucaoDesvio(instrucoesReais.get(index));
				}
			}
		}
			break;
			
		case E:{

            IChoice iChoice = new IChoice("");
            IBackCommit iBackCommit = new IBackCommit("");
            IFail iFail = new IFail();

            iChoice.setInstrucaoDesvio(iFail);
            iBackCommit.setInstrucaoDesvio(next);

            ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.e().getPadrao());

            retorno.add(iChoice);
            retorno.addAll(instrucoesRepeticao);
            retorno.add(iBackCommit);
            retorno.add(iFail);
        }
			break;
		
		case NAO:{

            IChoice iChoice = new IChoice("");
            ICommit iCommit = new ICommit("");
            IFail iFail = new IFail();

            iChoice.setInstrucaoDesvio(next);
            iCommit.setInstrucaoDesvio(iFail);

            next = iFail;

            ArrayList<Instrucao> instrucoesRepeticao = instrucoesDoPadrao(padrao.nao().getPadrao());

            retorno.add(iChoice);
            retorno.addAll(instrucoesRepeticao);
            retorno.add(iCommit);
            retorno.add(iFail);
        }
			break;
			
		case OPCIONAL:{

        }
			break;
			
		case ATE:
			retorno.add(new ISpan(padrao.ate().getPadrao().conjunto().getConjuntoCaracteres()));
			break;
		
		case CADEIA_VAZIA:
			retorno.add(new IChar(true));
			break;
			
		case SELF:
			retorno.add(new ICall(labelUltimaGramatica));
			break;
		
		case CHAMADA:{
			ICall call = new ICall("");
			call.setInstrucaoDesvio(instrucaoGramatica.get(padrao.chamada().getLabel()));
			retorno.add(call);
		}
			break;
			
		case SEQUENCIA:
			String texto = padrao.sequencia().getTexto();
			for(int i = 0; i < texto.length(); i++){					
				retorno.add(new IChar(texto.charAt(i)));
			}
			break;
			
		case PONTO:
			retorno.add(new IAny(padrao.ponto().getNumero()));
			break;
			
		case CONJUNTO:
			retorno.add(new ICharset(padrao.conjunto().getTexto(), padrao.conjunto().getConjuntoCaracteres()));
			break;
		
		default:
			break;
		}
		
		next = backupNext;
		previous = backupPrevious;
		
		/*if(padrao.getTipo() == TipoPadrao.OPCIONAL){
					
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
			
		}*/
		
		return retorno;		
	}
	
	public void imprimirInstrucoes(ArrayList<Instrucao> instrucoes, HashMap<Instrucao, String> labelsIntrucao){
		
		for(int i = 0; i < instrucoes.size(); i++){
			Instrucao instrucao = instrucoes.get(i);


			System.out.print(i+":");
			
			String labelDesvio = null;			
			if(instrucao.getInstrucaoDesvio()!=null){
				labelDesvio = ""+instrucoes.indexOf(instrucao.getInstrucaoDesvio());
			}
			
			switch (instrucao.getTipoInstrucao()) {
			case CHAR:
				System.out.println("\tChar \'"+ instrucao.IChar().getCaracter() +"\'");
				break;
				
			case ANY:
				System.out.println("\tAny "+ instrucao.IAny().getN());
				break;
				
			case CHOICE:
				System.out.println("\tChoice "+labelDesvio);
				break;
				
			case COMMIT:
				System.out.println("\tCommit "+labelDesvio);
				break;
				
			case BACKCOMMIT:
				System.out.println("\tBackCommit "+labelDesvio);
				break;
				
			case PARTIALCOMMIT:
				System.out.println("\tPartialCommit "+labelDesvio);
				break;
				
			case CHARSET:
				System.out.println("\tCharset ["+instrucao.ICharset().getTexto()+"]");
				break;
				
			case CALL:
				System.out.println("\tCall "+labelDesvio);
				break;
				
			case JUMP:
				System.out.println("\tJump "+labelDesvio);
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
				System.out.println("\tTestChar "+instrucao.ITestChar().getCaracter()+" "+labelDesvio);
				break;
				
			case TESTCHARSET:
				System.out.println("\tTestCharset "+instrucao.ITestCharset().getSet()+" "+labelDesvio);
				break;
			
			case TESTANY:
				System.out.println("\tTestAny "+labelDesvio);
				break;

			default:
				break;
			}
			
		}
		
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
		
		ArrayList<Instrucao> instrucoes = instrucoes(padrao);
		
		for(int i = 0; i < labelsIntrucao.keySet().size(); i++){
			Instrucao instrucao = (Instrucao) labelsIntrucao.keySet().toArray()[i];
			String label = labelsIntrucao.get(instrucao);
			instrucoesLabel.put(label, instrucao);
		}
		
		imprimirInstrucoes(instrucoes, labelsIntrucao);
		Maquina maquina = new Maquina(texto, instrucoes);
		maquina.run();
		return 0;
		//return rodarInstrucoes(instrucoes, texto, instrucoesLabel);
	}
	
	public static void main(String[] args) {
		
		Regex regex = new Regex();
		regex.useHeadFailOptimization = true;
		//System.out.println("Texto Casado " + regex.match("S <- 'davi'D'bola'*\nD <- 'teste'*?", "davitestebola"));
		
		//HeadFail example
		//System.out.println("Texto Casado " + regex.match("S <- 'ana' / .S", "tetstetgbshsghsghsghanajkjdkjskskjs"));
		
		//PartialCommit example
		//regex.usePartialCommitOptimization = true;
		/*System.out.println("Texto Casado " + regex.match("Expr <- Soma\n"
				+"Valor <- [0-9]+ / '(' Expr ')'\n"
				+"Produto <- Valor (('*' / '/') Valor)*\n"
				+"Soma <- Produto (('+' / '-') Produto)*", "1+2*2"));*/
		System.out.println("Texto Casado " + regex.match("A <- 'teste'!'ana'", "testeana"));
	}
	
}
