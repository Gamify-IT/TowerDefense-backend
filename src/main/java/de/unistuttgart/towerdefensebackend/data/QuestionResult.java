package de.unistuttgart.towerdefensebackend.data;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

/**
 * The QuestionResult class contains the question result related information.
 * A question result represents the result of a single question, i.e. the question and the answer given by the user.
 */
@Entity
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class QuestionResult {

    /**
     * A unique identifier for the question result.
     */
    @Id
    @GeneratedValue(generator = "uuid")
    UUID id;

    /**
     * The question text.
     */
    @NotNull(message = "question cannot be null")
    @ManyToOne
    @Valid
    Question question;

    /**
     * The text of the answer chosen by the user.
     */
    @NotNull(message = "answer cannot be null")
    @NotBlank(message = "answer cannot be blank")
    String answer;

    public QuestionResult(final Question question, final String answer) {
        this.question = question;
        this.answer = answer;
    }
}
