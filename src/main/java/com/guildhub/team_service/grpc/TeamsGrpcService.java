package com.guildhub.team_service.grpc;

import com.google.protobuf.Empty;
import com.guildhub.team_service.Entity.*;
import com.guildhub.team_service.Entity.PlayerCard;
import com.guildhub.team_service.Entity.Team;
import com.guildhub.team_service.Mapper.ProtoMapper;
import com.guildhub.team_service.Repository.PlayerCardRepository;
import com.guildhub.team_service.Repository.TeamRepository;
import com.guildhub.teams_service.grpc.*;
import com.guildhub.teams_service.grpc.MatchResult;
import com.guildhub.teams_service.grpc.TeamStats;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityExistsException;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class TeamsGrpcService extends TeamServiceGrpc.TeamServiceImplBase {

    private final PlayerCardRepository playerCardRepository;

    private final TeamRepository teamRepository;

    private final ProtoMapper protoMapper;

    public TeamsGrpcService(PlayerCardRepository playerCardRepository, TeamRepository teamRepository
           , ProtoMapper protoMapper) {
        this.playerCardRepository = playerCardRepository;
        this.teamRepository = teamRepository;
        this.protoMapper = protoMapper;
    }

    @Override
    public void getPlayerTrophies(GetPlayerRequest request, StreamObserver<TrophiesList> responseObserver) {
        try{
            PlayerCard player = playerCardRepository.findByIdWithTrophies(request.getPlayerId())
                    .orElseThrow(() ->
                            Status.NOT_FOUND
                                    .withDescription("Player with id " + request.getPlayerId() + " not found")
                                    .asRuntimeException()
                    );
            TrophiesList.Builder listBuilder = TrophiesList.newBuilder();

            if(player.getTrophies() != null)
            {
                player.getTrophies().forEach(trophy -> listBuilder.addTrophies(protoMapper.toProtoTrophy(trophy)));
            }

            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        }catch (io.grpc.StatusRuntimeException e)
        {
            responseObserver.onError(e);
        }catch (Exception e)
        {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error retrieving player trophies: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getPlayerAchievements(GetPlayerRequest request, StreamObserver<AchievementsList> responseObserver) {
        try{
            PlayerCard player = playerCardRepository.findByIdWithAchievements(request.getPlayerId())
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Player with id " + request.getPlayerId() + " not found")
                            .asRuntimeException());

            AchievementsList.Builder listBuilder = AchievementsList.newBuilder();

            if(player.getAchievements() != null)
            {
                player.getAchievements().forEach(achievement -> listBuilder.addAchievements(protoMapper.toProtoAchievement(achievement)));
            }

            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        }catch (io.grpc.StatusRuntimeException e)
        {
            responseObserver.onError(e);
        }catch (Exception e)
        {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error retrieving player trophies: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getTeamAchievements(GetTeamRequest request, StreamObserver<AchievementsList> responseObserver) {
        try{
            Team team = teamRepository.findByIdWithAchievements(request.getTeamId())
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Team with id " + request.getTeamId() + "not found")
                            .asRuntimeException());

            AchievementsList.Builder listBuilder = AchievementsList.newBuilder();

            if(team.getAchievements() != null){
                team.getAchievements().forEach(achievement -> listBuilder.addAchievements(protoMapper.toProtoAchievement(achievement)));
            }

            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        }catch (io.grpc.StatusRuntimeException e)
        {
            responseObserver.onError(e);
        }catch (Exception e)
        {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error retrieving player trophies: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getTeamStats(GetTeamRequest request, StreamObserver<TeamStats> responseObserver) {
        try {
            Team team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> Status.NOT_FOUND
                            .withDescription("Team with id " + request.getTeamId() + " not found")
                            .asRuntimeException());

            com.guildhub.team_service.Entity.TeamStats stats = team.getTeamStats().getFirst();

            TeamStats protoStats = protoMapper.toProtoTeamStats(stats);

            responseObserver.onNext(protoStats);
            responseObserver.onCompleted();
        }catch (io.grpc.StatusRuntimeException e)
        {
            responseObserver.onError(e);
        }catch (Exception e)
        {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error retrieving player trophies: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void createTeam(CreateTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        String name = request.getName();

        if(teamRepository.findByName(name).isPresent())
        {
            throw new EntityExistsException("Team with name " + name + " already exists");
        }

        ImageUpload logo = ImageUpload.newBuilder()
                .setImageData(request.getLogo().getImageData())
                .setImageFilename(request.getLogo().getImageFilename())
                .setImageContentType(request.getLogo().getImageContentType())
                .build();

        Team team = Team.builder()
                .name(name)
                .country(request.getCountry())
                .logoUrl(String.valueOf(logo)).build();

        Team savedTeam = teamRepository.save(team);

        com.guildhub.teams_service.grpc.Team protoTeam = protoMapper.toProtoTeam(savedTeam);

        responseObserver.onNext(protoTeam);
        responseObserver.onCompleted();
    }

    @Override
    public void getTeam(GetTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        super.getTeam(request, responseObserver);
    }

    @Override
    public void updateTeam(UpdateTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        super.updateTeam(request, responseObserver);
    }

    @Override
    public void deleteTeam(GetTeamRequest request, StreamObserver<Empty> responseObserver) {
        super.deleteTeam(request, responseObserver);
    }

    @Override
    public void listTeams(ListTeamsRequest request, StreamObserver<TeamsList> responseObserver) {
        super.listTeams(request, responseObserver);
    }

    @Override
    public void createPlayer(CreatePlayerRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        super.createPlayer(request, responseObserver);
    }

    @Override
    public void getPlayer(GetPlayerRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        super.getPlayer(request, responseObserver);
    }

    @Override
    public void updatePlayer(UpdatePlayerRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        super.updatePlayer(request, responseObserver);
    }

    @Override
    public void updatePlayerAvatar(UpdatePlayerAvatarRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        super.updatePlayerAvatar(request, responseObserver);
    }

    @Override
    public void deletePlayer(GetPlayerRequest request, StreamObserver<Empty> responseObserver) {
        super.deletePlayer(request, responseObserver);
    }

    @Override
    public void addPlayerToTeam(AddPlayerToTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        super.addPlayerToTeam(request, responseObserver);
    }

    @Override
    public void removePlayerFromTeam(RemovePlayerFromTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        super.removePlayerFromTeam(request, responseObserver);
    }

    @Override
    public void updateTeamStats(UpdateTeamStatsRequest request, StreamObserver<TeamStats> responseObserver) {
        super.updateTeamStats(request, responseObserver);
    }

    @Override
    public void addMatchResult(AddMatchResultRequest request, StreamObserver<MatchResult> responseObserver) {
        super.addMatchResult(request, responseObserver);
    }
}
