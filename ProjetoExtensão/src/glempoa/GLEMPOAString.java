package glempoa;
/**
 
* A interface <code> GLEMPOAString </ code> permite um <code> toJSONString () </ code>
 * método para que uma classe possa mudar o comportamento de
 * <code> JSONObject.toString () </ code>, <code> JSONArray.toString () </ code>,
 * e <code> JSONWriter.value (</ code> Object <code>) </ code>. o
 O método <code> toJSONString </ code> será usado em vez do comportamento padrão
 * de usar o método <code> toString () </ code> do objeto e citando o resultado.
 */
public interface GLEMPOAString {
    /**
    
* O método <code> toJSONString </ code> permite que uma classe produza seu próprio JSON
     * serialização.
     *
     * @return Um texto JSON estritamente sintaticamente correto.
     */
    public String toJSONString();
}
