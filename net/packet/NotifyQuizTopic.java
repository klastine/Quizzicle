package net.packet;

import net.NetworkHandler;
import net.Side;

import static gui.Launcher.GUI;

/**
 * Notify client of the current quiz name
 */
public class NotifyQuizTopic extends IMessage
{
    /**
     * Name of quiz
     */
    private final String quizTopic;

    /**
     * Server-Side only
     * Create NotifyQuizName message
     *
     * @param quizTopic Name of quiz to send to client
     */
    public NotifyQuizTopic(String quizTopic)
    {
        this.quizTopic = quizTopic;
    }

    /**
     * Client-Side Only
     * Sets current quiz for self
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        GUI.setTopicDisplay(this.quizTopic);
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
