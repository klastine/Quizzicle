package app;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Parent class of all Question classes. Handles all information and interaction associated with a single trivia question.
 *
 * @author obergman
 * @author klastine
 * @author gscott4
 * @see SAQuestion Single-answer question
 * @see MAQuestion Multi-answer question
 * @see ImageQuestion Image question
 */
public abstract class Question implements Serializable
{
    /**
     * Text of the question
     */
    private final String questionText;
    /**
     * list of answers
     */
    private final ArrayList<String> answers;
    /**
     * list of correct answers
     */
    private final transient ArrayList<String> correctAnswers;
    /**
     * integer representation of the difficulty, 0-2
     */
    private final int difficulty;
    /**
     * The SQL Table key
     */
    private final int id;
    /**
     * URL of the associated image, if there is one.
     */
    private final String remoteMediaURL;

    /**
     * @param id             The SQL Table key
     * @param questionText   Text of the question
     * @param answers        list of answers
     * @param correctAnswers list of correct answers
     * @param difficulty     integer representation of the difficulty, 0-2
     * @param remoteMediaURL URL of the associated image, if there is one.
     */
    public Question(int id, String questionText, ArrayList<String> answers, ArrayList<String> correctAnswers, int difficulty, String remoteMediaURL)
    {
        this.id = id;
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswers = correctAnswers;
        this.difficulty = difficulty;
        this.remoteMediaURL = remoteMediaURL;
    }

    /**
     * returns the text of the question
     *
     * @return the text of the question
     */
    public String getQuestionText()
    {
        return questionText;
    }

    /**
     * returns the list of answers
     *
     * @return the list of answers
     */
    public ArrayList<String> getAnswers()
    {
        return answers;
    }

    /**
     * returns the list of correct answers
     *
     * @return the list of correct answers
     */
    public ArrayList<String> getCorrectAnswers()
    {
        return correctAnswers;
    }

    /**
     * returns the url of the associated image, if there is one
     *
     * @return the url of the associated image
     */
    public String getRemoteMediaURL()
    {
        return remoteMediaURL;
    }

    /**
     * Returns an int representing the difficulty
     *
     * @return an int representing the difficulty
     */
    public int getDifficulty()
    {
        return difficulty;
    }

    /**
     * Returns the SQL table ID of the question.
     *
     * @return the SQL table ID of the question.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Does the question have multiple true answers?
     *
     * @return True for Multi-answer questions, false otherwise.
     */
    public abstract boolean hasMultipleAnswers();

}
