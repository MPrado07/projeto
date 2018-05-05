package glempoa;

/**
 * A GLEMPOAException � lan�ada  quando as coisas est�o erradas.
 *
 * @author mathe
 * 
 */
public class GLEMPOAException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private Throwable cause;

    /**
     * Constr�i um GLEMPOAException com uma mensagem explicativa.
     *
     * mensagem @param
     * Detalhe sobre o motivo da exce��o.
     */
    public GLEMPOAException(String message) {
        super(message);
    }

    /**
    * Constr�i um novo GLEMPOAException com a causa especificada.
     * @param causa A causa.
     */
    public GLEMPOAException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    /**
     * Retorna a causa desta exce��o ou nula se a causa for inexistente
     * ou desconhecido.
     *
     * @retornar a causa desta exce��o ou null se a causa for inexistente
     * ou desconhecido.
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
