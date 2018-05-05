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
 
* O GLEMPOAStringer fornece uma maneira rápida e conveniente de produzir texto JSON.
 * Os textos produzidos estão estritamente de acordo com as regras de sintaxe do JSON. Nenhum espaço em branco é
 adicionado, então os resultados estão prontos para transmissão ou armazenamento. Cada instância de
 O GLEMPOAStringer pode produzir um texto JSON.
 <p>
 Uma instância GLEMPOAStringer fornece um método <code> value </ code> para anexar
 * valores para o
 * texto e uma chave <code> </ code>
 * método para adicionar chaves antes de valores em objetos. Existem <code> array </ code>
 * e métodos <code> endArray </ code> que criam e limitam valores de array, e
 Métodos <code> object </ code> e <code> endObject </ code> que fazem e limitam
 valores de objeto. Todos esses métodos retornam a instância do GLEMPOAWriter,
 permitindo estilo cascata. 
 
 * O primeiro método chamado deve ser <code> array </ code> ou <code> object </ code>.
 Não há métodos para adicionar vírgulas ou dois pontos. GLEMPOAStringer adiciona-os para
 você. Objetos e matrizes podem ser aninhados até 20 níveis de profundidade.

 * Isso às vezes pode ser mais fácil do que usar um JSONObject para construir uma string.
 * @author mathe
 * 
 */
public class GLEMPOAStringer extends GLEMPOAWriter {
    /**
     * Faça um novo JSONStringer. Pode ser usado para criar um texto JSON.
     */
    public GLEMPOAStringer() {
        super(new StringWriter());
    }

    /**
    * Retorne o texto JSON. Este método é usado para obter o produto do
 GLEMPOAStringer instance. Ele retornará <code> null </ code> se houver
     * problema na construção do texto JSON (como as chamadas para
     * <code> array </ code> não foram balanceados corretamente com chamadas para
     * <code> endArray </ code>).
     * @return O texto JSON.
     */
    public String toString() {
        return this.mode == 'd' ? this.writer.toString() : null;
    }
}
