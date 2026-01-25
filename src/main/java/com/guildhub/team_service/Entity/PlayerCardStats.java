package com.guildhub.team_service.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "player_card_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCardStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int firepower;

    private int entrying;

    private int trading;

    private int opening;

    private int clutching;

    private int sniping;

    private int utility;

    @OneToOne(mappedBy = "playerCardStats")
    private PlayerCard playerCard;
}
