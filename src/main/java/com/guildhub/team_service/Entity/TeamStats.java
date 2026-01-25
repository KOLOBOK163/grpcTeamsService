package com.guildhub.team_service.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Table(name = "team_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "team_id")
    private Long teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", insertable = false, updatable = false)
    private Team team;

    @OneToMany(mappedBy = "teamStats", cascade = CascadeType.ALL)
    private List<MapStats> mapStats = new ArrayList<>();

    @OneToMany(mappedBy = "teamStats", cascade = CascadeType.ALL)
    private List<MatchResult> recentMatches = new ArrayList<>();

    private int totalWins;

    private int totalLosses;

    @Column(name = "updated_at")
    private Instant updatedAt;

}
