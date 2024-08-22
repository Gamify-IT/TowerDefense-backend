package de.unistuttgart.towerdefensebackend.clients;

import de.unistuttgart.towerdefensebackend.data.OverworldResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This client's purpose is to send an OverworldResultDTO to the Overworld backend when a player finished a game.
 */
@FeignClient(value = "resultClient", url = "${overworld.url}/internal")
public interface ResultClient {
    /**
     * Submits the resultDTO to the Overworld backend
     *
     * @param accessToken the user's access token
     * @param resultDTO the player's submitted result, trimmed down to the data needed for the Overworld
     */
    @PostMapping("/submit-game-pass")
    void submit(@CookieValue("access_token") final String accessToken, final OverworldResultDTO resultDTO);
}
