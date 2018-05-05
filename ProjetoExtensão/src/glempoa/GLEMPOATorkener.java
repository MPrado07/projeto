package glempoa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/*
Copyright (c) 2002 JSON.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/**

* Um GLEMPOATokener pega uma string de origem e extrai caracteres e tokens de
 isto. Ele é usado pelos construtores GLEMPOAObject e JSONArray para analisar
 Strings de origem JSON.
 * @author mathe
 *
 */
public class GLEMPOATorkener {

    private long    character;
    private boolean eof;
    private long    index;
    private long    line;
    private char    previous;
    private Reader  reader;
    private boolean usePrevious;


    /**
     * Construct a JSONTokener from a Reader.
     *
     * @param reader     A reader.
     */
    public GLEMPOATorkener(Reader reader) {
        this.reader = reader.markSupported()
            ? reader
            : new BufferedReader(reader);
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0;
        this.character = 1;
        this.line = 1;
    }


    /**
      Construa um JSONTokener de um InputStream.
     * @param inputStream A fonte.
     */
    public GLEMPOATorkener(InputStream inputStream) throws GLEMPOAException {
        this(new InputStreamReader(inputStream));
    }


    /**
     * Construct a JSONTokener from a string.
     *
     * @param s     A source string.
     */
    public GLEMPOATorkener(String s) {
        this(new StringReader(s));
    }


    /**
     
* Faça o backup de um caractere. Isso fornece uma espécie de capacidade de lookahead,
     * para que você possa testar um dígito ou letra antes de tentar analisar
     * o próximo número ou identificador.
     */
    public void back() throws GLEMPOAException {
        if (this.usePrevious || this.index <= 0) {
            throw new GLEMPOAException("Stepping back two steps is not supported");
        }
        this.index -= 1;
        this.character -= 1;
        this.usePrevious = true;
        this.eof = false;
    }


    /**
    * Obtenha o valor hexadecimal de um caractere (base16).
     * @param c Um caracter entre '0' e '9' ou entre 'A' e 'F' ou
     * entre 'a' e 'f'.
     * @return Um int entre 0 e 15, ou -1 se c não era um dígito hexadecimal.
     */
    public static int dehexchar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'A' && c <= 'F') {
            return c - ('A' - 10);
        }
        if (c >= 'a' && c <= 'f') {
            return c - ('a' - 10);
        }
        return -1;
    }

    public boolean end() {
        return this.eof && !this.usePrevious;
    }


    /**
     
* Determine se a string de origem ainda contém caracteres que next ()
     * pode consumir.
     * @retorne a verdade se ainda não estiver no final da fonte.
     */
    public boolean more() throws GLEMPOAException {
        this.next();
        if (this.end()) {
            return false;
        }
        this.back();
        return true;
    }


    /**
    
* Obtenha o próximo caractere na string de origem.
     *
     * @return O próximo caractere ou 0 se passado no final da string de origem.
     */
    public char next() throws GLEMPOAException {
        int c;
        if (this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
                throw new GLEMPOAException(exception);
            }

            if (c <= 0) { // End of stream
                this.eof = true;
                c = 0;
            }
        }
        this.index += 1;
        if (this.previous == '\r') {
            this.line += 1;
            this.character = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            this.line += 1;
            this.character = 0;
        } else {
            this.character += 1;
        }
        this.previous = (char) c;
        return this.previous;
    }


    /**
     
* Consuma o próximo caractere e verifique se ele corresponde a um
     * personagem.
     * @param c O caractere a ser correspondido.
     * @return O personagem.
     * @throws GLEMPOAException se o caractere não corresponder.
     */
    public char next(char c) throws GLEMPOAException {
        char n = this.next();
        if (n != c) {
            throw this.syntaxError("Expected '" + c + "' and instead saw '" +
                    n + "'");
        }
        return n;
    }


    /**
    
* Obtenha os próximos n caracteres.
     *
     * @param n O número de caracteres a serem capturados.
     * @return Uma string de n caracteres.
     * @throws GLEMPOAException
     * Substring bounds error se não houver
     * n caracteres restantes na string de origem.
     */
     public String next(int n) throws GLEMPOAException {
         if (n == 0) {
             return "";
         }

         char[] chars = new char[n];
         int pos = 0;

         while (pos < n) {
             chars[pos] = this.next();
             if (this.end()) {
                 throw this.syntaxError("Substring bounds error");
             }
             pos += 1;
         }
         return new String(chars);
     }


    /**
     * Pegue o próximo caractere na string, ignorando o espaço em branco.
     * @throws GLEMPOAException
     * @return Um caractere, ou 0 se não houver mais caracteres.
     */
    public char nextClean() throws GLEMPOAException {
        for (;;) {
            char c = this.next();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }


    /**
    
* Retorne os caracteres até o próximo caractere de aspas próximas.
     * O processamento de barra invertida está concluído. O formato JSON formal não
     * Permitir strings entre aspas simples, mas uma implementação é permitida
     * aceite-os.
     * @param quote O caractere de citação, seja
     * <code> "</ code> & nbsp; <small> (aspas duplas) </ small> ou
     * <code> '</ code> & nbsp; <small> (aspas simples) </ small>.
     * @return A String.
     * @throws GLEMPOAException Cadeia não terminada.
     */
    public String nextString(char quote) throws GLEMPOAException {
        char c;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            c = this.next();
            switch (c) {
            case 0:
            case '\n':
            case '\r':
                throw this.syntaxError("Unterminated string");
            case '\\':
                c = this.next();
                switch (c) {
                case 'b':
                    sb.append('\b');
                    break;
                case 't':
                    sb.append('\t');
                    break;
                case 'n':
                    sb.append('\n');
                    break;
                case 'f':
                    sb.append('\f');
                    break;
                case 'r':
                    sb.append('\r');
                    break;
                case 'u':
                    sb.append((char)Integer.parseInt(this.next(4), 16));
                    break;
                case '"':
                case '\'':
                case '\\':
                case '/':
                    sb.append(c);
                    break;
                default:
                    throw this.syntaxError("Illegal escape.");
                }
                break;
            default:
                if (c == quote) {
                    return sb.toString();
                }
                sb.append(c);
            }
        }
    }


    /**
    
* Obter o texto, mas não incluindo o caractere especificado ou o
     * fim da linha, o que ocorrer primeiro.
     * @param delimiter Um caractere delimitador.
     * @return Uma string.
     */
    public String nextTo(char delimiter) throws GLEMPOAException {
        StringBuilder sb = new StringBuilder();
        for (;;) {
            char c = this.next();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
    * Obtenha o texto, mas não incluindo um dos delimitadores especificados
     * caracteres ou o fim da linha, o que ocorrer primeiro.
     * @ delimitadores param Um conjunto de caracteres delimitadores.
     * @return Uma string, aparada.
     */
    public String nextTo(String delimiters) throws GLEMPOAException {
        char c;
        StringBuilder sb = new StringBuilder();
        for (;;) {
            c = this.next();
            if (delimiters.indexOf(c) >= 0 || c == 0 ||
                    c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }


    /**
     * Obtenha o próximo valor. O valor pode ser booleano, duplo, inteiro
 JSONArray, GLEMPOAObjeto, Longo, ou String, ou o objeto GLEMPOAObject.NULL.
     * @throws GLEMPOAException Se erro de sintaxe.
     *
     * @return Um objeto.
     */
    public Object nextValue() throws GLEMPOAException {
        char c = this.nextClean();
        String string;

        switch (c) {
            case '"':
            case '\'':
                return this.nextString(c);
            case '{':
                this.back();
                return new GLEMPOAObject(this);
            case '[':
                this.back();
                return new GLEMPOAArray(this);
        }

        /*
         * Lidar com texto sem aspas. Estes podem ser os valores verdadeiro, falso ou
         * null, ou pode ser um número. Uma implementação (como esta)
         * É permitido também aceitar formulários não padronizados.
         *
         * Acumule personagens até chegarmos ao final do texto ou um
         * caractere de formatação.
         */

        StringBuilder sb = new StringBuilder();
        while (c >= ' ' && ",:]}/\\\"[{;=#".indexOf(c) < 0) {
            sb.append(c);
            c = this.next();
        }
        this.back();

        string = sb.toString().trim();
        if ("".equals(string)) {
            throw this.syntaxError("Missing value");
        }
        return GLEMPOAObject.stringToValue(string);
    }


    /**
    * Pular caracteres até que o próximo caractere seja o caractere solicitado.
     * Se o caractere solicitado não for encontrado, nenhum caractere será ignorado.
     * @param para um caractere para pular para.
     * @return O caractere solicitado ou zero se o caractere solicitado
     * Não foi encontrado.
     */
    public char skipTo(char to) throws GLEMPOAException {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                c = this.next();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.character = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while (c != to);
        } catch (IOException exception) {
            throw new GLEMPOAException(exception);
        }
        this.back();
        return c;
    }


    /**
     
* Faça um GLEMPOAException para sinalizar um erro de sintaxe.
     *
     * @param message A mensagem de erro.
     * @return Um objeto GLEMPOAException, adequado para jogar
     */
    public GLEMPOAException syntaxError(String message) {
        return new GLEMPOAException(message + this.toString());
    }


    /**
    
* Faça uma string imprimível deste GLEMPOATokener.
     *
     * @return "em {index} [caractere {caractere} linha {linha}]"
     */
    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " +
            this.line + "]";
    }
}
