package NGO.VolunteerHub.Controller;

import NGO.VolunteerHub.Model.SignUp;
import NGO.VolunteerHub.Service.SignUpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signups")
@CrossOrigin
public class SignUpController {

    private final SignUpService signUpService;

    public SignUpController(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    @PostMapping
    public ResponseEntity<SignUp> createSignup(
            @RequestParam Long eventId,
            @RequestParam Long volunteerId) {

        SignUp signup =
                signUpService.createSignup(eventId, volunteerId);

        return ResponseEntity.ok(signup);
    }

    @GetMapping
    public ResponseEntity<List<SignUp>> getAllSignups() {
        return ResponseEntity.ok(
                signUpService.getAllSignups()
        );
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<SignUp>> getSignupsByEvent(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                signUpService.getSignupsByEvent(eventId)
        );
    }

    @GetMapping("/volunteer/{volunteerId}")
    public ResponseEntity<List<SignUp>> getSignupsByVolunteer(
            @PathVariable Long volunteerId) {

        return ResponseEntity.ok(
                signUpService.getSignupsByVolunteer(volunteerId)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSignup(
            @PathVariable Long id) {

        signUpService.deleteSignup(id);

        return ResponseEntity.ok(
                "Signup deleted successfully"
        );
    }
}