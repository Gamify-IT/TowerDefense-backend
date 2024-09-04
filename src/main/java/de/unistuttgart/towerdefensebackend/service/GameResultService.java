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
            resultClient.submit(accessToken, resultDTO);
            final List<QuestionResult> correctQuestions = questionResultMapper.questionResultDTOsToQuestionResults(
                gameResultDTO.getCorrectAnsweredQuestions()
            );
            final List<QuestionResult> wrongQuestions = questionResultMapper.questionResultDTOsToQuestionResults(
                gameResultDTO.getWrongAnsweredQuestions()
            );

            final int score = calculateResultScore(
                gameResultDTO.getCorrectQuestionsCount(),
                gameResultDTO.getQuestionCount()
            );
            final int rewards = 0;
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
        final int resultScore = calculateResultScore(
            gameResultDTO.getCorrectQuestionsCount(),
            gameResultDTO.getQuestionCount()
        );
        final int rewards = 0;
        return new @Valid OverworldResultDTO(
            "TOWER DEFENSE",
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
    private int calculateResultScore(final int correctAnswers, final int numberOfQuestions) {
        if (correctAnswers < 0 || numberOfQuestions < correctAnswers) {
            throw new IllegalArgumentException(
                String.format(
                    "correctAnswers (%s) or numberOfQuestions (%s) is not possible",
                    correctAnswers,
                    numberOfQuestions
                )
            );
        }
        return (int) ((100.0 * correctAnswers) / numberOfQuestions);
    }

    /**
     * TODO
     *
     * @param resultScore
     * @return
     */
    private int calculateRewards(final int resultScore) {
        return 0;
    }
}
