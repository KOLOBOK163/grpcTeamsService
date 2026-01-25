package com.guildhub.team_service.Repository;

import com.guildhub.team_service.Entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT p FROM Team p LEFT JOIN FETCH p.achievements WHERE p.id = :id")
    Optional<Team> findByIdWithAchievements(Long id);

    @Query("SELECT p FROM Team p LEFT JOIN FETCH p.teamStats WHERE p.id = :id")
    Optional<Team> findByIdWithStats(Long id);

    Optional<Team> findByName(String name);
}
