package glempoa;
/**
 
* A interface <code> GLEMPOAString </ code> permite um <code> toJSONString () </ code>
 * m�todo para que uma classe possa mudar o comportamento de
 * <code> JSONObject.toString () </ code>, <code> JSONArray.toString () </ code>,
 * e <code> JSONWriter.value (</ code> Object <code>) </ code>. o
 O m�todo <code> toJSONString </ code> ser� usado em vez do comportamento padr�o
 * de usar o m�todo <code> toString () </ code> do objeto e citando o resultado.
 */
public interface GLEMPOAString {
    /**
    
* O m�todo <code> toJSONString </ code> permite que uma classe produza seu pr�prio JSON
     * serializa��o.
     *
     * @return Um texto JSON estritamente sintaticamente correto.
     */
    public String toJSONString();
}
