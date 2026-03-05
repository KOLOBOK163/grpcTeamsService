package com.guildhub.team_service.grpc;

import com.google.protobuf.Empty;
import com.guildhub.team_service.Entity.*;
import com.guildhub.team_service.Entity.PlayerCard;
import com.guildhub.team_service.Entity.Team;
import com.guildhub.team_service.Entity.enums.PlayerRole;
import com.guildhub.team_service.Mapper.ProtoMapper;
import com.guildhub.team_service.Repository.PlayerCardRepository;
import com.guildhub.team_service.Repository.TeamRepository;
import com.guildhub.teams_service.grpc.*;
import com.guildhub.teams_service.grpc.MatchResult;
import com.guildhub.teams_service.grpc.TeamStats;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;


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
        try{
            Team team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Team with id " + request.getTeamId() + "not found"));

            com.guildhub.teams_service.grpc.Team teamProto = protoMapper.toProtoTeam(team);

            responseObserver.onNext(teamProto);
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
    public void updateTeam(UpdateTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        try{
            Team team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new EntityNotFoundException("Entity with id " + request.getTeamId() + "not found"));

            String newName = request.getName();
            if(!team.getName().equals(newName) && teamRepository.existByName(newName))
            {
                responseObserver.onError(
                        Status.ALREADY_EXISTS
                                .withDescription("News with title '" + newName + "' already exists")
                                .asRuntimeException()
                );
                return;
            }

            ImageUpload logo = ImageUpload.newBuilder()
                    .setImageData(request.getLogo().getImageData())
                    .setImageFilename(request.getLogo().getImageFilename())
                    .setImageContentType(request.getLogo().getImageContentType())
                    .build();

            team.setName(newName);
            team.setCountry(request.getCountry());
            team.setLogoUrl(String.valueOf(logo));

            Team updatedTeam = teamRepository.save(team);

            responseObserver.onNext(protoMapper.toProtoTeam(updatedTeam));
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
    public void deleteTeam(GetTeamRequest request, StreamObserver<Empty> responseObserver) {
        Long teamId = request.getTeamId();

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + teamId + "not found"));

        if (team == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("News not found with id: " + teamId)
                            .asRuntimeException()
            );
            return;
        }

        teamRepository.delete(team);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void listTeams(ListTeamsRequest request, StreamObserver<TeamsList> responseObserver) {
        int pageNumber = Math.max(0, request.getPage());
        int pageSize = Math.min(request.getPageSize() > 0 ? request.getPageSize() : 10,
                50);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Team> page = teamRepository.findAllByOrderByCreatedAtDesc(pageRequest);

        List<TeamLight> teamList = page.getContent().stream()
                .map(protoMapper::toProtoTeamLight)
                .toList();

        TeamsList response = TeamsList.newBuilder()
                .addAllTeams(teamList)
                .setTotalCount((int) page.getTotalElements())
                .setPage(pageNumber)
                .setPageSize(pageSize)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createPlayer(CreatePlayerRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {

        if(playerCardRepository.existsByNickName(request.getNickname()))
        {
            throw new EntityExistsException("Player with nickname: " + request.getNickname() + "already exists");
        }

        ImageUpload avatar = ImageUpload.newBuilder()
                .setImageData(request.getAvatar().getImageData())
                .setImageFilename(request.getAvatar().getImageFilename())
                .setImageContentType(request.getAvatar().getImageContentType())
                .build();

        PlayerRole role = protoMapper.fromProtoPlayerRole(request.getRole());

        PlayerCard player = PlayerCard.builder()
                .nickName(request.getNickname())
                .fullName(request.getFullName())
                .age(request.getAge())
                .country(request.getCountry())
                .role(role)
                .avatarUrl(null)
                .build();

        PlayerCard savedPlayer = playerCardRepository.save(player);
        com.guildhub.teams_service.grpc.PlayerCard playerProto = protoMapper.toProtoPlayerCard(savedPlayer);

        responseObserver.onNext(playerProto);
        responseObserver.onCompleted();
    }

    @Override
    public void getPlayer(GetPlayerRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        Long id = request.getPlayerId();

        PlayerCard player = playerCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Player with id: " + id + "not found"));

        com.guildhub.teams_service.grpc.PlayerCard playerProto = protoMapper.toProtoPlayerCard(player);

        responseObserver.onNext(playerProto);
        responseObserver.onCompleted();
    }

    @Override
    public void updatePlayer(UpdatePlayerRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        PlayerCard player = playerCardRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new EntityNotFoundException("Entity with id " + request.getPlayerId() + "not found"));

        String newNickname = request.getNickname();
        if(!player.getNickName().equals(newNickname) && playerCardRepository.existsByNickName(newNickname))
        {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("News with title '" + newNickname + "' already exists")
                            .asRuntimeException()
            );
            return;
        }

        PlayerRole role = protoMapper.fromProtoPlayerRole(request.getRole());

        player.setNickName(newNickname);
        player.setFullName(request.getFullName());
        player.setAge(request.getAge());
        player.setCountry(request.getCountry());
        player.setRole(role);

        PlayerCard updatedPlayer = playerCardRepository.save(player);

        responseObserver.onNext(protoMapper.toProtoPlayerCard(updatedPlayer));
        responseObserver.onCompleted();
    }

    @Override
    public void updatePlayerAvatar(UpdatePlayerAvatarRequest request, StreamObserver<com.guildhub.teams_service.grpc.PlayerCard> responseObserver) {
        super.updatePlayerAvatar(request, responseObserver);
    }

    @Override
    public void deletePlayer(GetPlayerRequest request, StreamObserver<Empty> responseObserver) {
        Long id = request.getPlayerId();
        PlayerCard playerCard = playerCardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Player with id: " + id + "not found"));

        playerCardRepository.delete(playerCard);
    }

    @Override
    public void addPlayerToTeam(AddPlayerToTeamRequest request, StreamObserver<com.guildhub.teams_service.grpc.Team> responseObserver) {
        Long playerId = request.getPlayerId();
        Long teamId = request.getTeamId();

        if(playerId != null && teamId != null)
        {
            throw new IllegalArgumentException("Player Id and Team Id must be not null");
        }

        PlayerCard player = playerCardRepository.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Player with id: " + playerId + "not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team with id: " + teamId + "not found"));

        if (team.getPlayers().stream().anyMatch(p -> playerId.equals(p.getId()))) {
            throw new IllegalArgumentException("Player with id " + playerId + " is already in team " + teamId);
        }
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
