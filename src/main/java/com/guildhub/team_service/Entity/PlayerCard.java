package com.guildhub.team_service.Entity;

import com.guildhub.team_service.Entity.enums.PlayerRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;

    private String fullName;

    private String avatarUrl;

    private int hltvRating;

    private int age;

    private int prizeMoney;

    // Убрали ManyToMany связь, оставили только в Team для избежания цикличности

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    private String facietUrl;

    private String country;

    @OneToMany(mappedBy = "playerCard", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Trophy> trophies = new ArrayList<>();

    @OneToMany(mappedBy = "playerCard", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Acheivement> achievements = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_card_stats_id")
    private PlayerCardStats playerCardStats;

    @Enumerated(EnumType.STRING)
    private PlayerRole role;
}
