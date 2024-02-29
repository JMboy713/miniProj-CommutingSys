package com.group.commutesystem.controller.team;

import com.group.commutesystem.dto.team.request.CreateTeamRequest;
import com.group.commutesystem.dto.team.response.TeamResponse;
import com.group.commutesystem.service.team.TeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/team")
    public void createTeam(@RequestBody CreateTeamRequest request) {
        teamService.createTeam(request);
    }

    @GetMapping("/team")
    public List<TeamResponse> teams() {
        return teamService.getTeams();
    }


}
