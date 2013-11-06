import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Estruturas.*;
import Instrucoes.*;


public class ParserPEG extends RegexDelegate{
	
	Regex regex;
	
	public static Set<Character> maiusculas = new HashSet<Character>(Arrays.asList('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'));
	public static Set<Character> minusculas = new HashSet<Character>(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));
	public static Set<Character> numeros = new HashSet<Character>(Arrays.asList('0','1','2','3','4','5','6','7','8','9'));
	public static Set<Character> todosOsCaracteres = new HashSet<Character>();
	
	//Instruções da Gramática do Padrao
	ICall chamadaGramatica = new ICall("P");
	IJump jumpFim = new IJump("End");
	IReturn returnGramatica = new IReturn();
	IEnd end = new IEnd();
	ICall chamadaGramaticaPadraoSimples = new ICall("PS");
	
	//Instruções de Qualquer
	ICapture inicioCapturaQualquer = new ICapture(TipoCapture.BEGIN);
	IChar any = new IChar('.');
	ICapture fimCapturaQualquer = new ICapture(TipoCapture.END);
	
	//Instrucões de Sequência
	IChar inicioSequencia = new IChar('\'');
	ICapture inicioCapturaSequencia = new ICapture(TipoCapture.BEGIN);
	IChoice inicioChoiceSequencia = new IChoice("FimSequencia");
	ICharset todosCaracteresSequencia = new ICharset("ALL", ParserPEG.caracteresDaSequencia());
	ICommit fimCommitSequencia = new ICommit("InicioChoiceSequencia");
	ICapture fimCapturaSequencia = new ICapture(TipoCapture.END);
	IChar fimSequencia = new IChar('\'');
	
	//Instrucões de Conjunto
	IChar inicioConjunto = new IChar('[');
	ICapture inicioCapturaConjunto = new ICapture(TipoCapture.BEGIN);
	IChoice inicioChoiceConjunto = new IChoice("FimConjunto");
	ICharset todosCaracteresConjunto = new ICharset("ALL", ParserPEG.caracteresDoConjunto());
	ICommit fimCommitConjunto = new ICommit("InicioChoiceConjunto");
	ICapture fimCapturaConjunto = new ICapture(TipoCapture.END);
	IChar fimConjunto = new IChar(']');
	
	Padrao padrao;
	
	public static void main(String[] args){
		ParserPEG parser = new ParserPEG();
		parser.intrucoesDoParser();
	}
	
	ParserPEG(){
		for(int i = 32; i < 127; i++){
			todosOsCaracteres.add(new Character((char)i));
		}
	}

	public ArrayList<Instrucao> intrucoesDoParser(){
		
		regex = new Regex();
		regex.setDelegate(this);
		
		String padrao = "[WZS]";
		
		IChoice choiceEscolhaQualquer = new IChoice("EscolhaSequencia");
		ICommit commitEscolhaQualquer = new ICommit("Return");
		IChoice choiceEscolhaSequencia = new IChoice("EscolhaConjunto");
		ICommit commitEscolhaSequencia = new ICommit("Return");
		IChoice choiceEscolhaConjunto = new IChoice("Return");
		ICommit commitEscolhaConjunto = new ICommit("Return");
		
		
		ArrayList<Instrucao> instrucoes = new ArrayList<Instrucao>(Arrays.asList(
				chamadaGramatica,
				jumpFim,
				
				choiceEscolhaQualquer,
				inicioCapturaQualquer,
				any,
				fimCapturaQualquer,
				commitEscolhaQualquer,
				
				choiceEscolhaSequencia,
				inicioSequencia,
				inicioCapturaSequencia,
				inicioChoiceSequencia,
				todosCaracteresSequencia,
				fimCommitSequencia,
				fimCapturaSequencia,
				fimSequencia,
				commitEscolhaSequencia,
				
				choiceEscolhaConjunto,
				inicioConjunto,
				inicioCapturaConjunto,
				inicioChoiceConjunto,
				todosCaracteresConjunto,
				fimCommitConjunto,
				fimCapturaConjunto,
				fimConjunto,
				commitEscolhaConjunto,
				
				returnGramatica,
				end
				));
		
		HashMap<String, Instrucao> instrucoesLabel = new HashMap<String, Instrucao>();
		
		instrucoesLabel.put("P", choiceEscolhaQualquer);
		instrucoesLabel.put("PS", choiceEscolhaQualquer);
		instrucoesLabel.put("EscolhaSequencia", choiceEscolhaSequencia);
		instrucoesLabel.put("EscolhaConjunto", choiceEscolhaConjunto);
		instrucoesLabel.put("Return", returnGramatica);
		instrucoesLabel.put("End", end);
		
		instrucoesLabel.put("FimSequencia", fimCapturaSequencia);
		instrucoesLabel.put("FimConjunto", fimCapturaConjunto);
		instrucoesLabel.put("InicioChoiceSequencia", inicioChoiceSequencia);
		instrucoesLabel.put("InicioChoiceConjunto", inicioChoiceConjunto);
		
		HashMap<Instrucao, String> labelsIntrucao = new HashMap<Instrucao, String>();
		
		for(int i = 0; i < instrucoesLabel.keySet().size(); i++){
			String key = (String) instrucoesLabel.keySet().toArray()[i];
			Instrucao instrucao = instrucoesLabel.get(key);
			labelsIntrucao.put(instrucao, key);
		}
		
		regex.imprimirInstrucoes(instrucoes, labelsIntrucao);
		regex.rodarInstrucoes(instrucoes, padrao, instrucoesLabel);
		
		return null;
	}
	
	public void rodouInstrucao(Instrucao i){
		
		@SuppressWarnings("all")
		Padrao padraoEncontrado = null;
		String capture = regex.lastCapture();
		
		//System.out.println(capture);
		
		if(i == fimCapturaQualquer){
			padraoEncontrado = new Ponto(1);
			System.out.println("QUALQUER: "+ capture);
		}if(i == fimSequencia){
			
			if(capture.length() > 0){
				padraoEncontrado = new Sequencia(capture);
				System.out.println("SEQUENCIA: "+ capture);
			}else{
				padraoEncontrado = new CadeiaVazia();
				System.out.println("CADEIA VAZIA");
			}
		}if(i == fimConjunto){
			padraoEncontrado = new Conjunto(capture);
			System.out.println("CONJUNTO: "+ padraoEncontrado.conjunto().getConjuntoCaracteres());
		}
	}
	
	private static Set<Character> caracteresDaSequencia(){
		Set<Character> result = new HashSet<>();
		
		for(int i = 32; i < 127; i++){
			result.add(new Character((char)i));
		}
		
		result.remove('\'');
		return result;
	}
	
	private static Set<Character> caracteresDoConjunto(){
		Set<Character> result = new HashSet<>();
		
		for(int i = 32; i < 127; i++){
			result.add(new Character((char)i));
		}
		
		result.remove('[');
		result.remove(']');
		return result;
	}
	
}
