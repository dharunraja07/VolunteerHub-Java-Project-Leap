package NGO.VolunteerHub.Controller;
import NGO.VolunteerHub.Model.AttendanceRecord;
import NGO.VolunteerHub.Service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<AttendanceRecord> createAttendance(
            @RequestParam Long signupId,
            @RequestParam boolean attended,
            @RequestParam double hours) {

        AttendanceRecord attendance =
                attendanceService.createAttendance(
                        signupId,
                        attended,
                        hours
                );

        return ResponseEntity.ok(attendance);
    }

    @GetMapping
    public ResponseEntity<List<AttendanceRecord>> getAllAttendance() {

        return ResponseEntity.ok(
                attendanceService.getAllAttendance()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRecord> getAttendanceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceRecord> updateAttendance(
            @PathVariable Long id,
            @RequestParam boolean attended,
            @RequestParam double hours) {

        AttendanceRecord updatedAttendance =
                attendanceService.updateAttendance(
                        id,
                        attended,
                        hours
                );

        return ResponseEntity.ok(updatedAttendance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttendance(
            @PathVariable Long id) {

        attendanceService.deleteAttendance(id);

        return ResponseEntity.ok(
                "Attendance deleted successfully"
        );
    }
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<AttendanceRecord>> getAttendanceByEvent(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceByEvent(eventId)
        );
    }
}