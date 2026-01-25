package com.guildhub.team_service.Entity;

import com.guildhub.team_service.Entity.enums.MatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Builder
@Table(name = "match_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "team_id")
    private Long teamId;

    private Long opponentId;

    private String opponentName;

    private String opponentLogoUrl;

    private String mapName;

    private int teamScore;

    private int opponentScore;

    private boolean isWin;

    @Enumerated(EnumType.STRING)
    private MatchStatus matchStatus;

    @Column(name = "match_date")
    private Instant matchDate;

    private String tournamentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_stats_id")
    private TeamStats teamStats;
}
