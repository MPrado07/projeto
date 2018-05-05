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

* O GLEMPOAWriter fornece uma maneira r�pida e conveniente de produzir texto JSON.
 * Os textos produzidos est�o estritamente de acordo com as regras de sintaxe do JSON. Nenhum espa�o em branco �
 adicionado, ent�o os resultados est�o prontos para transmiss�o ou armazenamento. Cada inst�ncia de
 O GLEMPOAWriter pode produzir um texto JSON.
 <p>
 Uma inst�ncia do GLEMPOAWriter fornece um m�todo <code> value </ code> para anexar
 * valores para o
 * texto e uma chave <code> </ code>
 * m�todo para adicionar chaves antes de valores em objetos. Existem <code> array </ code>
 * e m�todos <code> endArray </ code> que criam e limitam valores de array, e
 M�todos <code> object </ code> e <code> endObject </ code> que fazem e limitam
 valores de objeto. Todos esses m�todos retornam a inst�ncia do GLEMPOAWriter,
 permitindo um estilo em cascata. Por exemplo, <pre>
 novo GLEMPOAWriter (myWriter)
     .objeto()
         .key ("JSON")
         .value ("Ol�, mundo!")
     .endObject (); </ pre> que escreve <pre>
 * {"JSON": "Ol�, mundo!"} </ Pre>
 * <p>
 * O primeiro m�todo chamado deve ser <code> array </ code> ou <code> object </ code>.
 N�o h� m�todos para adicionar v�rgulas ou dois pontos. GLEMPOAWriter adiciona-os para
 voc�. Objetos e matrizes podem ser aninhados at� 20 n�veis de profundidade.
 <p>
 �s vezes, isso pode ser mais f�cil do que usar um GLEMPOAObject para construir uma string.
 * @author mathe
 * 
 */
public class GLEMPOAWriter {
    private static final int maxdepth = 200;

    /**
    * O sinalizador de v�rgula determina se uma v�rgula deve ser emitida antes da pr�xima
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
     * O �ndice superior da pilha. Um valor de 0 indica que a pilha est� vazia.
     */
    private int top;

    /**
    * O escritor que receber� a sa�da.
     */
    protected Writer writer;

    /**
     * Fa�a um novo JSONWriter. Pode ser usado para criar um texto JSON.
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
     * @throws GLEMPOAException Se o valor estiver fora de seq��ncia.
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
     * Comece anexando uma nova matriz. Todos os valores at� o balanceamento
     * <code> endArray </ code> ser� anexado a este array. o
     O m�todo <code> endArray </ code> deve ser chamado para marcar o final da matriz.
     * @retornar isso
     * @throws GLEMPOAException Se o aninhamento for muito profundo ou se o objeto for
     * iniciado no lugar errado (por exemplo, como chave ou ap�s o final do
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
    
* Encerra um array. Este m�todo mais deve ser chamado para balancear chamadas para
     * <code> array </ code>.
     * @retornar isso
     * @throws GLEMPOAException Se aninhado incorretamente.
     */
    public GLEMPOAWriter endArray() throws GLEMPOAException {
        return this.end('a', ']');
    }

    /**
    
* Encerra um objeto. Este m�todo mais deve ser chamado para balancear chamadas para
     * <code> objeto </ code>.
     * @retornar isso
     * @throws GLEMPOAException Se aninhado incorretamente.
     */
    public GLEMPOAWriter endObject() throws GLEMPOAException {
        return this.end('k', '}');
    }

    /**
     * Anexar uma chave. A chave ser� associada ao pr�ximo valor. Em um
     * objeto, todo valor deve ser precedido por uma chave.
     * @param string Uma string de chave.
     * @retornar isso
     * @throws GLEMPOAException Se a chave estiver fora do lugar. Por exemplo, chaves
     * n�o pertence a arrays ou se a chave � nula.
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
    
* Comece anexando um novo objeto. Todas as chaves e valores at� o balanceamento
     * <code> endObject </ code> ser� anexado a este objeto. o
     O m�todo <code> endObject </ code> deve ser chamado para marcar o final do objeto.
     * @retornar isso
     * @throws GLEMPOAException Se o aninhamento for muito profundo ou se o objeto for
     * iniciado no lugar errado (por exemplo, como chave ou ap�s o final do
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
     * @throws GLEMPOAException Se o n�mero n�o for finito.
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
     * @param object O objeto a ser anexado. Pode ser nulo, ou booleano, num�rico,
   Cadeia de caracteres, GLEMPOAObjeto ou JSONArray ou um objeto que implementa JSONString.
     * @retornar isso
     * @throws GLEMPOAException Se o valor estiver fora de seq��ncia.
     */
    public GLEMPOAWriter value(Object object) throws GLEMPOAException {
        return this.append(GLEMPOAObject.valueToString(object));
    }
}
