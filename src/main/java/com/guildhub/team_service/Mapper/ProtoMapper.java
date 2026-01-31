package com.guildhub.team_service.Mapper;

import com.google.protobuf.Timestamp;
import com.guildhub.teams_service.grpc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProtoMapper {

    private final JsonMapper.Builder builder;

    public Trophy toProtoTrophy(com.guildhub.team_service.Entity.Trophy trophy) {
        Trophy.Builder builder = Trophy.newBuilder()
                .setId(trophy.getId())
                .setName(trophy.getName())
                .setLogoUrl(trophy.getLogoUrl())
                .setCreatedAt(Timestamp.newBuilder()
                        .setSeconds(trophy.getCreatedAt().getEpochSecond())
                        .setNanos(trophy.getCreatedAt().getNano())
                        .build());
        if (trophy.getUpdatedAt() != null) {
            builder.setUpdatedAt(Timestamp.newBuilder()
                            .setSeconds(trophy.getUpdatedAt().getEpochSecond())
                            .setNanos(trophy.getUpdatedAt().getNano()))
                    .build();
        }

        return builder.build();
    }

    public Achievement toProtoAchievement(com.guildhub.team_service.Entity.Achievement achievement) {
        Achievement.Builder builder = Achievement.newBuilder()
                .setId(achievement.getId())
                .setName(achievement.getName())
                .setPlace(achievement.getPlace())
                .setCreatedAt(Timestamp.newBuilder()
                        .setSeconds(achievement.getCreatedAt().getEpochSecond())
                        .setNanos(achievement.getCreatedAt().getNano())
                        .build());
        if (achievement.getUpdatedAt() != null) {
            builder.setUpdatedAt(Timestamp.newBuilder()
                            .setSeconds(achievement.getUpdatedAt().getEpochSecond())
                            .setNanos(achievement.getUpdatedAt().getNano()))
                    .build();
        }

        return builder.build();
    }


    public Team toProtoTeam(com.guildhub.team_service.Entity.Team team) {
        Team.Builder builder = Team.newBuilder()
                .setId(team.getId())
                .setName(team.getName())
                .setLogoUrl(team.getLogoUrl())
                .setPrizes(team.getPrizes())
                .setHltvRating(team.getHltvRating())
                .setCountry(team.getCountry());
        if(team.getCreatedAt() != null)
        {
            builder.setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(team.getCreatedAt().getEpochSecond())
                    .setNanos(team.getCreatedAt().getNano())
                    .build());
        }

        if(team.getUpdatedAt() != null)
        {
            builder.setUpdatedAt(Timestamp.newBuilder()
                    .setSeconds(team.getUpdatedAt().getEpochSecond())
                    .setNanos(team.getUpdatedAt().getNano())
                    .build());
        }

        if(team.getAchievements() != null)
        {
            team.getAchievements().forEach(achievement -> builder.addAchievements(toProtoAchievement(achievement)));
        }

        if(team.getTeamStats() != null && !team.getTeamStats().isEmpty()){
            builder.setStats(toProtoTeamStats(team.getTeamStats().getFirst()));
        }

        if(team.getPlayers() != null)
        {
            team.getPlayers().forEach(playerCard -> builder.addPlayers(toProtoPlayerCardLight(playerCard)));
        }

        return builder.build();
    }

    public TeamLight toProtoTeamLight(com.guildhub.team_service.Entity.Team team) {
        TeamLight.Builder builder = TeamLight.newBuilder()
                .setId(team.getId())
                .setName(team.getName())
                .setLogoUrl(team.getLogoUrl())
                .setCountry(team.getCountry());

        if (team.getPlayers() != null) {
            team.getPlayers().forEach(playerCard -> builder.addPlayers(toProtoPlayerCardLight(playerCard)));
        }

        return builder.build();
    }

    public PlayerCard toProtoPlayerCard(com.guildhub.team_service.Entity.PlayerCard playerCard) {
        PlayerCard.Builder builder = PlayerCard.newBuilder()
                .setId(playerCard.getId())
                .setNickname(playerCard.getNickName())
                .setFullName(playerCard.getFullName())
                .setAvatarUrl(playerCard.getAvatarUrl())
                .setHltvRating(playerCard.getHltvRating())
                .setAge(playerCard.getAge())
                .setPrizeMoney(playerCard.getPrizeMoney())
                .setFaceitUrl(playerCard.getFacietUrl())
                .setCountry(playerCard.getCountry())
                .setRole(toProtoPlayerRole(playerCard.getRole()));

        if(playerCard.getCreatedAt() != null)
        {
            builder.setCreatedAt(Timestamp.newBuilder()
                    .setSeconds(playerCard.getCreatedAt().getEpochSecond())
                    .setNanos(playerCard.getCreatedAt().getNano())
                    .build());
        }

        if(playerCard.getUpdatedAt() != null)
        {
            builder.setUpdatedAt(Timestamp.newBuilder()
                    .setSeconds(playerCard.getCreatedAt().getEpochSecond())
                    .setNanos(playerCard.getCreatedAt().getNano())
                    .build());
        }

        if(playerCard.getPlayerCardStats() != null)
        {
            builder.setStats(toProtoPlayerCardStats(playerCard.getPlayerCardStats()));
        }

        if(playerCard.getTrophies() != null && !playerCard.getTrophies().isEmpty()){
            playerCard.getTrophies().forEach(trophy -> builder.addTrophies(toProtoTrophy(trophy)));
        }

        if(playerCard.getAchievements() != null && !playerCard.getAchievements().isEmpty()) {
            playerCard.getAchievements().forEach(achievement -> builder.addAchievements(toProtoAchievement(achievement)));
        }

        if(playerCard.getTeams() != null)
        {
            playerCard.getTeams().forEach(team -> builder.addTeams(toProtoTeamLight(team)));
        }

        return builder.build();
    }

    public PlayerCardLight toProtoPlayerCardLight(com.guildhub.team_service.Entity.PlayerCard player) {
        return PlayerCardLight.newBuilder()
                .setId(player.getId())
                .setNickname(player.getNickName())
                .setFullName(player.getFullName())
                .setAvatarUrl(player.getAvatarUrl())
                .setRole(toProtoPlayerRole(player.getRole()))
                .setCountry(player.getCountry())
                .build();
    }

    public PlayerCardStats toProtoPlayerCardStats(com.guildhub.team_service.Entity.PlayerCardStats playerCardStats) {
        return PlayerCardStats.newBuilder()
                .setId(playerCardStats.getId())
                .setFirepower(playerCardStats.getFirepower())
                .setEntrying(playerCardStats.getEntrying())
                .setTrading(playerCardStats.getTrading())
                .setOpening(playerCardStats.getOpening())
                .setClutching(playerCardStats.getClutching())
                .setSniping(playerCardStats.getSniping())
                .setUtility(playerCardStats.getUtility())
                .build();
    }

    public TeamStats toProtoTeamStats(com.guildhub.team_service.Entity.TeamStats teamStats) {
        TeamStats.Builder builder = TeamStats.newBuilder()
                .setId(teamStats.getId())
                .setTotalWins(teamStats.getTotalWins())
                .setTotalLosses(teamStats.getTotalLosses());

        if(teamStats.getMapStats() != null){
            teamStats.getMapStats().forEach(mapStats -> builder.addMapStats(toProtoMapStats(mapStats)));
        }

        if(teamStats.getRecentMatches() != null)
        {
            teamStats.getRecentMatches().forEach(matchResult -> builder.addRecentMatches(toProtoResentMatches(matchResult)));
        }

        if(teamStats.getUpdatedAt() != null){
            builder.setUpdatedAt(Timestamp.newBuilder()
                    .setSeconds(teamStats.getUpdatedAt().getEpochSecond())
                    .setNanos(teamStats.getUpdatedAt().getNano())
                    .build());
        }

        return builder.build();
    }

    public MapStats toProtoMapStats(com.guildhub.team_service.Entity.MapStats mapStats){
        MapStats.Builder builder = MapStats.newBuilder()
                .setId(mapStats.getId())
                .setMapName(mapStats.getMapName())
                .setWins(mapStats.getWins())
                .setLosses(mapStats.getLosses())
                .setWinPercentage(mapStats.getWinPercentage());

        return builder.build();
    }

    public MatchResult toProtoResentMatches(com.guildhub.team_service.Entity.MatchResult matchResult){
        MatchResult.Builder builder = MatchResult.newBuilder()
                .setId(matchResult.getId())
                .setOpponentId(matchResult.getOpponentId())
                .setOpponentName(matchResult.getOpponentName())
                .setOpponentLogoUrl(matchResult.getOpponentLogoUrl())
                .setMapName(matchResult.getMapName())
                .setTeamScore(matchResult.getTeamScore())
                .setOpponentScore(matchResult.getOpponentScore())
                .setIsWin(matchResult.isWin())
                .setStatus(toProtoMatchStatus(matchResult.getMatchStatus()))
                .setTournamentName(matchResult.getTournamentName());
        if(matchResult.getMatchDate() != null)
        {
            builder.setMatchDate(Timestamp.newBuilder()
                    .setSeconds(matchResult.getMatchDate().getEpochSecond())
                    .setNanos(matchResult.getMatchDate().getNano())
                    .build());
        }

        return builder.build();
    }

    public PlayerRole toProtoPlayerRole(com.guildhub.team_service.Entity.enums.PlayerRole role) {
        if (role == null) {
            return PlayerRole.PLAYER;
        }

        switch (role) {
            case PLAYER:
                return PlayerRole.PLAYER;
            case TEAM_LEADER:
                return PlayerRole.TEAM_LEADER;
            case COACH:
                return PlayerRole.COACH;
            default:
                return PlayerRole.PLAYER;
        }
    }

    public com.guildhub.team_service.Entity.enums.PlayerRole fromProtoPlayerRole(PlayerRole role) {
        if (role == null) {
            return com.guildhub.team_service.Entity.enums.PlayerRole.PLAYER;
        }

        switch (role) {
            case PLAYER:
                return com.guildhub.team_service.Entity.enums.PlayerRole.PLAYER;
            case TEAM_LEADER:
                return com.guildhub.team_service.Entity.enums.PlayerRole.TEAM_LEADER;
            case COACH:
                return com.guildhub.team_service.Entity.enums.PlayerRole.COACH;
            default:
                return com.guildhub.team_service.Entity.enums.PlayerRole.PLAYER;
        }
    }

    public MatchStatus toProtoMatchStatus(com.guildhub.team_service.Entity.enums.MatchStatus status) {
        if (status == null) {
            return MatchStatus.PENDING;
        }

        switch (status) {
            case PENDING:
                return MatchStatus.PENDING;
            case IN_PROGRESS:
                return MatchStatus.IN_PROGRESS;
            case COMPLETED:
                return MatchStatus.COMPLETED;
            case CANCELLED:
                return MatchStatus.CANCELLED;
            default:
                return MatchStatus.PENDING;
        }
    }

    public com.guildhub.team_service.Entity.enums.MatchStatus fromProtoMatchStatus(MatchStatus status) {
        if (status == null) {
            return com.guildhub.team_service.Entity.enums.MatchStatus.PENDING;
        }

        switch (status) {
            case PENDING:
                return com.guildhub.team_service.Entity.enums.MatchStatus.PENDING;
            case IN_PROGRESS:
                return com.guildhub.team_service.Entity.enums.MatchStatus.IN_PROGRESS;
            case COMPLETED:
                return com.guildhub.team_service.Entity.enums.MatchStatus.COMPLETED;
            case CANCELLED:
                return com.guildhub.team_service.Entity.enums.MatchStatus.CANCELLED;
            default:
                return com.guildhub.team_service.Entity.enums.MatchStatus.PENDING;
        }
    }
}
