package de.unistuttgart.towerdefensebackend.data;

import de.unistuttgart.towerdefensebackend.Constants;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

/**
 * The ConfigurationDTO class contains all data that has to be stored to configure a tower defense game.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class ConfigurationDTO {

    /**
     * A unique identifier for the configuration.
     */
    @Nullable
    UUID id;

    /**
     * The questions that are part of the configuration.
     */
    @Valid
    Set<QuestionDTO> questions;

    /**
     * The volume level that is set by the player.
     */
    Integer volumeLevel;

    public ConfigurationDTO(final Set<QuestionDTO> questions) {
        this.questions = questions;
    }

    public boolean equalsContent(final ConfigurationDTO other) {
        if (this == other) return true;
        if (other == null) return false;
        return Objects.equals(questions, other.questions);
    }
}
