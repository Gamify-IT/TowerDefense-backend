package de.unistuttgart.towerdefensebackend.repositories;

import de.unistuttgart.towerdefensebackend.data.Configuration;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {
}
