package NGO.VolunteerHub.Service;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Model.AttendanceRecord;
import NGO.VolunteerHub.Model.SignUp;
import NGO.VolunteerHub.Repository.AttendanceRepository;
import NGO.VolunteerHub.Repository.SignUpRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final SignUpRepository signUpRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            SignUpRepository signUpRepository) {

        this.attendanceRepository = attendanceRepository;
        this.signUpRepository = signUpRepository;
    }

    public AttendanceRecord createAttendance(
            Long signupId,
            boolean attended,
            double hours) {

        // Check whether signup exists
        SignUp signup = signUpRepository.findById(signupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Signup not found with ID: " + signupId));

        // Check whether attendance already exists
        if (attendanceRepository.existsBySignupId(signupId)) {
            throw new RuntimeException(
                    "Attendance already exists for signup ID: " + signupId);
        }

        // If volunteer did not attend, hours must be 0
        if (!attended && hours > 0) {
            throw new RuntimeException(
                    "Hours cannot be recorded when volunteer did not attend");
        }

        // Hours cannot be negative
        if (hours < 0) {
            throw new RuntimeException(
                    "Hours cannot be negative");
        }

        AttendanceRecord attendance = new AttendanceRecord();

        attendance.setSignup(signup);
        attendance.setAttended(attended);
        attendance.setHours(hours);

        return attendanceRepository.save(attendance);
    }

    public List<AttendanceRecord> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public AttendanceRecord getAttendanceById(Long id) {

        return attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance not found with ID: " + id));
    }

    public AttendanceRecord updateAttendance(
            Long id,
            boolean attended,
            double hours) {

        AttendanceRecord attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance not found with ID: " + id));

        if (!attended && hours > 0) {
            throw new RuntimeException(
                    "Hours cannot be recorded when volunteer did not attend");
        }

        if (hours < 0) {
            throw new RuntimeException(
                    "Hours cannot be negative");
        }

        attendance.setAttended(attended);
        attendance.setHours(hours);

        return attendanceRepository.save(attendance);
    }

    public void deleteAttendance(Long id) {

        if (!attendanceRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Attendance not found with ID: " + id);
        }

        attendanceRepository.deleteById(id);
    }
}