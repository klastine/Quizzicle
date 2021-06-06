package net.packet;

import app.Player;
import app.game.QuizClient;
import app.game.QuizServer;
import javafx.application.Platform;
import net.InvalidSenderException;
import net.NetworkHandler;
import net.Side;

import java.io.IOException;

import static gui.Launcher.GUI;

/**
 * Negotiates with server about client's username choice
 */
public class SetUsername extends IMessage
{
    /**
     * Player's requested username
     */
    private final String username;
    /**
     * Whether the username is valid/taken
     */
    private boolean valid;

    /**
     * Create SetUsername message
     *
     * @param username Player's requested Username
     */
    public SetUsername(String username)
    {
        this.username = username;
        this.valid = true;
    }

    /**
     * Both sides
     * Client - Check whether submitted name was valid
     * Server - Check whether name received is valid/taken then send result
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        try
        {
            if (context.getSide() == Side.SERVER)
            {
                this.valid = QuizServer.IsUsernameValid(this.username);
                context.Send(this);
                if (this.valid)
                {
                    // Associate connection routine w/ player and team
                    Player newPlayer = QuizServer.RegisterPlayer(this.username, context);
                    QuizServer.OnPlayerAuthorized(newPlayer, context);
                }
            } else if (context.getSide() == Side.CLIENT)
            {
                if (this.valid)
                {
                    GUI.setPlayerName(this.username);
                } else
                {
                    Platform.runLater(() -> QuizClient.TrySetLocalUsername(GUI.userNamePopup(true)));  // Call username popup in new thread
                }
            }
        } catch (IOException | InvalidSenderException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Side getValidSide()
    {
        return Side.BOTH;
    }
}
