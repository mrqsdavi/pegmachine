import java.io.IOException;
import java_cup.runtime.*; 

%%
%class AnalizadorLexico
%line
%column
%cup

APAR = "("
FPAR = ")"
ACOL = "["
FCOL = "]"
ACHA = "{"
FCHA = "}"
ASTERISCO = "*"
MAIS = "+"
ASPAS = "'"
BARRA = "/"
QUALQUER = "."
NOT = "!"
AND = "&"
OPCIONAL = "?"
ATRIBUICAO = "<-"
ATE = "~"

ESPACO = [ \t\r]*
LINHA = [\n]
LETRA = [a-zA-Z]
DIGITO = [0-9]
HEXADECIMAL = 0x[0-9a-fA-F]+

ID = ({LETRA} | "_")({LETRA}|{DIGITO}|"_")*
NUMINT = {DIGITO}+ | {HEXADECIMAL}
NUMFLOAT = {NUMINT}*"."{NUMINT}+ | {NUMINT}+"."{NUMINT}*
STRING = \"([^\\\"]|\\.)*\"
STRINGNAOFECHADA = \"([^\\\"]|\\.)*
COMENTARIO = "/*" ~"*/"
COMENTARIONAOFECHADO = "/*" [^*]
PRINT = "print"

SEQUENCIA = {ASPAS} ~{ASPAS}
CONJUNTO = {ACOL} ~{FCOL}

%{

    private Symbol symbol(int type) {
        return new Symbol(type, getNumeroDaLinha(), getNumeroColuna());
    }

    private Symbol symbol(int type, Object value) {
        return new Symbol(type, getNumeroDaLinha(), getNumeroColuna(), value);
    }

    public boolean imprimirEspaco = false;
    public void imprimaComEspacoEntreTokens(String texto){if(imprimirEspaco){System.out.print(" ");} System.out.print(texto); imprimirEspaco = true;}
    public int getNumeroDaLinha(){return yyline + 1;}
    public int getNumeroColuna(){return yycolumn + 1;}
    public String getStringConfigurada(){return yytext().replaceAll("\\\\\"","\"").replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");}

%}

%%
{LINHA} {imprimirEspaco = false; return symbol(sym.LINHA, yytext());}
{ESPACO} {imprimirEspaco = false;}

{SEQUENCIA} {return symbol(sym.SEQUENCIA, yytext());}
{CONJUNTO} {return symbol(sym.CONJUNTO, yytext());}
{APAR} {return symbol(sym.APAR, yytext());}
{FPAR} {return symbol(sym.FPAR, yytext());}
{ACHA} {return symbol(sym.ACHA, yytext());}
{FCHA} {return symbol(sym.FCHA, yytext());}
{ASTERISCO} {return symbol(sym.ASTERISCO, yytext());}
{MAIS} {return symbol(sym.MAIS, yytext());}
{BARRA} {return symbol(sym.BARRA, yytext());}
{QUALQUER} {return symbol(sym.QUALQUER, yytext());}
{NOT} {return symbol(sym.NOT, yytext());}
{AND} {return symbol(sym.AND, yytext());}
{OPCIONAL} {return symbol(sym.OPCIONAL, yytext());}
{ATRIBUICAO} {return symbol(sym.ATRIBUICAO, yytext());}
{ATE} {return symbol(sym.ATE, yytext());}
{ID} {return symbol(sym.ID, yytext());}

. {System.out.println("Erro lexico na linha " + getNumeroDaLinha() + ", coluna " + getNumeroColuna() +": caractere \"" + yytext() + "\" nao reconhecido"); System.exit(0);}


