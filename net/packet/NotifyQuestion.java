package net.packet;

import app.Question;
import net.NetworkHandler;
import net.Side;

import static gui.Launcher.GUI;

/**
 * Loads a new question on the client
 */
public class NotifyQuestion extends IMessage
{
    /**
     * Question object
     */
    private final Question question;

    /**
     * Server-Side only
     * Create NotifyQuestion message
     *
     * @param question Question object to send to client
     */
    public NotifyQuestion(Question question)
    {
        this.question = question;
    }

    /**
     * Client-Side Only
     * Sets current question for self
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        GUI.loadQuestion(this.question);
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
