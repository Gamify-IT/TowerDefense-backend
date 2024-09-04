package de.unistuttgart.towerdefensebackend.data.mapper;

import de.unistuttgart.towerdefensebackend.data.Question;
import de.unistuttgart.towerdefensebackend.data.QuestionResult;
import de.unistuttgart.towerdefensebackend.data.QuestionResultDTO;
import de.unistuttgart.towerdefensebackend.repositories.QuestionRepository;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * This mapper maps the RoundResultDTO objects (used from external clients) and RoundResult objects (used from internal code)
 */
@Mapper(componentModel = "spring")
public abstract class QuestionResultMapper {

    @Autowired
    QuestionRepository questionRepository;

    public QuestionResult questionResultDTOToQuestionResult(final QuestionResultDTO questionResultDTO) {
        final Question question = questionRepository
            .findById(questionResultDTO.getQuestionUUId())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("There is no question with uuid %s.", questionResultDTO.getQuestionUUId())
                )
            );
        return new QuestionResult(question, questionResultDTO.getAnswer());
    }

    public List<QuestionResult> questionResultDTOsToQuestionResults(final List<QuestionResultDTO> questionResultDTOs) {
        final List<QuestionResult> questionResults = new ArrayList<>();
        questionResultDTOs.forEach(questionResultDTO ->
            questionResults.add(questionResultDTOToQuestionResult(questionResultDTO))
        );
        return questionResults;
    }
}
