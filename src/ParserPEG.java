import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import Estruturas.*;
import Instrucoes.*;


public class ParserPEG extends RegexDelegate{
	
	Regex regex;
	
	Set<Character> maiusculas = new HashSet<Character>(Arrays.asList('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'));
	Set<Character> minusculas = new HashSet<Character>(Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'));
	Set<Character> numeros = new HashSet<Character>(Arrays.asList('0','1','2','3','4','5','6','7','8','9'));
	Set<Character> todosOsCaracteres = new HashSet<Character>();
	
	IChar inicioSequencia = new IChar('\'');
	ICapture inicioCapturaSequencia = new ICapture(TipoCapture.BEGIN);
	IChoice inicioChoiceSequencia = new IChoice("FinalSequencia");
	ICharset todosCaracteresSequencia = new ICharset("A-Za-z0-9", todosOsCaracteres);
	ICommit fimCommitSequencia = new ICommit("InicioChoiceSequencia");
	ICapture fimCapturaSequencia = new ICapture(TipoCapture.END);
	IChar finalSequencia = new IChar('\'');
	
	Padrao padrao;
	
	public static void main(String[] args){
		ParserPEG parser = new ParserPEG();
		parser.intrucoesDoParser();
	}

	public ArrayList<Instrucao> intrucoesDoParser(){
		
		todosOsCaracteres.addAll(maiusculas);
		todosOsCaracteres.addAll(minusculas);
		todosOsCaracteres.addAll(numeros);
		
		regex = new Regex();
		regex.setDelegate(this);
		
		String padrao = "'a'";
		
		ArrayList<Instrucao> instrucoes = new ArrayList<Instrucao>(Arrays.asList(
				inicioSequencia,
				inicioCapturaSequencia,
				inicioChoiceSequencia,
				todosCaracteresSequencia,
				fimCommitSequencia,
				fimCapturaSequencia,
				finalSequencia));
		
		HashMap<String, Instrucao> instrucoesLabel = new HashMap<String, Instrucao>();
		instrucoesLabel.put("FinalSequencia", fimCapturaSequencia);
		instrucoesLabel.put("InicioChoiceSequencia", inicioChoiceSequencia);
		
		regex.rodarInstrucoes(instrucoes, padrao, instrucoesLabel);
		
		return null;
	}
	
	public void rodouInstrucao(Instrucao i){
		
		@SuppressWarnings("unused")
		Padrao padraoEncontrado = null;
		String capture = regex.lastCapture();
		
		if(i == finalSequencia){
			padraoEncontrado = new Sequencia(capture);
			System.out.println("SEQUENCIA: "+ capture);
		}
	}
	
}
