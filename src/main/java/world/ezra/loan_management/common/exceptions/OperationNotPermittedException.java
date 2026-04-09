package world.ezra.loan_management.common.exceptions;

/**
 * @author Alex Kiburu
 */
public class OperationNotPermittedException extends RuntimeException{
    public OperationNotPermittedException(String msg) {
        super(msg);
    }
}
