package glempoa;

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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 
 * @author mathe.org
 * 
 */
public class GLEMPOAObject {
    /**
     * GLEMPOAObject.NULL é equivalente ao valor que o JavaScript chama nulo,
 enquanto o nulo de Java é equivalente ao valor que o JavaScript chama
 Indefinido.
     */
    private static final class Null {

        /**
         
         * Existe apenas a intenção de ser uma única instância do objeto NULL,
         * então o método clone retorna a si mesmo.
         *
         * @return NULL.
         */
        @Override
        protected final Object clone() {
            return this;
        }

        /**
         * Um objeto nulo é igual ao valor nulo e a ele mesmo.
         *
         * @param object
         * Um objeto para testar a nulidade.
         * @return true se o parâmetro object for o objeto GLEMPOAObject.NULL ou
         nulo.
         */
        
        @Override
        public boolean equals(Object object) {
            return object == null || object == this;
        }

        /**
         
         * Obtenha o valor da string "nulo".
         *
         * @return A string "null".
         */
        public String toString() {
            return "null";
        }
    }

   
    private final Map<String, Object> map;

     public static final Object NULL = new Null();

    
    public GLEMPOAObject() {
        this.map = new HashMap<String, Object>();
    }

    
    public GLEMPOAObject(GLEMPOAObject jo, String[] names) {
        this();
        for (int i = 0; i < names.length; i += 1) {
            try {
                this.putOnce(names[i], jo.opt(names[i]));
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Construa um JSONObject de um GLEMPOATokener.
     *
     * @ param x
     * Um objeto GLEMPOATokener contendo a string de origem.
     * @throws GLEMPOAException
     * Se houver um erro de sintaxe na string de origem ou
     * chave duplicada.
     */
    
    public GLEMPOAObject(GLEMPOATorkener x) throws GLEMPOAException {
        this();
        char c;
        String key;

        if (x.nextClean() != '{') {
            throw x.syntaxError("A GLEMPOAObject text must begin with '{'");
        }
        for (;;) {
            c = x.nextClean();
            switch (c) {
            case 0:
                throw x.syntaxError("A GLEMPOAObject text must end with '}'");
            case '}':
                return;
            default:
                x.back();
                key = x.nextValue().toString();
            }



            c = x.nextClean();
            if (c != ':') {
                throw x.syntaxError("Esperado a ':' depois uma chave");
            }
            this.putOnce(key, x.nextValue());



            switch (x.nextClean()) {
            case ';':
            case ',':
                if (x.nextClean() == '}') {
                    return;
                }
                x.back();
                break;
            case '}':
                return;
            default:
                throw x.syntaxError("Expected a ',' or '}'");
            }
        }
    }

    /**
     * Construa um GLEMPOAObject de um mapa.
     *
     * mapa @param
     * Um objeto de mapa que pode ser usado para inicializar o conteúdo de
     * o JSONObject.
     * @throws GLEMPOAException
     */

    public GLEMPOAObject(Map<String, Object> map) {
        this.map = new HashMap<String, Object>();
        if (map != null) {
            Iterator<Entry<String, Object>> i = map.entrySet().iterator();
            while (i.hasNext()) {
                Entry<String, Object> entry = i.next();
                Object value = entry.getValue();
                if (value != null) {
                    this.map.put(entry.getKey(), wrap(value));
                }
            }
        }
    }

    /**
	    * Construa um GLEMPOAObject de um objeto usando getters de beans. Reflete sobre
     * todos os métodos públicos do objeto. Para cada um dos métodos sem
     * parâmetros e um nome começando com <code> "get" </ code> ou
     * <code> "é" </ code> seguido por uma letra maiúscula, o método é invocado,
     * e uma chave e o valor retornado do método getter são colocados no
     * novo GLEMPOAObject.
     
     */
    public GLEMPOAObject(Object bean) {
        this();
        this.populateMap(bean);
    }

    /**
     
     * Construa um objeto GLEMPOAObject de um objeto, usando reflexão para encontrar
     * membros públicos. As chaves do JSONObject resultantes serão as strings de
     * a matriz de nomes e os valores serão os valores de campo associados
     * aquelas chaves no objeto. Se uma chave não for encontrada ou não estiver visível,
     * não será copiado para o novo JSONObject.
     *
     * @param object
     * Um objeto que possui campos que devem ser usados ​​para fazer um
     * JSONObject.
     * @param nomes
     * Uma matriz de strings, os nomes dos campos a serem obtidos
     * do objeto.
     */
    
    public GLEMPOAObject(Object object, String names[]) {
        this();
        Class c = object.getClass();
        for (int i = 0; i < names.length; i += 1) {
            String name = names[i];
            try {
                this.putOpt(name, c.getField(name).get(object));
            } catch (Exception ignore) {
            }
        }
    }

    /**
    
     * Construa um GLEMPOAObject a partir de uma string de texto JSON de origem. Este é o mais
     * Construtor GLEMPOAObject comumente usado.
     *
     * @param source
     * Uma string começando com <code> {</ code> & nbsp; <small> (esquerda
     * brace) </ small> e terminando com <code>} </ code>
     * & nbsp; <small> (chave direita) </ small>.
     * @throws GLEMPOAException Se houver um erro de sintaxe na string de origem ou
                chave duplicada.
     */
    public GLEMPOAObject(String source) throws GLEMPOAException {
        this(new GLEMPOATorkener(source));
    }

    /**
     * Construa um GLEMPOAObject de um ResourceBundle.
     *
     * @param baseName
     * O nome base do ResourceBundle.
     * @ locale param
     * O local para carregar o ResourceBundle para.
     * @throws GLEMPOAException
     * Se qualquer JSONExceptions for detectado.
     */
    public GLEMPOAObject(String baseName, Locale locale) throws GLEMPOAException {
        this();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale,
                Thread.currentThread().getContextClassLoader());

// Iterate through the keys in the bundle.

        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (key != null) {

                String[] path = ((String) key).split("\\.");
                int last = path.length - 1;
                GLEMPOAObject target = this;
                for (int i = 0; i < last; i += 1) {
                    String segment = path[i];
                    GLEMPOAObject nextTarget = target.optJSONObject(segment);
                    if (nextTarget == null) {
                        nextTarget = new GLEMPOAObject();
                        target.put(segment, nextTarget);
                    }
                    target = nextTarget;
                }
                target.put(path[last], bundle.getString((String) key));
            }
        }
    }

    /**
     * Acumule valores sob uma chave. É semelhante ao método put, exceto
     * que, se já houver um objeto armazenado sob a chave, um GLEMPOAArray
     * é armazenado sob a chave para conter todos os valores acumulados. Se lá
     * já é um GLEMPOAArray, então o novo valor é anexado a ele. Dentro
     * contraste, o método put substitui o valor anterior.
     *
     * Se apenas um valor for acumulado que não seja um GLEMPOAArray, o resultado
     * será o mesmo que usar put. Mas se vários valores são acumulados,
     * então o resultado será como acrescentar.
     *
     * tecla @param
     * Uma string de chave.
     * valor @param
     * Um objeto a ser acumulado sob a chave.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se o valor for um número inválido ou se a chave for nula.
     */
    public GLEMPOAObject accumulate(String key, Object value) throws GLEMPOAException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key,
                    value instanceof GLEMPOAArray ? new GLEMPOAArray().put(value)
                            : value);
        } else if (object instanceof GLEMPOAArray) {
            ((GLEMPOAArray) object).put(value);
        } else {
            this.put(key, new GLEMPOAArray().put(object).put(value));
        }
        return this;
    }

    /**
    
* Anexar valores ao array sob uma chave. Se a chave não existir no
 GLEMPOAObjeto, a chave é colocada no GLEMPOAObjeto, com seu valor sendo um
 GLEMPOAArray contendo o parâmetro value. Se a chave já estava
 associado a um GLEMPOAArray, o parâmetro value é anexado a ele.
     *
     * tecla @param
     * Uma string de chave.
     * valor @param
     * Um objeto a ser acumulado sob a chave.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se a chave for nula ou se o valor atual associado a
     * a chave não é um GLEMPOAArray.
     */
    public GLEMPOAObject append(String key, Object value) throws GLEMPOAException {
        testValidity(value);
        Object object = this.opt(key);
        if (object == null) {
            this.put(key, new GLEMPOAArray().put(value));
        } else if (object instanceof GLEMPOAArray) {
            this.put(key, ((GLEMPOAArray) object).put(value));
        } else {
            throw new GLEMPOAException("JSONObject[" + key
                    + "] is not a GLEMPOAArray.");
        }
        return this;
    }


    /**
     * Produza uma string de um duplo. A string "null" será retornada se o
     * número não é finito.
     *
     * @param d
     *            O dobro.
     * @return A String.
     */
    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

// Shave off trailing zeros and decimal point, if possible.

        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    /**
     * Obtenha o objeto de valor associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return O objeto associado à chave.
     * @throws GLEMPOAException
     * se a chave não for encontrada.
     */

    public Object get(String key) throws GLEMPOAException {
        if (key == null) {
            throw new GLEMPOAException("Null key.");
        }
        Object object = this.opt(key);
        if (object == null) {
            throw new GLEMPOAException("JSONObject[" + quote(key) + "] not found.");
        }
        return object;
    }

    /**
     * Obtenha o valor booleano associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @Retorna a verdade.
     * @throws GLEMPOAException
     * se o valor não for booleano ou a string "true" ou
     * "falso".
     */
    public boolean getBoolean(String key) throws GLEMPOAException {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw new GLEMPOAException("JSONObject[" + quote(key)
                + "] is not a Boolean.");
    }

    /**
     
* Obtenha o valor duplo associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return O valor numérico.
     * @throws GLEMPOAException
     * se a chave não for encontrada ou se o valor não for um número
     * objeto e não pode ser convertido em um número.
     */
    public double getDouble(String key) throws GLEMPOAException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (Exception e) {
            throw new GLEMPOAException("JSONObject[" + quote(key)
                    + "] is not a number.");
        }
    }

    /**
     * Obtenha o valor int associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return O valor inteiro.
     * @throws GLEMPOAException
     * se a chave não for encontrada ou se o valor não puder ser convertido
     * para um inteiro.
     */
    public int getInt(String key) throws GLEMPOAException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (Exception e) {
            throw new GLEMPOAException("JSONObject[" + quote(key)
                    + "] is not an int.");
        }
    }

    /**
    
* Obtenha o valor GLEMPOAArray associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return A GLEMPOAArray que é o valor.
     * @throws GLEMPOAException
     * se a chave não for encontrada ou se o valor não for um GLEMPOAArray.
    
     */
    
    public GLEMPOAArray getGLEMPOAArray(String key) throws GLEMPOAException {
        Object object = this.get(key);
        if (object instanceof GLEMPOAArray) {
            return (GLEMPOAArray) object;
        }
        throw new GLEMPOAException("JSONObject[" + quote(key)
                + "] is not a GLEMPOAArray.");
    }


    /**
     * Obtenha o valor GLEMPOAObject associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return A GLEMPOAObject que é o valor.
     * @throws GLEMPOAException
     * se a chave não for encontrada ou se o valor não for um GLEMPOAObjeto.
     */
    
    public GLEMPOAObject getJSONObject(String key) throws GLEMPOAException {
        Object object = this.get(key);
        if (object instanceof GLEMPOAObject) {
            return (GLEMPOAObject) object;
        }
        throw new GLEMPOAException("JSONObject[" + quote(key)
                + "] is not a JSONObject.");
    }

    /**
     
* Obtenha o valor longo associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return O valor longo.
     * @throws GLEMPOAException
     * se a chave não for encontrada ou se o valor não puder ser convertido
     * para um longo
     */
    
    public long getLong(String key) throws GLEMPOAException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (Exception e) {
            throw new GLEMPOAException("JSONObject[" + quote(key)
                    + "] is not a long.");
        }
    }

    /**
     * Obtenha uma matriz de nomes de campo a partir de um GLEMPOAObjeto.
     *
     * @return Uma matriz de nomes de campos, ou null, se não houver nomes.
     */
    
    public static String[] getNames(GLEMPOAObject jo) {
        int length = jo.length();
        if (length == 0) {
            return null;
        }
        Iterator<String> iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext()) {
            names[i] = iterator.next();
            i += 1;
        }
        return names;
    }

    /**
    * Obter uma matriz de nomes de campo de um objeto.
     *
     * @return Uma matriz de nomes de campos, ou null, se não houver nomes.
     */
    
    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class klass = object.getClass();
        Field[] fields = klass.getFields();
        int length = fields.length;
        if (length == 0) {
            return null;
        }
        String[] names = new String[length];
        for (int i = 0; i < length; i += 1) {
            names[i] = fields[i].getName();
        }
        return names;
    }

    /**
     
* Obtenha a string associada a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return Uma string que é o valor.
     * @throws GLEMPOAException
     * se não houver valor de string para a chave.
     */
    public String getString(String key) throws GLEMPOAException {
        Object object = this.get(key);
        if (object instanceof String) {
            return (String) object;
        }
        throw new GLEMPOAException("JSONObject[" + quote(key) + "] not a string.");
    }

    /**
     * Determine se o GLEMPOAObject contém uma chave específica.
     *
     * tecla @param
     * Uma string de chave.
     * @retorne true se a chave existir no GLEMPOAObject.
     */
    public boolean has(String key) {
        return this.map.containsKey(key);
    }

    /**
    
* Incrementar uma propriedade de um GLEMPOAObject. Se não houver essa propriedade,
     * criar um com um valor de 1. Se houver tal propriedade, e se for
     * um Integer, Long, Double ou Float, então adicione um a ele.
     *
     * tecla @param
     * Uma string de chave.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se já existe uma propriedade com este nome que não é uma
     * Inteiro, Longo, Duplo ou Flutuante.
     */
    public GLEMPOAObject increment(String key) throws GLEMPOAException {
        Object value = this.opt(key);
        if (value == null) {
            this.put(key, 1);
        } else if (value instanceof Integer) {
            this.put(key, (Integer) value + 1);
        } else if (value instanceof Long) {
            this.put(key, (Long) value + 1);
        } else if (value instanceof Double) {
            this.put(key, (Double) value + 1);
        } else if (value instanceof Float) {
            this.put(key, (Float) value + 1);
        } else {
            throw new GLEMPOAException("Unable to increment [" + quote(key) + "].");
        }
        return this;
    }

    /**
    
     * Determine se o valor associado à chave é nulo ou se não há
     * valor.
     *
     * tecla @param
     * Uma string de chave.
     * @return true se não houver um valor associado à chave ou se o valor
         é o objeto GLEMPOAObject.NULL.
     */
    public boolean isNull(String key) {
        return GLEMPOAObject.NULL.equals(this.opt(key));
    }

    /**
    
     * Obtenha uma enumeração das chaves do GLEMPOAObject.
     */
    public Iterator<String> keys() {
        return this.keySet().iterator();
    }

    /**
     * Obtenha um conjunto de chaves do GLEMPOAObject.
     */
    public Set<String> keySet() {
        return this.map.keySet();
    }

    /**
    * Obtenha o número de chaves armazenadas no GLEMPOAObject.
     */
    public int length() {
        return this.map.size();
    }

    /**
     
* Produza um GLEMPOAArray contendo os nomes dos elementos deste
 GLEMPOAObjeto.
     */
    
    public GLEMPOAArray names() {
        GLEMPOAArray ja = new GLEMPOAArray();
        Iterator<String> keys = this.keys();
        while (keys.hasNext()) {
            ja.put(keys.next());
        }
        return ja.length() == 0 ? null : ja;
    }


    /**
     
     * Produza uma string de um número.
     *
     * @param number
     *            Um número
     * @return A String.
     * @throws GLEMPOAException
     * Se n é um número não finito.
     */
    
    public static String numberToString(Number number) throws GLEMPOAException {
        if (number == null) {
            throw new GLEMPOAException("Null pointer");
        }
        testValidity(number);



        String string = number.toString();
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    /**
     * Obtenha um valor opcional associado a uma chave.
     *
     * tecla @param
     * Uma string de chave.
     * @return Um objeto que é o valor, ou null, se não houver valor.
     */
    public Object opt(String key) {
        return key == null ? null : this.map.get(key);
    }

    /**
     
     * Obtenha um booleano opcional associado a uma chave. Ele retorna falso se houver
     * não é tal chave, ou se o valor não é booleano.TRUE ou a string "true".
     *
     * tecla @param
     * Uma string de chave.
     * @Retorna a verdade.
     */
    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    /**
     * Obtenha um booleano opcional associado a uma chave. Devolve o
     * defaultValue se não houver tal chave, ou se não for booleano ou
     * String "true" ou "false" (sem distinção entre maiúsculas e minúsculas).
     *
     * tecla @param
     * Uma string de chave.
     * @param defaultValue
     *            O padrão.
     * @return The truth.
     */

    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return this.getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    
    /**
     *Obter um duplo opcional associado a uma chave, ou NaN, se não houver
     * key ou se o seu valor não é um número. Se o valor for uma string, uma tentativa
     * será feito para avaliá-lo como um número.
     *
     * tecla @param
     * Uma string que é a chave.
     * @return Um objeto que é o valor.
     */
    
    public double optDouble(String key) {
        return this.optDouble(key, Double.NaN);
    }

    /**
     * Obter um duplo opcional associado a uma chave ou o defaultValue se
     * não existe tal chave ou se seu valor não é um número. Se o valor for um
     * string, uma tentativa será feita para avaliá-lo como um número.
     *
     * tecla @param
     * Uma string de chave.
     * @param defaultValue
     *            O padrão.
     * @return Um objeto que é o valor.
     */
    
    public double optDouble(String key, double defaultValue) {
        try {
            return this.getDouble(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
    * Obter um valor int opcional associado a uma chave ou zero se não houver
     * tal chave ou se o valor não é um número. Se o valor for uma string, um
     * tentativa será feita para avaliá-lo como um número.
     *
     * tecla @param
     * Uma string de chave.
     * @return Um objeto que é o valor.
     */
    
    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    /**
     * Obtenha um valor int opcional associado a uma chave ou o padrão, se houver
     * não existe tal chave ou se o valor não é um número. Se o valor é uma string,
     * Será feita uma tentativa para avaliá-lo como um número.
     *
     * tecla @param
     * Uma string de chave.
     * @param defaultValue
     *            O padrão.
     * @return Um objeto que é o valor.
     */
    
    public int optInt(String key, int defaultValue) {
        try {
            return this.getInt(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
    * Obtenha um GLEMPOAArray opcional associado a uma chave. Retorna null se houver
     * não existe tal chave, ou se seu valor não é um GLEMPOAArray.
     *
     * tecla @param
     * Uma string de chave.
     * @return A GLEMPOAArray que é o valor.
     */
    
    public GLEMPOAArray optGLEMPOAArray(String key) {
        Object o = this.opt(key);
        return o instanceof GLEMPOAArray ? (GLEMPOAArray) o : null;
    }


    /**
     * Obtenha um GLEMPOAObject opcional associado a uma chave. Ele retorna null se
 não existe tal chave, ou se seu valor não é um GLEMPOAObjeto.
     *
     * tecla @param
     * Uma string de chave.
     * @return A GLEMPOAObject que é o valor.
     */
    public GLEMPOAObject optJSONObject(String key) {
        Object object = this.opt(key);
        return object instanceof GLEMPOAObject ? (GLEMPOAObject) object : null;
    }

    /**
    * Obtenha um valor longo opcional associado a uma chave ou zero se não houver
     * tal chave ou se o valor não é um número. Se o valor for uma string, um
     * tentativa será feita para avaliá-lo como um número.
     *
     * tecla @param
     * Uma string de chave.
     * @return Um objeto que é o valor.
     */
    public long optLong(String key) {
        return this.optLong(key, 0);
    }

    /**
     * Obter um valor longo opcional associado a uma chave, ou o padrão, se houver
     * não existe tal chave ou se o valor não é um número. Se o valor é uma string,
     * Será feita uma tentativa para avaliá-lo como um número.
     *
     * tecla @param
     * Uma string de chave.
     * @param defaultValue
     *            O padrão.
     * @return Um objeto que é o valor.
     */
    
    public long optLong(String key, long defaultValue) {
        try {
            return this.getLong(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Obtenha uma string opcional associada a uma chave. Ele retorna uma string vazia
     * se não houver essa chave. Se o valor não for uma string e não for nulo,
     * então é convertido em uma string.
     *
     * tecla @param
     * Uma string de chave.
     * @return Uma string que é o valor.
     */
    public String optString(String key) {
        return this.optString(key, "");
    }

    /**
     * Obtenha uma string opcional associada a uma chave. Retorna o defaultValue
     * se não houver essa chave.
     *
     * tecla @param
     * Uma string de chave.
     * @param defaultValue
     *            O padrão.
     * @return Uma string que é o valor.
     */
    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        return NULL.equals(object) ? defaultValue : object.toString();
    }

    private void populateMap(Object bean) {
        Class klass = bean.getClass();



        boolean includeSuperClass = klass.getClassLoader() != null;

        Method[] methods = includeSuperClass ? klass.getMethods() : klass
                .getDeclaredMethods();
        for (int i = 0; i < methods.length; i += 1) {
            try {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if ("getClass".equals(name)
                                || "getDeclaringClass".equals(name)) {
                            key = "";
                        } else {
                            key = name.substring(3);
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }
                    if (key.length() > 0
                            && Character.isUpperCase(key.charAt(0))
                            && method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase()
                                    + key.substring(1);
                        }

                        Object result = method.invoke(bean, (Object[]) null);
                        if (result != null) {
                            this.map.put(key, wrap(result));
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    /**
    * Coloque um par chave / booleano no GLEMPOAObject.
     */
    public GLEMPOAObject put(String key, boolean value) throws GLEMPOAException {
        this.put(key, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    /**
    * Coloque um par chave / valor no GLEMPOAObjeto, onde o valor será um
 GLEMPOAArray que é produzido a partir de uma coleção.
     *
     * tecla @param
     * Uma string de chave.
     * valor @param
     * Um valor de coleção.
     * @retorne isso.
     * @throws GLEMPOAException
     */
    public GLEMPOAObject put(String key, Collection<Object> value) throws GLEMPOAException {
        this.put(key, new GLEMPOAArray(value));
        return this;
    }


    /**
     * Coloque um par de chaves / duplas no GLEMPOAObject.
     *
     * tecla @param
     * Uma string de chave.
     * valor @param
     * Um duplo que é o valor.
     * @retorne isso.
     * @throws GLEMPOAException
     * Se a chave for nula ou se o número for inválido.
     */
    public GLEMPOAObject put(String key, double value) throws GLEMPOAException {
        this.put(key, new Double(value));
        return this;
    }

    /**
     * Coloque um par chave / int no GLEMPOAObject.
     */
    public GLEMPOAObject put(String key, int value) throws GLEMPOAException {
        this.put(key, new Integer(value));
        return this;
    }

    /**
     * Coloque um par chave / longo no GLEMPOAObject.
    
     */
    public GLEMPOAObject put(String key, long value) throws GLEMPOAException {
        this.put(key, new Long(value));
        return this;
    }

    /**
     * Coloque um par chave / valor no GLEMPOAObjeto, onde o valor será um
 GLEMPOAObjeto que é produzido a partir de um mapa.
     */
    public GLEMPOAObject put(String key, Map<String, Object> value) throws GLEMPOAException {
        this.put(key, new GLEMPOAObject(value));
        return this;
    }

    /**
     * Coloque um par chave / valor no GLEMPOAObject. Se o valor for nulo, então o
 A chave será removida do GLEMPOAObject, se estiver presente
     */
    public GLEMPOAObject put(String key, Object value) throws GLEMPOAException {
        if (key == null) {
            throw new NullPointerException("Null key.");
        }
        if (value != null) {
            testValidity(value);
            this.map.put(key, value);
        } else {
            this.remove(key);
        }
        return this;
    }

    /**
    * Coloque um par chave / valor no GLEMPOAObjeto, mas somente se a chave e o valor
 ambos são não-nulos, e somente se ainda não houver um membro com esse
 nome.
     *
     * string de chave @param
     * objeto de valor @param
     * @retorne isso.
     * @throws GLEMPOAException
     * se a chave for duplicada
     */
    public GLEMPOAObject putOnce(String key, Object value) throws GLEMPOAException {
        if (key != null && value != null) {
            if (this.opt(key) != null) {
                throw new GLEMPOAException("Duplicate key \"" + key + "\"");
            }
            this.put(key, value);
        }
        return this;
    }

    /**
  * Coloque um par chave / valor no GLEMPOAObjeto, mas somente se a chave e o valor
 são ambos não nulos.
     */
    public GLEMPOAObject putOpt(String key, Object value) throws GLEMPOAException {
        if (key != null && value != null) {
            this.put(key, value);
        }
        return this;
    }

    /**
    
* Produza uma string entre aspas duplas com sequências de barra invertida em todos os
     * lugares certos. Uma barra invertida será inserida dentro de </, produzindo <\ /,
     * permitindo que o texto JSON seja entregue em HTML. No texto GLEMPOA, uma string não pode
     * contém um caractere de controle ou uma citação ou barra invertida sem escape.
     */
    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.length() == 0) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }

    /**
    
* Remova um nome e seu valor, se presente.   
     */
    public Object remove(String key) {
        return this.map.remove(key);
    }

    /**
     * Determine se dois JSONObjects são semelhantes.
     * Eles devem conter o mesmo conjunto de nomes que devem ser associados
     
     */
    public boolean similar(Object other) {
        try {
            if (!(other instanceof GLEMPOAObject)) {
                return false;
            }
            Set<String> set = this.keySet();
            if (!set.equals(((GLEMPOAObject)other).keySet())) {
                return false;
            }
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String name = iterator.next();
                Object valueThis = this.get(name);
                Object valueOther = ((GLEMPOAObject)other).get(name);
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
        } catch (Throwable exception) {
            return false;
        }
    }

    /**
     
* Tente converter uma string em um número, booleano ou nulo. Se a string
     * não pode ser convertido, retorne a string.
     *
     * @param string
     *            Uma linha.
     * @return Um valor JSON simples.
     */
    public static Object stringToValue(String string) {
        Double d;
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return GLEMPOAObject.NULL;
        }

  

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '-') {
            try {
                if (string.indexOf('.') > -1 || string.indexOf('e') > -1
                        || string.indexOf('E') > -1) {
                    d = Double.valueOf(string);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } else {
                    Long myLong = new Long(string);
                    if (string.equals(myLong.toString())) {
                        if (myLong == myLong.intValue()) {
                            return myLong.intValue();
                        } else {
                            return myLong;
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
        return string;
    }

    /**
    * Lance uma exceção se o objeto for um NaN ou um número infinito.
     *
     * @param o
     * O objeto para testar.
     * @throws GLEMPOAException
     * Se o é um número não finito.
     */
    public static void testValidity(Object o) throws GLEMPOAException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
                    throw new GLEMPOAException(
                            "GLEMPOA does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
                    throw new GLEMPOAException(
                            "GLEMPOA does not allow non-finite numbers.");
                }
            }
        }
    }

    /**
     
* Produza um GLEMPOAArray contendo os valores dos membros deste
 GLEMPOAObjeto.
    
     */
    public GLEMPOAArray toGLEMPOAArray(GLEMPOAArray names) throws GLEMPOAException {
        if (names == null || names.length() == 0) {
            return null;
        }
        GLEMPOAArray ja = new GLEMPOAArray();
        for (int i = 0; i < names.length(); i += 1) {
            ja.put(this.opt(names.getString(i)));
        }
        return ja;
    }

    /**
    
     * Faça um texto JSON deste GLEMPOAObjeto. Para compacidade, nenhum espaço em branco é
     * adicionado. Se isso não resultar em um texto JSON sintaticamente correto,
     * então null será retornado.
     
     */
    public String toString() {
        try {
            return this.toString(0);
        } catch (Exception e) {
            return null;
        }
    }

    public String toString(int indentFactor) throws GLEMPOAException {
        StringWriter w = new StringWriter();
        synchronized (w.getBuffer()) {
            return this.write(w, indentFactor, 0).toString();
        }
    }

    /**
     * Faça um texto JSON de um valor de objeto. Se o objeto tiver um
 método value.toJSONString (), então esse método será usado para produzir o
 Texto GLEMPOA. O método é necessário para produzir um texto estritamente conforme.
 Se o objeto não contiver um método toGLEMPOAString (que é o mais
 caso comum), então um texto será produzido por outros meios. Se o valor
 é uma matriz ou coleção, então um GLEMPOAArray será feito a partir dele e
 toGLEMPOAString método será chamado. Se o valor for um MAP, então um
 GLEMPOAObject será feito a partir dele e seu método toJSONString será
 chamado. Caso contrário, o método toString do valor será chamado eo
 resultado será citado.

 <p>
     * Aviso: este método assume que a estrutura de dados é acíclica.
     *
     * valor @param
     * O valor a ser serializado.
     * @retornar uma representação transmissível, exibível e exibível do
     * objeto, começando com <code> {</ code> & nbsp; <small> (esquerda
     * brace) </ small> e terminando com <code>} </ code> & nbsp; <small> (direita
     * cinta) </ small>.
     * @throws GLEMPOAException
     * Se o valor for ou contiver um número inválido.
     */
    
    public static String valueToString(Object value) throws GLEMPOAException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof GLEMPOAString) {
            Object object;
            try {
                object = ((GLEMPOAString) value).toJSONString();
            } catch (Exception e) {
                throw new GLEMPOAException(e);
            }
            if (object instanceof String) {
                return (String) object;
            }
            throw new GLEMPOAException("Bad value from toJSONString: " + object);
        }
        if (value instanceof Number) {
            return numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof GLEMPOAObject
                || value instanceof GLEMPOAArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            return new GLEMPOAObject((Map<String, Object>)value).toString();
        }
        if (value instanceof Collection) {
            return new GLEMPOAArray((Collection<Object>) value).toString();
        }
        if (value.getClass().isArray()) {
            return new GLEMPOAArray(value).toString();
        }
        return quote(value.toString());
    }

    /**
    
* Envolva um objeto, se necessário. Se o objeto for nulo, retorne o NULL
 objeto. Se for uma matriz ou coleção, agrupe-a em um GLEMPOAArray. Se for
 um mapa, envolva-o em um GLEMPOAObjeto. Se for uma propriedade padrão (Double,
 Cadeia de caracteres, et al), em seguida, já está embrulhado. Caso contrário, se vier de
 um dos pacotes java, transforme-o em uma string. E se isso não acontecer, tente
 para envolvê-lo em um GLEMPOAObject. Se o agrupamento falhar, será retornado nulo.
    
     */
    public static Object wrap(Object object) {
        try {
            if (object == null) {
                return NULL;
            }
            if (object instanceof GLEMPOAObject || object instanceof GLEMPOAArray
                    || NULL.equals(object) || object instanceof GLEMPOAString
                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String) {
                return object;
            }

            if (object instanceof Collection) {
                return new GLEMPOAArray((Collection<Object>) object);
            }
            if (object.getClass().isArray()) {
                return new GLEMPOAArray(object);
            }
            if (object instanceof Map) {
                return new GLEMPOAObject((Map<String, Object>) object);
            }
            Package objectPackage = object.getClass().getPackage();
        String objectPackageName = objectPackage != null ? objectPackage
                    .getName() : "";
            if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            return new GLEMPOAObject(object);
        } catch (Exception exception) {
            return null;
        }
    }

    /**
   
* Escreva o conteúdo do GLEMPOAObject como texto JSON para um escritor. Para
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

    static final Writer writeValue(Writer writer, Object value,
            int indentFactor, int indent) throws GLEMPOAException, IOException {
        if (value == null || value.equals(null)) {
            writer.write("null");
        } else if (value instanceof GLEMPOAObject) {
            ((GLEMPOAObject) value).write(writer, indentFactor, indent);
        } else if (value instanceof GLEMPOAArray) {
            ((GLEMPOAArray) value).write(writer, indentFactor, indent);
        } else if (value instanceof Map) {
            new GLEMPOAObject((Map<String, Object>) value).write(writer, indentFactor, indent);
        } else if (value instanceof Collection) {
            new GLEMPOAArray((Collection<Object>) value).write(writer, indentFactor,
                    indent);
        } else if (value.getClass().isArray()) {
            new GLEMPOAArray(value).write(writer, indentFactor, indent);
        } else if (value instanceof Number) {
            writer.write(numberToString((Number) value));
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof GLEMPOAString) {
            Object o;
            try {
                o = ((GLEMPOAString) value).toJSONString();
            } catch (Exception e) {
                throw new GLEMPOAException(e);
            }
            writer.write(o != null ? o.toString() : quote(value.toString()));
        } else {
            quote(value.toString(), writer);
        }
        return writer;
    }

    static final void indent(Writer writer, int indent) throws IOException {
        for (int i = 0; i < indent; i += 1) {
            writer.write(' ');
        }
    }

    /**
    
* Escreva o conteúdo do GLEMPOAObject como texto JSON para um escritor. Para
     * compacidade, nenhum espaço em branco é adicionado.
     * <p>
     * Aviso: este método assume que a estrutura de dados é acíclica.
     *
     * @return O escritor.
     * @throws GLEMPOAException
     */
    
    Writer write(Writer writer, int indentFactor, int indent)
            throws GLEMPOAException {
        try {
            boolean commanate = false;
            final int length = this.length();
            Iterator<String> keys = this.keys();
            writer.write('{');

            if (length == 1) {
                Object key = keys.next();
                writer.write(quote(key.toString()));
                writer.write(':');
                if (indentFactor > 0) {
                    writer.write(' ');
                }
                writeValue(writer, this.map.get(key), indentFactor, indent);
            } else if (length != 0) {
                final int newindent = indent + indentFactor;
                while (keys.hasNext()) {
                    Object key = keys.next();
                    if (commanate) {
                        writer.write(',');
                    }
                    if (indentFactor > 0) {
                        writer.write('\n');
                    }
                    indent(writer, newindent);
                    writer.write(quote(key.toString()));
                    writer.write(':');
                    if (indentFactor > 0) {
                        writer.write(' ');
                    }
                    writeValue(writer, this.map.get(key), indentFactor, newindent);
                    commanate = true;
                }
                if (indentFactor > 0) {
                    writer.write('\n');
                }
                indent(writer, indent);
            }
            writer.write('}');
            return writer;
        } catch (IOException exception) {
            throw new GLEMPOAException(exception);
        }
    }
}
