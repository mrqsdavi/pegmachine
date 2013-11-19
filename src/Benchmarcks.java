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

		String path = System.getProperty("user.dir") + "/src/bible.txt";
		bibleText = null;
		
		try {
			bibleText = readFile(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    benchmark("'@the'");
	    benchmark("'Omega'");
	    benchmark("'Alpha'");
	    benchmark("'amethysts'");
	    benchmark("'heith'");
	    benchmark("'eartt'");
	    //benchmark("[A-Za-z ]*");
		
	}
	
	public static void benchmark(String p){
		long startTime= System.currentTimeMillis();
	    int posicao = searchP(p, bibleText);
	    long endTime = System.currentTimeMillis();
	    
	    System.out.println(p+": " + posicao+" - "+(endTime-startTime)+"ms");
	}
	
	public static Integer searchP(String padraoTexto, String texto){
		
		String p = "S <- "+padraoTexto+" / .S";
		
		Regex r = new Regex();
        
        Integer position = r.match(p, texto);
                
        if(position == null){
        	return 0;
        }
                
		return position - padraoTexto.length();
	}

	public static Integer searchW(String padraoTexto, String texto){
		
		String x = padraoTexto.substring(0,1);
		String p = "S <- '"+padraoTexto+"'/.~'"+x+"'S";
		Regex r = new Regex();
                
        Integer position = r.match(p, texto);
                
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
