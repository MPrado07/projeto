package glempoa;

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

import java.io.StringWriter;

/**
 
* O GLEMPOAStringer fornece uma maneira r�pida e conveniente de produzir texto JSON.
 * Os textos produzidos est�o estritamente de acordo com as regras de sintaxe do JSON. Nenhum espa�o em branco �
 adicionado, ent�o os resultados est�o prontos para transmiss�o ou armazenamento. Cada inst�ncia de
 O GLEMPOAStringer pode produzir um texto JSON.
 <p>
 Uma inst�ncia GLEMPOAStringer fornece um m�todo <code> value </ code> para anexar
 * valores para o
 * texto e uma chave <code> </ code>
 * m�todo para adicionar chaves antes de valores em objetos. Existem <code> array </ code>
 * e m�todos <code> endArray </ code> que criam e limitam valores de array, e
 M�todos <code> object </ code> e <code> endObject </ code> que fazem e limitam
 valores de objeto. Todos esses m�todos retornam a inst�ncia do GLEMPOAWriter,
 permitindo estilo cascata. 
 
 * O primeiro m�todo chamado deve ser <code> array </ code> ou <code> object </ code>.
 N�o h� m�todos para adicionar v�rgulas ou dois pontos. GLEMPOAStringer adiciona-os para
 voc�. Objetos e matrizes podem ser aninhados at� 20 n�veis de profundidade.

 * Isso �s vezes pode ser mais f�cil do que usar um JSONObject para construir uma string.
 * @author mathe
 * 
 */
public class GLEMPOAStringer extends GLEMPOAWriter {
    /**
     * Fa�a um novo JSONStringer. Pode ser usado para criar um texto JSON.
     */
    public GLEMPOAStringer() {
        super(new StringWriter());
    }

    /**
    * Retorne o texto JSON. Este m�todo � usado para obter o produto do
 GLEMPOAStringer instance. Ele retornar� <code> null </ code> se houver
     * problema na constru��o do texto JSON (como as chamadas para
     * <code> array </ code> n�o foram balanceados corretamente com chamadas para
     * <code> endArray </ code>).
     * @return O texto JSON.
     */
    public String toString() {
        return this.mode == 'd' ? this.writer.toString() : null;
    }
}
