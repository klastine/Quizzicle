package app;

import java.util.ArrayList;

/**
 * Multi-Answer question. Has multiple correct answers.
 *
 * @author obergman
 * @author klastine
 * @author gscott4
 */
public class MAQuestion extends Question
{
    /**
     * @param id             The SQL Table key
     * @param questionText   Text of the question
     * @param answers        list of answers
     * @param correctAnswers list of correct answers
     * @param difficulty     integer representation of the difficulty, 0-2
     * @param remoteMediaURL URL of the associated image, if there is one.
     */
    public MAQuestion(int id, String questionText, ArrayList<String> answers, ArrayList<String> correctAnswers, int difficulty, String remoteMediaURL)
    {
        super(id, questionText, answers, correctAnswers, difficulty, remoteMediaURL);
    }

    /**
     * always true
     */
    @Override
    public boolean hasMultipleAnswers()
    {
        return true;
    }
}
