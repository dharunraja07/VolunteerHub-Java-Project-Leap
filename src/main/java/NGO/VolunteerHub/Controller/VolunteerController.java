package NGO.VolunteerHub.Controller;
import NGO.VolunteerHub.Model.Volunteer;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Service.VolunteerService;
import NGO.VolunteerHub.Service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@CrossOrigin
public class VolunteerController {

    private final VolunteerService volunteerService;
    private final AttendanceService attendanceService;

    public VolunteerController(
            VolunteerService volunteerService,
            AttendanceService attendanceService) {

        this.volunteerService = volunteerService;
        this.attendanceService = attendanceService;
    }

    // Creating Volunteer
    @PostMapping
    public ResponseEntity<Volunteer> createVolunteer(
            @Valid @RequestBody Volunteer volunteer) {

        Volunteer savedVolunteer =
                volunteerService.createVolunteer(volunteer);

        return ResponseEntity.ok(savedVolunteer);
    }

    // Get All the Volunteers
    @GetMapping
    public ResponseEntity<List<Volunteer>> getAllVolunteers() {

        return ResponseEntity.ok(
                volunteerService.getAllVolunteers()
        );
    }

    // Get Volunteer by ID
    @GetMapping("/{id}")
    public ResponseEntity<Volunteer> getVolunteerById(
            @PathVariable Long id) {

        Volunteer volunteer = volunteerService.getVolunteerById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Volunteer not found with ID: " + id));

        return ResponseEntity.ok(volunteer);
    }

    // Updating a Volunteer
    @PutMapping("/{id}")
    public ResponseEntity<Volunteer> updateVolunteer(
            @PathVariable Long id,
            @Valid @RequestBody Volunteer volunteer) {

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
        );
    }

    // Get Total Volunteer Hours
    @GetMapping("/{id}/hours")
    public ResponseEntity<Double> getTotalHours(
            @PathVariable Long id) {

        double totalHours =
                attendanceService.getTotalHoursByVolunteer(id);

        return ResponseEntity.ok(totalHours);
    }
}