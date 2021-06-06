package net.packet;

import app.GameManager;
import app.GameState;
import net.InvalidSenderException;
import net.NetworkHandler;
import net.Side;

import java.io.IOException;

/**
 * Notifies the client of the current game state
 * e.g. Main menu, in-game, end of game leaderboards
 */
public class NotifyGameState extends IMessage
{
    /**
     * The gamestate in this context
     */
    private final GameState gameState;

    /**
     * Server-Side only
     * Create NotifyGameState message
     *
     * @param gameState current game state
     */
    public NotifyGameState(GameState gameState)
    {
        this.gameState = gameState;
    }

    /**
     * Client-Side Only
     * Sets the current game state
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context) throws IOException, InvalidSenderException
    {
        GameManager.SetGameState(this.gameState);
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
