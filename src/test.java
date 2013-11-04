import java.util.ArrayList;

import Estruturas.Conjunto;
import Estruturas.EscolhaOrdenada;
import Estruturas.Padrao;
import static java.util.Arrays.asList;


public class test {
	
	public static void main(String[] args) {
		Regex regex = new Regex();
		
		//tests for some basic optimizations
		assert(regex.match("!. / 'a'", "a") == 2);
		assert(regex.match("'' / 'a'", "a") == 1);
		assert(regex.match("'a' / !.", "b") == null);
		assert(regex.match("'a' / ''", "b") == 1);
		
		assert(regex.match("(!.)'a'", "a") == null); // VER POR QUE NÃO FUNCIONA regex.match("!.'a'", "a");
		assert(regex.match("'''a'", "a") == 2);
		assert(regex.match("'a'!.", "a") == null);
		assert(regex.match("'a'''", "a") == 2);
		
		//Sem consumo do texto OBS: Está igual ao exemplo anterior
		assert(regex.match("(!.)'a'", "a") == null); // VER POR QUE NÃO FUNCIONA regex.match("!.'a'", "a");
		assert(regex.match("'''a'", "a") == 2);
		assert(regex.match("'a'!.", "a") == null);
		assert(regex.match("'a'''", "a") == 2);
		
		
		// tests for locale
		
		
		//Como fazer esses?
		/*assert(m.match(3, "aaaa"))
		assert(m.match(4, "aaaa"))
		assert(not m.match(5, "aaaa"))
		assert(m.match(-3, "aa"))
		assert(not m.match(-3, "aaa"))
		assert(not m.match(-3, "aaaa"))
		assert(not m.match(-4, "aaaa"))
		assert(m.P(-5):match"aaaa")*/
		
		assert(regex.match("'a'", "alo") == 2);
		assert(regex.match("'al'", "alo") == 3);
		assert(regex.match("'alu'", "alo") == null);
		assert(regex.match("''","") == 1);
		
		Conjunto digit = new Conjunto("0123456789");
		Conjunto upper = new Conjunto("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		Conjunto lower = new Conjunto("abcdefghijklmnopqrstuvwxyz");
		Padrao letter = new EscolhaOrdenada(new ArrayList<Padrao>(asList(upper, lower)));
		Padrao alpha = new EscolhaOrdenada(new ArrayList<Padrao>(asList(letter, digit)));

		
		//grammar with a long call chain before left recursion
		/*regex.match("A <- BCDA"
				+ "B <- C"
				+ "C <- D"
				+ "D <- E"
				+ "E <- F"
				+ "F <- G"
				+ "G <- ''", "a");*/
	}

}
