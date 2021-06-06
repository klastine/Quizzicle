package app;

import java.util.ArrayList;

/**
 * Single-Answer question with image associated.
 *
 * @author obergman
 * @author klastine
 * @author gscott4
 */
public class ImageQuestion extends Question
{
    /**
     * @param id             The SQL Table key
     * @param questionText   Text of the question
     * @param answers        list of answers
     * @param correctAnswers list of correct answers
     * @param difficulty     integer representation of the difficulty, 0-2
     * @param remoteMediaURL URL of the associated image
     */
    public ImageQuestion(int id, String questionText, ArrayList<String> answers, ArrayList<String> correctAnswers, int difficulty, String remoteMediaURL)
    {
        super(id, questionText, answers, correctAnswers, difficulty, remoteMediaURL);
    }

    /**
     * always false
     */
    @Override
    public boolean hasMultipleAnswers()
    {
        return false;
    }
}
