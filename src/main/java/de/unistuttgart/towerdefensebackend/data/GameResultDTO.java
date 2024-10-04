package de.unistuttgart.towerdefensebackend.data;

import de.unistuttgart.towerdefensebackend.Constants;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * The GameResultDTO class contains all data that is saved after one tower defense game.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class GameResultDTO {

    /**
     * A unique identifier for the game result.
     */
    @Nullable
    private UUID id;

    /**
     * The total number of questions that were available.
     */
    @Min(
            value = Constants.MIN_QUESTION_COUNT,
            message = "cannot have less than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    @Max(
            value = Constants.MAX_QUESTION_COUNT,
            message = "cannot have more than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    private int questionCount;

    /**
     * The number of correctly answered questions.
     */
    @Min(
            value = Constants.MIN_QUESTION_COUNT,
            message = "cannot answer less than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    @Max(
            value = Constants.MAX_QUESTION_COUNT,
            message = "cannot answer more than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    private int correctQuestionsCount;

    /**
     * The number of incorrectly answered questions.
     */
    @Min(
            value = Constants.MIN_QUESTION_COUNT,
            message = "cannot answer less than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    @Max(
            value = Constants.MAX_QUESTION_COUNT,
            message = "cannot answer more than " + Constants.MIN_QUESTION_COUNT + " questions"
    )
    private int wrongQuestionsCount;

    @Min(value = Constants.MIN_POINTS, message = "cannot have less than " + Constants.MIN_POINTS + " points")
    @Max(value = Constants.MAX_POINTS, message = "cannot have more than " + Constants.MAX_POINTS + " points")
    private int points;

    /**
     * For the correctly answered questions: the text of the question and the selected answer text.
     */
    @Valid
    private List<QuestionResultDTO> correctAnsweredQuestions;

    /**
     * For the incorrectly answered questions: the text of the question and the selected answer text.
     */
    @Valid
    private List<QuestionResultDTO> wrongAnsweredQuestions;

    /**
     * UUID of the configuration that was used for this game.
     */
    @NotNull(message = "configurationAsUUID cannot be null")
    private UUID configurationAsUUID;

    private long score;
    private int rewards;

    public GameResultDTO(
            final int questionCount,
            final int correctKillsCount,
            final int wrongKillsCount,
            final int points,
            final List<QuestionResultDTO> correctAnsweredQuestions,
            final List<QuestionResultDTO> wrongAnsweredQuestions,
            final UUID configurationAsUUID,
            final long score,
            final int rewards
    ) {
        this.questionCount = questionCount;
        this.correctQuestionsCount = correctKillsCount;
        this.wrongQuestionsCount = wrongKillsCount;
        this.points = points;
        this.correctAnsweredQuestions = correctAnsweredQuestions;
        this.wrongAnsweredQuestions = wrongAnsweredQuestions;
        this.configurationAsUUID = configurationAsUUID;
        this.score = score;
        this.rewards = rewards;
    }

    public boolean equalsContent(final GameResultDTO other) {
        if (this == other) return true;
        if (other == null) return false;

        if (id != other.id) return false;
        if (questionCount != other.questionCount) return false;
        if (correctQuestionsCount != other.correctQuestionsCount) return false;
        if (wrongQuestionsCount != other.wrongQuestionsCount) return false;
        if (points != other.points) return false;
        if (!correctAnsweredQuestions.equals(other.correctAnsweredQuestions)) return false;
        if (!wrongAnsweredQuestions.equals(other.wrongAnsweredQuestions)) return false;
        return configurationAsUUID.equals(other.configurationAsUUID);
    }
}
