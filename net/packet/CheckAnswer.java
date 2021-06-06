package net.packet;

import app.Team;
import app.game.QuizServer;
import net.InvalidSenderException;
import net.NetworkHandler;
import net.Side;

import java.io.IOException;
import java.util.ArrayList;

import static gui.Launcher.GUI;

/**
 * Queries whether an answer is correct
 */
public class CheckAnswer extends IMessage
{
    private static final long serialVersionUID = 42L;
    /**
     * Answers to be queried
     */
    private final ArrayList<String> answers;
    /**
     * Whether the answers are correct
     */
    private boolean correct;

    /**
     * Client-Side only
     * Create CheckAnswer message
     *
     * @param answers Answers to check
     */
    public CheckAnswer(ArrayList<String> answers)
    {
        this.answers = answers;
        this.correct = false;
    }

    /**
     * Both sides
     * Client - Check whether correct is true
     * Server - Check whether all answers are correct, then sends back the result
     *
     * @param context NetworkHandler connection which received this message
     * @throws IOException            Stream could not be accessed
     * @throws InvalidSenderException Message was handled or sent on the wrong side
     */
    @Override
    public void Handle(NetworkHandler context) throws IOException, InvalidSenderException
    {
        if (context.getSide() == Side.SERVER)
        {
            Team playerTeam = QuizServer.GetPlayer(context).GetTeam();
            if (playerTeam != null && playerTeam.currentQuestion != null)
            {
                this.correct = QuizServer.AreAnswersCorrect(this.answers, playerTeam.currentQuestion);
                context.Send(this);
                QuizServer.UpdateTeamProgFromCorrect(this.correct, playerTeam, playerTeam.currentQuestion);
            } else
            {
                throw new IOException("Something went wrong!");
            }
        } else if (context.getSide() == Side.CLIENT)
        {
            GUI.displayAnswerResult(this.answers, this.correct);
        }
    }

    @Override
    public Side getValidSide()
    {
        return Side.BOTH;
    }
}
