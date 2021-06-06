package net.packet;

import app.game.QuizClient;
import net.NetworkHandler;
import net.Side;

/**
 * Syncs time remaining with the client
 */
public class NotifyTimer extends IMessage
{
    /**
     * Remaining time
     */
    private final int timeRemaining;

    /**
     * Server-Side only
     * Create NotifyTimer message
     *
     * @param timeRemaining the time remaining in the game
     */
    public NotifyTimer(int timeRemaining)
    {
        this.timeRemaining = timeRemaining;
    }

    /**
     * Client-Side only
     * Updates info about this team on the client
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        QuizClient.OnTimerSync(this.timeRemaining);
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
