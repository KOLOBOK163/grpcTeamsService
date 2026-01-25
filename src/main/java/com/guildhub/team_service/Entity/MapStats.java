package com.guildhub.team_service.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "map_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mapName;

    private int wins;

    private int losses;

    private int winPercentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_stats_id")
    private TeamStats teamStats;
}
