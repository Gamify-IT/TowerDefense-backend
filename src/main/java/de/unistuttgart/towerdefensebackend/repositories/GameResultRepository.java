package de.unistuttgart.towerdefensebackend.repositories;

import de.unistuttgart.towerdefensebackend.data.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {}
