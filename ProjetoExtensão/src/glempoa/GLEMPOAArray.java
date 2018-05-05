package glempoa;




import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;



public class GLEMPOAArray {

    /**
     * 
     */
    private final ArrayList<Object> myArrayList;

    /**
     * construirá um vetor vazio para iniciar (evitar captura de lixo)
     */
    public GLEMPOAArray() {
        this.myArrayList = new ArrayList<Object>();
    }

    /**
     
     * Construa um GLEMPOAArray de um GLEMPOATokener.
     *
     * @ param x
     Um GLEMPOATokener
     * @throws GLEMPOAException
     * Se existir um erro de sintaxe
     */
    public GLEMPOAArray(GLEMPOATorkener x) throws GLEMPOAException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A função GLEMPOAArray matriz de texto deve começar com:  '['");
        }
        if (x.nextClean() != ']') {             // essa é a função de iniciação do vetor de cadastro
            x.back();
            for (;;) {
                if (x.nextClean() == ',') {			 
                    x.back();
                    this.myArrayList.add(GLEMPOAObject.NULL);
                } else {
                    x.back();
                    this.myArrayList.add(x.nextValue());
                }
                switch (x.nextClean()) {
                case ',':
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    break;
                case ']':
                    return;
                default:
                    throw x.syntaxError("Esperado a ',' or ']'");
                }
            }
        }
    }

   
    public GLEMPOAArray(String source) throws GLEMPOAException {
        this(new GLEMPOATorkener(source));
    }

    /**
     * Construa um GLEMPOAArray de uma coleção.
     *
     * @param collection
     *            A Collection.
     */
    public GLEMPOAArray(Collection<Object> collection) {
        this.myArrayList = new ArrayList<Object>();
        if (collection != null) {
            Iterator<Object> iter = collection.iterator();
            while (iter.hasNext()) {
                this.myArrayList.add(GLEMPOAObject.wrap(iter.next()));
            }
        }
    }

    
    public GLEMPOAArray(Object array) throws GLEMPOAException {
        this();
        if (array.getClass().isArray()) {
            int length = Array.getLength(array);
            for (int i = 0; i < length; i += 1) {
                this.put(GLEMPOAObject.wrap(Array.get(array, i)));
            }
        } else {
            throw new GLEMPOAException(
                    "GLEMPOAArray initial value should be a string or collection or array.");
        }
    }

    /**
     * Obtenha o valor do objeto associado a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return Um valor de objeto.
     * @throws GLEMPOAException
     * Se não houver valor para o índice.
     */
    
    public Object get(int index) throws GLEMPOAException {
        Object object = this.opt(index);
        if (object == null) {
            throw new GLEMPOAException("GLEMPOAArray[" + index + "] not found.");
        }
        return object;
    }

    /**
     * Obtenha o valor booleano associado a um índice. Os valores da string "true"
     * e "false" são convertidos em booleanos.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @Retorna a verdade.
     * @throws GLEMPOAException
     * Se não houver valor para o índice ou se o valor não for
     * conversível em booleano.
     */
    
    public boolean getBoolean(int index) throws GLEMPOAException {
        Object object = this.get(index);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw new GLEMPOAException("GLEMPOAArray[" + index + "] is not a boolean.");
    }

    /**
    * Obtenha o valor duplo associado a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return O valor.
     * @throws GLEMPOAException
     * Se a chave não for encontrada ou se o valor não puder ser convertido
     * para um número.
     */
    
    public double getDouble(int index) throws GLEMPOAException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (Exception e) {
            throw new GLEMPOAException("GLEMPOAArray[" + index + "] is not a number.");
        }
    }

    /**
     * Obtenha o valor int associado a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return O valor.
     * @throws GLEMPOAException
     * Se a chave não for encontrada ou se o valor não for um número.
     */
    
    public int getInt(int index) throws GLEMPOAException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (Exception e) {
            throw new GLEMPOAException("GLEMPOAArray[" + index + "] is not a number.");
        }
    }

    /**
     * Obtenha o GLEMPOAArray associado a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return Um valor GLEMPOAArray.
     * @throws GLEMPOAException
     * Se não houver valor para o índice. ou se o valor não é um
     * GLEMPOAArray
     */
    
    public GLEMPOAArray getGLEMPOAArray(int index) throws GLEMPOAException {
        Object object = this.get(index);
        if (object instanceof GLEMPOAArray) {
            return (GLEMPOAArray) object;
        }
        throw new GLEMPOAException("GLEMPOAArray[" + index + "] is not a GLEMPOAArray.");
    }

    /**
     * Obtenha o GLEMPOAObject associado a um índice.
     *
     * @param index
     * subscrito
     * @return A GLEMPOAObject value.
     * @throws GLEMPOAException
     * Se não houver valor para o índice ou se o valor não for
     * GLEMPOAObject
     */
    
    public GLEMPOAObject getGLEMPOAObject(int index) throws GLEMPOAException {
        Object object = this.get(index);
        if (object instanceof GLEMPOAObject) {
            return (GLEMPOAObject) object;
        }
        throw new GLEMPOAException("GLEMPOAArray[" + index + "] is not a GLEMPOAObject.");
    }

    /**
    * Obtenha o valor longo associado a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return O valor.
     * @throws GLEMPOAException
     * Se a chave não for encontrada ou se o valor não puder ser convertido
     * para um número.
     */
    public long getLong(int index) throws GLEMPOAException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (Exception e) {
            throw new GLEMPOAException("GLEMPOAArray[" + index + "] is not a number.");
        }
    }

    /**
     * Obtenha a string associada a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return Um valor de string.
     * @throws GLEMPOAException
     * Se não houver valor de string para o índice.
     */
    public String getString(int index) throws GLEMPOAException {
        Object object = this.get(index);
        if (object instanceof String) {
            return (String) object;
        }
        throw new GLEMPOAException("GLEMPOAArray[" + index + "] not a string.");
    }

    /**
    * Determine se o valor é nulo.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @retorne true se o valor no índice for nulo ou se não houver valor.
     */
    public boolean isNull(int index) {
        return GLEMPOAObject.NULL.equals(this.opt(index));
    }

    public String join(String separator) throws GLEMPOAException {
        int len = this.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < len; i += 1) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(GLEMPOAObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }

    /**
    
* Obtenha o número de elementos no GLEMPOAArray, incluindo nulos.
     *
     * @return O comprimento (ou tamanho).
     */
    public int length() {
        return this.myArrayList.size();
    }

    /**
    * Obtenha o valor de objeto opcional associado a um índice.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return Um valor de objeto, ou null, se não houver nenhum objeto nesse índice.
     */
    public Object opt(int index) {
        return (index < 0 || index >= this.length()) ? null : this.myArrayList
                .get(index);
    }

    /**
     * Obtenha o valor booleano opcional associado a um índice. Ele retorna falso
     * se não houver valor nesse índice ou se o valor não for Booleano.TRUE
     * ou a string "true".
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @Retorna a verdade.
     */
    public boolean optBoolean(int index) {
        return this.optBoolean(index, false);
    }

    /**
     * Obtenha o valor booleano opcional associado a um índice. Devolve o
     * defaultValue se não houver valor nesse índice ou se não for um valor booleano
     * ou a String "true" ou "false" (sem distinção entre maiúsculas e minúsculas).
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @param defaultValue
     * Um padrão booleano.
     * @Retorna a verdade.
     */
    public boolean optBoolean(int index, boolean defaultValue) {
        try {
            return this.getBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Obtenha o valor duplo opcional associado a um índice. NaN é retornado
     * se não houver valor para o índice ou se o valor não for um número e
     * não pode ser convertido em um número.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return O valor.
     */
    public double optDouble(int index) {
        return this.optDouble(index, Double.NaN);
    }

    /**
     
* Obtenha o valor duplo opcional associado a um índice. O defaultValue
     * é retornado se não houver valor para o índice ou se o valor não for
     * número e não pode ser convertido em um número.
     *
     * @param index
     * subscrito
     * @param defaultValue
     * O valor padrão.
     * @return O valor.
     */
    public double optDouble(int index, double defaultValue) {
        try {
            return this.getDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Obtenha o valor int opcional associado a um índice. Zero é retornado se
     * não há valor para o índice, ou se o valor não é um número e
     * não pode ser convertido em um número.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return O valor.
     */
    public int optInt(int index) {
        return this.optInt(index, 0);
    }

    /**
     * Obtenha o valor int opcional associado a um índice. O valor padrão é
     * retornado se não houver valor para o índice ou se o valor não for
     * número e não pode ser convertido em um número.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @param defaultValue
     * O valor padrão.
     * @return O valor.
     */
    public int optInt(int index, int defaultValue) {
        try {
            return this.getInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
    * Obtenha o GLEMPOAArray opcional associado a um índice.
     *
     * @param index
     * subscrito
     * @return Um valor GLEMPOAArray, ou null se o índice não tiver valor, ou se o
     * value não é um GLEMPOAArray
     */
    public GLEMPOAArray optGLEMPOAArray(int index) {
        Object o = this.opt(index);
        return o instanceof GLEMPOAArray ? (GLEMPOAArray) o : null;
    }

    /**
     * Obtenha o GLEMPOAObject opcional associado a um índice. O nulo é retornado se
     * a chave não é encontrada, ou nula se o índice não tem valor, ou se o valor
     * não é um GLEMPOAObject.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return A GLEMPOAObject value.
     */
    public GLEMPOAObject optGLEMPOAObject(int index) {
        Object o = this.opt(index);
        return o instanceof GLEMPOAObject ? (GLEMPOAObject) o : null;
    }

    /**
     * Obtenha o valor longo opcional associado a um índice. Zero é retornado se
     * não há valor para o índice, ou se o valor não é um número e
     * não pode ser convertido em um número.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return O valor.
     */
    public long optLong(int index) {
        return this.optLong(index, 0);
    }

    /**
     * Obtenha o valor longo opcional associado a um índice. O valor padrão é
     * retornado se não houver valor para o índice ou se o valor não for
     * número e não pode ser convertido em um número.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @param defaultValue
     * O valor padrão.
     * @return The value.
     */
    public long optLong(int index, long defaultValue) {
        try {
            return this.getLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Obtenha o valor de sequência opcional associado a um índice. Ele retorna um
     * string vazia se não houver valor nesse índice. Se o valor não for
     * string e não é nulo, então é convertido em uma string.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @return Um valor de string.
     */
    public String optString(int index) {
        return this.optString(index, "");
    }

    /**
     * Obtenha a cadeia opcional associada a um índice. O valor padrão é
     * retornado se a chave não for encontrada.
     *
     * @param index
     * O índice deve estar entre 0 e length () - 1.
     * @param defaultValue
     * O valor padrão.
     * @return Um valor de string.
     */
    public String optString(int index, String defaultValue) {
        Object object = this.opt(index);
        return GLEMPOAObject.NULL.equals(object) ? defaultValue : object
                .toString();
    }

    /**
     * Anexar um valor booleano. Isso aumenta o comprimento da matriz em um.
     *
     * valor @param
     * Um valor booleano.
     * @retorne isso.
     */
    public GLEMPOAArray put(boolean value) {
        this.put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Coloque um valor no GLEMPOAArray, onde o valor será um GLEMPOAArray que
     * é produzido a partir de uma coleção.
     *
     * valor @param
     * Um valor de coleção.
     * @retorne isso.
     */
    public GLEMPOAArray put(Collection<Object> value) {
        this.put(new GLEMPOAArray(value));
        return this;
    }

    /**
     * Anexar um valor duplo. Isso aumenta o comprimento da matriz em um.
     *
     * valor @param
     * Um valor duplo.
     * @throws GLEMPOAException
     * se o valor não for finito.
     * @retorne isso.
     */
    public GLEMPOAArray put(double value) throws GLEMPOAException {
        Double d = new Double(value);
        GLEMPOAObject.testValidity(d);
        this.put(d);
        return this;
    }

    /**
    * Anexar um valor int. Isso aumenta o comprimento da matriz em um.
     *
     * valor @param
     * Um valor int.
     * @retorne isso.
     */
    public GLEMPOAArray put(int value) {
        this.put(new Integer(value));
        return this;
    }

    /**
     * Anexar um valor longo. Isso aumenta o comprimento da matriz em um.
     *
     * valor @param
     * Um valor longo.
     * @retorne isso.
     */
    public GLEMPOAArray put(long value) {
        this.put(new Long(value));
        return this;
    }

    /**
    * Coloque um valor no GLEMPOAArray, onde o valor será um GLEMPOAObjeto que
     * é produzido a partir de um mapa.
     *
     * valor @param
     * Um valor de mapa.
     * @retorne isso.
     */
    public GLEMPOAArray put(Map<String, Object> value) {
        this.put(new GLEMPOAObject(value));
        return this;
    }

    /**
    * Anexar um valor de objeto. Isso aumenta o comprimento da matriz em um.
     *
     * valor @param
     * Um valor de objeto. O valor deve ser um Booleano, Double,
     * Integer, GLEMPOAArray, GLEMPOAObject, Long, ou String, ou o
     Objeto * GLEMPOAObject.NULL.
     * @retorne isso.
     */
    public GLEMPOAArray put(Object value) {
        this.myArrayList.add(value);
        return this;
    }

    /**
     * Coloque ou substitua um valor booleano no GLEMPOAArray. Se o índice é maior
     * do que o comprimento do GLEMPOAArray, os elementos nulos serão adicionados como
     * necessário para preenchê-lo.
     *
     * @param index
     * O subscrito.
     * valor @param
     * Um valor booleano.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo.
     */
    public GLEMPOAArray put(int index, boolean value) throws GLEMPOAException {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
     * Coloque um valor no GLEMPOAArray, onde o valor será um GLEMPOAArray que
     * é produzido a partir de uma coleção.
     *
     * @param index
     * O subscrito.
     * valor @param
     * Um valor de coleção.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo ou se o valor não for finito.
     */
    public GLEMPOAArray put(int index, Collection<Object> value) throws GLEMPOAException {
        this.put(index, new GLEMPOAArray(value));
        return this;
    }

    /**
     * Coloque ou substitua um valor duplo. Se o índice for maior que o comprimento de
     * o GLEMPOAArray, então os elementos nulos serão adicionados conforme necessário para preenchê-lo
     * Fora.
     *
     * @param index
     * O subscrito.
     * valor @param
     * Um valor duplo.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo ou se o valor não for finito.
     */
    public GLEMPOAArray put(int index, double value) throws GLEMPOAException {
        this.put(index, new Double(value));
        return this;
    }

    /**
    * Coloque ou substitua um valor int. Se o índice for maior que o comprimento de
     * o GLEMPOAArray, então os elementos nulos serão adicionados conforme necessário para preenchê-lo
     * Fora.
     *
     * @param index
     * O subscrito.
     * valor @param
     * Um valor int.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo.
     */
    public GLEMPOAArray put(int index, int value) throws GLEMPOAException {
        this.put(index, new Integer(value));
        return this;
    }

    /**
     * Coloque ou substitua um valor longo. Se o índice for maior que o comprimento de
     * o GLEMPOAArray, então os elementos nulos serão adicionados conforme necessário para preenchê-lo
     * Fora.
     *
     * @param index
     * O subscrito.
     * valor @param
     * Um valor longo.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo.
     */
    public GLEMPOAArray put(int index, long value) throws GLEMPOAException {
        this.put(index, new Long(value));
        return this;
    }

    /**
     * Coloque um valor no GLEMPOAArray, em que o valor será um GLEMPOAObjeto que
     * é produzido a partir de um mapa.
     *
     * @param index
     * O subscrito.
     * valor @param
     * O valor do mapa.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo ou se o valor for inválido
     *             número.
     */
    public GLEMPOAArray put(int index, Map<String, Object> value) throws GLEMPOAException {
        this.put(index, new GLEMPOAObject(value));
        return this;
    }

    /**
     * Coloque ou substitua um valor de objeto no GLEMPOAArray. Se o índice é maior
     * do que o comprimento do GLEMPOAArray, os elementos nulos serão adicionados como
     * necessário para preenchê-lo.
     *
     * @param index
     * O subscrito.
     * valor @param
     * O valor para colocar na matriz. O valor deve ser um
     * Boolean, Double, Integer, GLEMPOAArray, GLEMPOAObject, Long ou
     * String ou o objeto GLEMPOAObject.NULL.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o índice for negativo ou se o valor for inválido
     *             número.
     */
    public GLEMPOAArray put(int index, Object value) throws GLEMPOAException {
        GLEMPOAObject.testValidity(value);
        if (index < 0) {
            throw new GLEMPOAException("GLEMPOAArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            this.myArrayList.set(index, value);
        } else {
            while (index != this.length()) {
                this.put(GLEMPOAObject.NULL);
            }
            this.put(value);
        }
        return this;
    }

    /**
    * Remova um índice e feche o buraco.
     *
     * @param index
     * O índice do elemento a ser removido.
     * @return O valor que foi associado ao índice, ou null, se houver
     * não tinha valor.
     */
    public Object remove(int index) {
        return index >= 0 && index < this.length()
            ? this.myArrayList.remove(index)
            : null;
    }

    /**
     
* Determine se duas GLEMPOAArrays são semelhantes.
     * Eles devem conter seqüências semelhantes.
     *
     * @param other O outro GLEMPOAArray
     * @return true se forem iguais
     */
    public boolean similar(Object other) {
        if (!(other instanceof GLEMPOAArray)) {
            return false;
        }
        int len = this.length();
        if (len != ((GLEMPOAArray)other).length()) {
            return false;
        }
        for (int i = 0; i < len; i += 1) {
            Object valueThis = this.get(i);
            Object valueOther = ((GLEMPOAArray)other).get(i);
            if (valueThis instanceof GLEMPOAObject) {
                if (!((GLEMPOAObject)valueThis).similar(valueOther)) {
                    return false;
                }
            } else if (valueThis instanceof GLEMPOAArray) {
                if (!((GLEMPOAArray)valueThis).similar(valueOther)) {
                    return false;
                }
            } else if (!valueThis.equals(valueOther)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Produza um GLEMPOAObject combinando um GLEMPOAArray de nomes com os valores de
     * este GLEMPOAArray.
     *
     * @param nomes
     * Um GLEMPOAArray contendo uma lista de strings principais. Estes serão
     * emparelhado com os valores.
     * @return A GLEMPOAObject, ou null se não houver nomes ou se este GLEMPOAArray
     * não tem valores.
     * @throws GLEMPOAException
     * Se algum dos nomes for nulo.
     */
    public GLEMPOAObject toGLEMPOAObject(GLEMPOAArray names) throws GLEMPOAException {
        if (names == null || names.length() == 0 || this.length() == 0) {
            return null;
        }
        GLEMPOAObject jo = new GLEMPOAObject();
        for (int i = 0; i < names.length(); i += 1) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }

    /**
     * Faça um texto GLEMPOA deste GLEMPOAArray. Para compacidade, não é desnecessário
     * espaço em branco é adicionado. Se não for possível produzir uma sintaxe
     * texto GLEMPOA correto, em seguida, será retornado null. Isso pode ocorrer se
     * o array contém um número inválido.
     * <p>
     * Aviso: este método assume que a estrutura de dados é acíclica.
     *
     * @retornar uma representação transmissível, exibível e exibível do
     * matriz.
     */
    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
    * Faça um texto GLEMPOA bonito deste GLEMPOAArray. Aviso: este método
     * assume que a estrutura de dados é acíclica.
     *
     * @ param indentFactor
     * O número de espaços para adicionar a cada nível de recuo.
     * @retornar uma representação transmissível, exibível e exibível do
     * objeto, começando com <code> [</ code> & nbsp; <small> (esquerda
     * colchete) </ small> e terminando com <code>] </ code>
     * & nbsp; <small> (colchete direito) </ small>.
     * @throws GLEMPOAException
     */
    public String toString(int indentFactor) throws GLEMPOAException {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }

    /**
	     Escreva o conteúdo do GLEMPOAArray como texto GLEMPOA para um escritor. Para
     * compacidade, nenhum espaço em branco é adicionado.
     * <p>
     * Aviso: este método assume que a estrutura de dados é acíclica.
     *
     * @return O escritor.
     * @throws GLEMPOAException
     */
    public Writer write(Writer writer) throws GLEMPOAException {
        return this.write(writer, 0, 0);
    }

    /**
      Escreva o conteúdo do GLEMPOAArray como texto GLEMPOA para um escritor. Para
     * compacidade, nenhum espaço em branco é adicionado.
     * <p>
     * Aviso: este método assume que a estrutura de dados é acíclica.
     *
     * @ param indentFactor
     * O número de espaços para adicionar a cada nível de recuo.
     * @ param recuo
     * O recuo do nível superior.
     * @return O escritor.
     * @throws GLEMPOAException
     */
    Writer write(Writer writer, int indentFactor, int indent)
            throws GLEMPOAException {
        try {
            boolean commanate = false;
            int length = this.length();
            writer.write('[');

            if (length == 1) {
                GLEMPOAObject.writeValue(writer, this.myArrayList.get(0),
                        indentFactor, indent);
            } else if (length != 0) {
                final int newindent = indent + indentFactor;

                for (int i = 0; i < length; i += 1) {
                    if (commanate) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    GLEMPOAObject.indent(writer, newindent);
                    GLEMPOAObject.writeValue(writer, this.myArrayList.get(i),
                            indentFactor, newindent);
                    commanate = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                GLEMPOAObject.indent(writer, indent);
            }
            writer.write(']');
            return writer;
        } catch (IOException e) {
            throw new GLEMPOAException(e);
        }
    }
}
