package net.packet;

import net.InvalidSenderException;
import net.NetworkHandler;
import net.Side;

import java.io.IOException;
import java.io.Serializable;

/**
 * Generic message interface
 */
public abstract class IMessage implements Serializable
{
    /**
     * Handle packet on receiving end
     *
     * @param context NetworkHandler connection which received this message
     * @throws IOException            Stream could not be accessed or connection is closed
     * @throws InvalidSenderException Message was handled or sent on the wrong side
     */
    public abstract void Handle(NetworkHandler context) throws IOException, InvalidSenderException;

    /**
     * Gets the valid side for sending this message
     *
     * @return Valid sides this message can be sent from
     */
    public abstract Side getValidSide();
}
