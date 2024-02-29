package com.group.commutesystem.service.team;

import com.group.commutesystem.dto.team.request.CreateTeamRequest;
import com.group.commutesystem.dto.team.response.TeamResponse;
import com.group.commutesystem.model.team.Team;
import com.group.commutesystem.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public void createTeam(CreateTeamRequest request) {
        Team team = new Team(request.getName());
        teamRepository.save(team);
    }

    public List<TeamResponse> getTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream().map(team -> new TeamResponse(team)).collect(Collectors.toList());
    }
}
