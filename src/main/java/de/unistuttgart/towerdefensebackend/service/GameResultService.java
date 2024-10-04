package de.unistuttgart.towerdefensebackend.service;

import de.unistuttgart.towerdefensebackend.clients.ResultClient;
import de.unistuttgart.towerdefensebackend.data.GameResult;
import de.unistuttgart.towerdefensebackend.data.GameResultDTO;
import de.unistuttgart.towerdefensebackend.data.OverworldResultDTO;
import de.unistuttgart.towerdefensebackend.data.QuestionResult;
import de.unistuttgart.towerdefensebackend.data.mapper.QuestionResultMapper;
import de.unistuttgart.towerdefensebackend.repositories.GameResultRepository;
import feign.FeignException;

import java.util.List;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * This service handles the logic for the GameResultController class
 */
@Service
@Slf4j
@Transactional
public class GameResultService {

    @Autowired
    ResultClient resultClient;

    @Autowired
    GameResultRepository gameResultRepository;

    @Autowired
    QuestionResultMapper questionResultMapper;

    private int hundredScoreCount = 0;

    /**
     * Casts a GameResultDTO to GameResult and saves it in the database
     *
     * @param gameResultDTO extern gameResultDTO
     * @param userId        id of the user
     * @param accessToken   accessToken of the user
     * @throws IllegalArgumentException if at least one of the arguments is null
     */
    public void saveGameResult(
            final @Valid GameResultDTO gameResultDTO,
            final String userId,
            final String accessToken
    ) {
        if (gameResultDTO == null || userId == null || accessToken == null) {
            throw new IllegalArgumentException("gameResultDTO or userId or accessToken is null");
        }
        final OverworldResultDTO resultDTO = createOverworldResult(gameResultDTO, userId);
        try {
            resultClient.submit(resultDTO, accessToken);
            final List<QuestionResult> correctQuestions = questionResultMapper.questionResultDTOsToQuestionResults(
                    gameResultDTO.getCorrectAnsweredQuestions()
            );
            final List<QuestionResult> wrongQuestions = questionResultMapper.questionResultDTOsToQuestionResults(
                    gameResultDTO.getWrongAnsweredQuestions()
            );

            final long score = calculateResultScore(gameResultDTO.getCorrectQuestionsCount(), gameResultDTO.getQuestionCount());
            final int rewards = calculateRewards(score);
            final GameResult result = new @Valid GameResult(
                    gameResultDTO.getQuestionCount(),
                    gameResultDTO.getCorrectQuestionsCount(),
                    gameResultDTO.getWrongQuestionsCount(),
                    gameResultDTO.getPoints(),
                    correctQuestions,
                    wrongQuestions,
                    gameResultDTO.getConfigurationAsUUID(),
                    userId,
                    score,
                    rewards
            );
            gameResultDTO.setScore(score);
            gameResultDTO.setRewards(rewards);

            gameResultRepository.save(result);
        } catch (final FeignException.BadGateway badGateway) {
            final String warning =
                    "The Overworld backend is currently not available. The result was NOT saved. Please try again later";
            log.error(warning + badGateway);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, warning);
        } catch (final FeignException.NotFound notFound) {
            final String warning = "The result could not be saved. Unknown User";
            log.error(warning + notFound);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, warning);
        }
    }

    /**
     * Create an OverworldResultDTO
     *
     * @param gameResultDTO contains all game related data
     * @param userId        id of the player
     * @return OverworldResultDTO
     */
    private OverworldResultDTO createOverworldResult(final @Valid GameResultDTO gameResultDTO, final String userId) {
        final long resultScore = calculateResultScore(
                gameResultDTO.getCorrectQuestionsCount(),
                gameResultDTO.getQuestionCount()
        );
        final int rewards = calculateRewards(resultScore);
        return new @Valid OverworldResultDTO(
                "TOWERDEFENSE",
                gameResultDTO.getConfigurationAsUUID(),
                resultScore,
                userId,
                rewards
        );
    }

    /**
     * Calculates the score a player made
     *
     * @param correctAnswers    correct answer count
     * @param numberOfQuestions available question count
     * @return score as int in per cent
     * @throws IllegalArgumentException if correctAnswers < 0 || numberOfQuestions < correctAnswers
     */
    private long calculateResultScore(final int correctAnswers, final int numberOfQuestions) {
        if (correctAnswers < 0 || numberOfQuestions < correctAnswers) {
            throw new IllegalArgumentException(
                    String.format(
                            "correctAnswers (%s) or numberOfQuestions (%s) is not possible",
                            correctAnswers,
                            numberOfQuestions
                    )
            );
        }
        return (long) ((100.0 * correctAnswers) / numberOfQuestions);
    }

    /**
     * Calculates the player rewards based on the result score achieved in the minigame.
     * Note that after successfully completing the game more than three times, the player rewards will decrease.
     * @param resultScore result score of the minigame
     * @return rewards for the minigame session
     */
    private int calculateRewards(final long resultScore) {
        if (resultScore < 0) {
            throw new IllegalArgumentException("Result score cannot be less than zero");
        }
        if (resultScore == 100 && hundredScoreCount < 3) {
            hundredScoreCount++;
            return 10;
        }
        else if (resultScore == 100) {
            return 5;
        }
        return (int) resultScore / 10;
    }
}
