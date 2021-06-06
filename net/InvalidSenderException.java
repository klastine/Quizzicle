package net;

/**
 * Exception for when a packet is sent/received then handled on the wrong side
 */
public class InvalidSenderException extends Exception
{
    /**
     * Create exception
     *
     * @param errorMessage Context message to print
     */
    public InvalidSenderException(String errorMessage)
    {
        super(errorMessage);
    }
}
