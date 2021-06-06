package net.packet;

import app.game.QuizClient;
import net.NetworkHandler;
import net.Side;

/**
 * Notifies the client of that their team is out of questions
 */
public class NotifyOutOfQuestions extends IMessage
{
    /**
     * Client-Side Only
     * out of question  \o/
     *
     * @param context NetworkHandler connection which received this message
     */
    @Override
    public void Handle(NetworkHandler context)
    {
        QuizClient.OnOutOfQuestions();
    }

    @Override
    public Side getValidSide()
    {
        return Side.SERVER;
    }
}
