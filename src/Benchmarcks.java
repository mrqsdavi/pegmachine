import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import Estruturas.Concatenacao;
import Estruturas.EscolhaOrdenada;
import Estruturas.Gramatica;
import Estruturas.Padrao;
import Estruturas.Ponto;
import Estruturas.Self;
import Estruturas.Sequencia;


public class Benchmarcks {

	public static String bibleText = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		bibleText = null;
		
		try {
			bibleText = readFile("/Volumes/Arquivos/Dropbox/TCC/PEG/src/bible.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    benchmark("@the");
	    benchmark("Omega");
	    benchmark("Alpha");
	    benchmark("amethysts");
	    benchmark("heith");
	    benchmark("eartt");
		
	}
	
	public static void benchmark(String p){
		long startTime= System.currentTimeMillis();
	    int posicao = search(p, bibleText);
	    long endTime = System.currentTimeMillis();
	    
	    System.out.println(p+": " + posicao+" - "+(endTime-startTime)+"ms");
	}

	public static Integer search(String padraoTexto, String texto){
		Regex r = new Regex();
		Gramatica g = new Gramatica("S");
		EscolhaOrdenada eo = new EscolhaOrdenada(new Sequencia(padraoTexto));
		Concatenacao c = new Concatenacao(new Ponto(1));
		c.addPadrao(new Self());
		eo.addPadrao(c);
		g.setPadrao(eo);
                
                Integer position = r.match(g, texto);
                
                if(position == null){
                    return 0;
                }
                
		return position - padraoTexto.length();
	}
	
	public static String readFile(String pathname) throws IOException {

	    File file = new File(pathname);
	    StringBuilder fileContents = new StringBuilder((int)file.length());
	    Scanner scanner = new Scanner(file);
	    String lineSeparator = System.getProperty("line.separator");

	    try {
	        while(scanner.hasNextLine()) {        
	            fileContents.append(scanner.nextLine() + lineSeparator);
	        }
	        return fileContents.toString();
	    } finally {
	        scanner.close();
	    }
	}
}
