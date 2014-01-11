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
		
		/*System.out.println("Busca com otimização");
	    benchmark("@the", 0);
	    benchmark("Omega", 0);
	    benchmark("Alpha", 0);
	    benchmark("amethysts", 0);
	    benchmark("heith", 0);
	    benchmark("eartt", 0);

	    System.out.println("\nBusca sem otimização");
	    benchmark("'@the'", 1);*/
	    benchmark("'Omega'", 1);
	    benchmark("'Alpha'", 1);
	    benchmark("'amethysts'", 1);
	    benchmark("'heith'", 1);
	    benchmark("'eartt'", 1);
	    benchmark("[A-Za-z ]*", 1);
	    benchmark("([a-zA-Z]+'Abram')",1);
	    benchmark("([a-zA-Z]+'Joseph')",1);
	    
	    System.out.println("\nEffectiveness of optimizations");
	    benchmark("S <- 'transparent' / . S", 2);
	    benchmark("(!'transparent' .)* 'transparent'", 2);
	    benchmark("S <- [a-zA-Z]+ ' '* 'transparent' / . S", 2);
	    benchmark("(!([a-zA-Z]+ ' '* 'transparent') .)*", 2);
		
	}
	
	public static void benchmark(String p, int tipo){
		long startTime= System.currentTimeMillis();
	    int posicao = -1; 
	    
	    if(tipo == 0){
	    	posicao = searchW(p, bibleText);
	    }else if(tipo == 1){
	    	posicao = searchP(p, bibleText);
	    }else{
	    	posicao = searchG(p, bibleText);
	    }
	    
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
                
		return position;
	}
	
	public static Integer searchG(String gramatica, String texto){
		Regex r = new Regex();
                
        Integer position = r.match(gramatica, texto);
                
        if(position == null){
        	return 0;
        }
                
		return position;
	}

	public static Integer searchW(String padraoTexto, String texto){
		
		String x = padraoTexto.substring(0,1);
		String p = "S <- '"+padraoTexto+"'/.~'"+x+"'S";
		Regex r = new Regex();
                
        Integer position = r.match(p, texto);
                
        if(position == null){
        	return 0;
        }
                
		return position;
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
