package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;


    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(@PathVariable int executionId, Principal principal, @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return tournamentService.createTournament(executionId, user.getId(), tournamentDto);
    }

    @GetMapping("/executions/{executionId}/tournaments/show-open")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> showAllOpenTournaments(@PathVariable int executionId) {
        return tournamentService.showAllOpenTournaments(executionId);
    }

    @PutMapping("/tournaments/{tournamentId}/enroll/{studentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public List<UserDto> enrollInTournament(@PathVariable int studentId, @PathVariable Integer tournamentId) {
        return tournamentService.enrollInTournament(studentId, tournamentId);
    }

    @GetMapping("/executions/{executionId}/tournaments/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> showAvailableTournaments(@PathVariable int executionId) {
        return tournamentService.showAvailableTournaments(executionId);
    }

}