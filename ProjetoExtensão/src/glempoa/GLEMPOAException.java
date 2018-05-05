package glempoa;

/**
 * A GLEMPOAException é lançada  quando as coisas estão erradas.
 *
 * @author mathe
 * 
 */
public class GLEMPOAException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private Throwable cause;

    /**
     * Constrói um GLEMPOAException com uma mensagem explicativa.
     *
     * mensagem @param
     * Detalhe sobre o motivo da exceção.
     */
    public GLEMPOAException(String message) {
        super(message);
    }

    /**
    * Constrói um novo GLEMPOAException com a causa especificada.
     * @param causa A causa.
     */
    public GLEMPOAException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    /**
     * Retorna a causa desta exceção ou nula se a causa for inexistente
     * ou desconhecido.
     *
     * @retornar a causa desta exceção ou null se a causa for inexistente
     * ou desconhecido.
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
