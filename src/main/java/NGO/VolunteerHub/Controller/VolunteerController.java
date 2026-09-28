package NGO.VolunteerHub.Controller;
import NGO.VolunteerHub.Model.Volunteer;
import NGO.VolunteerHub.Service.VolunteerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/volunteers")
@CrossOrigin
public class VolunteerController {
    private final VolunteerService volunteerService;
    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;}

    // Creating Volunteer
    @PostMapping
    public ResponseEntity<Volunteer> createVolunteer(
            @RequestBody Volunteer volunteer) {
        Volunteer savedVolunteer =
                volunteerService.createVolunteer(volunteer);
        return ResponseEntity.ok(savedVolunteer);}

    // Get All the Volunteers
    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {
        return ResponseEntity.ok(
                volunteerService.getAllVolunteers()
        );}

    // Get Volunteer by id
    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteerById(
            @PathVariable Long id) {
        return volunteerService.getVolunteerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());}

    // updating a Voulunteer
    @PutMapping("/{id}")
    public ResponseEntity<Volunteer> updateVolunteer(
            @PathVariable Long id,
            @RequestBody Volunteer volunteer) {
        Volunteer updatedVolunteer =
                volunteerService.updateVolunteer(id, volunteer);
        return ResponseEntity.ok(updatedVolunteer);
    }

    // Deleting a Volunteer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVolunteer(
            @PathVariable Long id) {

        volunteerService.deleteVolunteer(id);
        return ResponseEntity.ok(
                "Volunteer deleted successfully"
        );}
}