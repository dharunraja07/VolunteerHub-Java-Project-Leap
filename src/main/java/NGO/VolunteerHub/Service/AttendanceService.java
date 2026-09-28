package NGO.VolunteerHub.Service;

import NGO.VolunteerHub.Exception.DuplicateAttendanceException;
import NGO.VolunteerHub.Exception.InvalidAttendanceException;
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

    // CREATE ATTENDANCE
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

            throw new DuplicateAttendanceException(
                    "Attendance already exists for signup ID: " + signupId);
        }

        // Validate attendance details
        validateAttendance(attended, hours);

        // Create attendance record
        AttendanceRecord attendance = new AttendanceRecord();

        attendance.setSignup(signup);
        attendance.setAttended(attended);
        attendance.setHours(hours);

        return attendanceRepository.save(attendance);
    }


    // GET ALL ATTENDANCE
    public List<AttendanceRecord> getAllAttendance() {

        return attendanceRepository.findAll();
    }


    // GET ATTENDANCE BY ID
    public AttendanceRecord getAttendanceById(Long id) {

        return attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance not found with ID: " + id));
    }


    // GET ATTENDANCE BY EVENT
    public List<AttendanceRecord> getAttendanceByEvent(Long eventId) {

        return attendanceRepository.findBySignupEventId(eventId);
    }


    // GET ALL SIGNUPS FOR AN EVENT
    public List<SignUp> getSignupsForAttendance(Long eventId) {

        return signUpRepository.findByEventId(eventId);
    }


    // UPDATE ATTENDANCE
    public AttendanceRecord updateAttendance(
            Long id,
            boolean attended,
            double hours) {

        // Find existing attendance
        AttendanceRecord attendance =
                attendanceRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance not found with ID: " + id));

        // Validate updated attendance details
        validateAttendance(attended, hours);

        // Update attendance
        attendance.setAttended(attended);
        attendance.setHours(hours);

        return attendanceRepository.save(attendance);
    }


    // DELETE ATTENDANCE
    public void deleteAttendance(Long id) {

        // Check whether attendance exists
        if (!attendanceRepository.existsById(id)) {

            throw new ResourceNotFoundException(
                    "Attendance not found with ID: " + id);
        }

        attendanceRepository.deleteById(id);
    }


    // VALIDATE ATTENDANCE
    private void validateAttendance(
            boolean attended,
            double hours) {

        // Hours cannot be negative
        if (hours < 0) {

            throw new InvalidAttendanceException(
                    "Hours cannot be negative");
        }

        // Absent volunteer cannot have hours
        if (!attended && hours > 0) {

            throw new InvalidAttendanceException(
                    "Hours cannot be recorded when volunteer did not attend");
        }
    }
}