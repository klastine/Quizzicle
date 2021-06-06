package net.packet;

import app.game.QuizClient;
import net.NetworkHandler;
import net.Side;

import java.util.UUID;

/**
 * Notify a client of their UUID
 */
public class NotifyPlayer extends IMessage
{
    /**
     * Player UUID
     */
    private final UUID uuid;

    /**
     * Server-Side only
     * Create NotifyPlayer message
     *
     * @param uuid player's UUID
     */
    public NotifyPlayer(UUID uuid)
    {
        this.uuid = uuid;
    }

    /**
     * Client-Side Only
     * Sets the UUID for self
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        QuizClient.localPlayerUUID = this.uuid;
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
