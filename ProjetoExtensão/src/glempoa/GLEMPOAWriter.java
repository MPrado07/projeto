package glempoa;

import java.io.IOException;
import java.io.Writer;

/*
Copyright (c) 2006 JSON.org

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

* O GLEMPOAWriter fornece uma maneira rápida e conveniente de produzir texto JSON.
 * Os textos produzidos estão estritamente de acordo com as regras de sintaxe do JSON. Nenhum espaço em branco é
 adicionado, então os resultados estão prontos para transmissão ou armazenamento. Cada instância de
 O GLEMPOAWriter pode produzir um texto JSON.
 <p>
 Uma instância do GLEMPOAWriter fornece um método <code> value </ code> para anexar
 * valores para o
 * texto e uma chave <code> </ code>
 * método para adicionar chaves antes de valores em objetos. Existem <code> array </ code>
 * e métodos <code> endArray </ code> que criam e limitam valores de array, e
 Métodos <code> object </ code> e <code> endObject </ code> que fazem e limitam
 valores de objeto. Todos esses métodos retornam a instância do GLEMPOAWriter,
 permitindo um estilo em cascata. Por exemplo, <pre>
 novo GLEMPOAWriter (myWriter)
     .objeto()
         .key ("JSON")
         .value ("Olá, mundo!")
     .endObject (); </ pre> que escreve <pre>
 * {"JSON": "Olá, mundo!"} </ Pre>
 * <p>
 * O primeiro método chamado deve ser <code> array </ code> ou <code> object </ code>.
 Não há métodos para adicionar vírgulas ou dois pontos. GLEMPOAWriter adiciona-os para
 você. Objetos e matrizes podem ser aninhados até 20 níveis de profundidade.
 <p>
 Às vezes, isso pode ser mais fácil do que usar um GLEMPOAObject para construir uma string.
 * @author mathe
 * 
 */
public class GLEMPOAWriter {
    private static final int maxdepth = 200;

    /**
    * O sinalizador de vírgula determina se uma vírgula deve ser emitida antes da próxima
     * valor.
     */
    private boolean comma;

    /**
     * O modo atual. Valores:
     * 'a' (array),
     * 'd' (feito),
     * 'i' (inicial)
     * 'k' (tecla),
     * 'o' (objeto).
     */
    protected char mode;

    /**
    * A pilha de objeto / matriz.
     */
    private final GLEMPOAObject stack[];

    /**
     * O índice superior da pilha. Um valor de 0 indica que a pilha está vazia.
     */
    private int top;

    /**
    * O escritor que receberá a saída.
     */
    protected Writer writer;

    /**
     * Faça um novo JSONWriter. Pode ser usado para criar um texto JSON.
     */
    public GLEMPOAWriter(Writer w) {
        this.comma = false;
        this.mode = 'i';
        this.stack = new GLEMPOAObject[maxdepth];
        this.top = 0;
        this.writer = w;
    }

    /**
     
* Anexar um valor.
     * @param string Um valor de string.
     * @retornar isso
     * @throws GLEMPOAException Se o valor estiver fora de seqüência.
     */
    private GLEMPOAWriter append(String string) throws GLEMPOAException {
        if (string == null) {
            throw new GLEMPOAException("Null pointer");
        }
        if (this.mode == 'o' || this.mode == 'a') {
            try {
                if (this.comma && this.mode == 'a') {
                    this.writer.write(',');
                }
                this.writer.write(string);
            } catch (IOException e) {
                throw new GLEMPOAException(e);
            }
            if (this.mode == 'o') {
                this.mode = 'k';
            }
            this.comma = true;
            return this;
        }
        throw new GLEMPOAException("Value out of sequence.");
    }

    /**
     * Comece anexando uma nova matriz. Todos os valores até o balanceamento
     * <code> endArray </ code> será anexado a este array. o
     O método <code> endArray </ code> deve ser chamado para marcar o final da matriz.
     * @retornar isso
     * @throws GLEMPOAException Se o aninhamento for muito profundo ou se o objeto for
     * iniciado no lugar errado (por exemplo, como chave ou após o final do
     * matriz ou objeto mais externo).
     */
    public GLEMPOAWriter array() throws GLEMPOAException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            this.push(null);
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new GLEMPOAException("Misplaced array.");
    }

    /**
    
* Acabar com algo.
     * modo de modo @param
     * @param c Closing character
     * @retornar isso
     * @throws GLEMPOAException Se desequilibrado.
     */
    private GLEMPOAWriter end(char mode, char c) throws GLEMPOAException {
        if (this.mode != mode) {
            throw new GLEMPOAException(mode == 'a'
                ? "Misplaced endArray."
                : "Misplaced endObject.");
        }
        this.pop(mode);
        try {
            this.writer.write(c);
        } catch (IOException e) {
            throw new GLEMPOAException(e);
        }
        this.comma = true;
        return this;
    }

    /**
    
* Encerra um array. Este método mais deve ser chamado para balancear chamadas para
     * <code> array </ code>.
     * @retornar isso
     * @throws GLEMPOAException Se aninhado incorretamente.
     */
    public GLEMPOAWriter endArray() throws GLEMPOAException {
        return this.end('a', ']');
    }

    /**
    
* Encerra um objeto. Este método mais deve ser chamado para balancear chamadas para
     * <code> objeto </ code>.
     * @retornar isso
     * @throws GLEMPOAException Se aninhado incorretamente.
     */
    public GLEMPOAWriter endObject() throws GLEMPOAException {
        return this.end('k', '}');
    }

    /**
     * Anexar uma chave. A chave será associada ao próximo valor. Em um
     * objeto, todo valor deve ser precedido por uma chave.
     * @param string Uma string de chave.
     * @retornar isso
     * @throws GLEMPOAException Se a chave estiver fora do lugar. Por exemplo, chaves
     * não pertence a arrays ou se a chave é nula.
     */
    public GLEMPOAWriter key(String string) throws GLEMPOAException {
        if (string == null) {
            throw new GLEMPOAException("Null key.");
        }
        if (this.mode == 'k') {
            try {
                this.stack[this.top - 1].putOnce(string, Boolean.TRUE);
                if (this.comma) {
                    this.writer.write(',');
                }
                this.writer.write(GLEMPOAObject.quote(string));
                this.writer.write(':');
                this.comma = false;
                this.mode = 'o';
                return this;
            } catch (IOException e) {
                throw new GLEMPOAException(e);
            }
        }
        throw new GLEMPOAException("Misplaced key.");
    }


    /**
    
* Comece anexando um novo objeto. Todas as chaves e valores até o balanceamento
     * <code> endObject </ code> será anexado a este objeto. o
     O método <code> endObject </ code> deve ser chamado para marcar o final do objeto.
     * @retornar isso
     * @throws GLEMPOAException Se o aninhamento for muito profundo ou se o objeto for
     * iniciado no lugar errado (por exemplo, como chave ou após o final do
     * matriz ou objeto mais externo).
     */
    public GLEMPOAWriter object() throws GLEMPOAException {
        if (this.mode == 'i') {
            this.mode = 'o';
        }
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            this.push(new GLEMPOAObject());
            this.comma = false;
            return this;
        }
        throw new GLEMPOAException("Misplaced object.");

    }


    /**
     
     * Pop uma matriz ou escopo de objeto.
     * @param c O escopo para fechar.
     * @throws GLEMPOAException Se o aninhamento estiver errado.
     */
    private void pop(char c) throws GLEMPOAException {
        if (this.top <= 0) {
            throw new GLEMPOAException("Nesting error.");
        }
        char m = this.stack[this.top - 1] == null ? 'a' : 'k';
        if (m != c) {
            throw new GLEMPOAException("Nesting error.");
        }
        this.top -= 1;
        this.mode = this.top == 0
            ? 'd'
            : this.stack[this.top - 1] == null
            ? 'a'
            : 'k';
    }

    /**
     * Empurre um escopo de matriz ou objeto.
     * @param jo O escopo para abrir.
     * @throws GLEMPOAException Se o aninhamento for muito profundo.
     */
    private void push(GLEMPOAObject jo) throws GLEMPOAException {
        if (this.top >= maxdepth) {
            throw new GLEMPOAException("Nesting too deep.");
        }
        this.stack[this.top] = jo;
        this.mode = jo == null ? 'a' : 'k';
        this.top += 1;
    }


    /**
    
* Anexar o valor <code> true </ code> ou o valor
     * <code> false </ code>.
     * @param b Um booleano.
     * @retornar isso
     * @throws GLEMPOAException
     */
    public GLEMPOAWriter value(boolean b) throws GLEMPOAException {
        return this.append(b ? "true" : "false");
    }

    /**
     * Anexar um valor duplo.
     * @param d Um duplo.
     * @retornar isso
     * @throws GLEMPOAException Se o número não for finito.
     */
    public GLEMPOAWriter value(double d) throws GLEMPOAException {
        return this.value(new Double(d));
    }

    /**
     
* Anexar um valor longo.
     * @param l Um longo.
     * @retornar isso
     * @throws GLEMPOAException
     */
    public GLEMPOAWriter value(long l) throws GLEMPOAException {
        return this.append(Long.toString(l));
    }


    /**
    
* Anexar um valor de objeto.
     * @param object O objeto a ser anexado. Pode ser nulo, ou booleano, numérico,
   Cadeia de caracteres, GLEMPOAObjeto ou JSONArray ou um objeto que implementa JSONString.
     * @retornar isso
     * @throws GLEMPOAException Se o valor estiver fora de seqüência.
     */
    public GLEMPOAWriter value(Object object) throws GLEMPOAException {
        return this.append(GLEMPOAObject.valueToString(object));
    }
}
