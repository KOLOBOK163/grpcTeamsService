package com.guildhub.team_service.Repository;

import com.guildhub.team_service.Entity.PlayerCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PlayerCardRepository extends JpaRepository<PlayerCard, Long> {
    @Query("SELECT p FROM PlayerCard p LEFT JOIN FETCH p.trophies WHERE p.id = :id")
    Optional<PlayerCard> findByIdWithTrophies(Long id);

    @Query("SELECT p FROM PlayerCard p LEFT JOIN FETCH p.achievements WHERE p.id = :id")
    Optional<PlayerCard> findByIdWithAchievements(Long id);


}
