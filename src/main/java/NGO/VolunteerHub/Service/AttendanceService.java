package NGO.VolunteerHub.Service;

import NGO.VolunteerHub.Exception.DuplicateAttendanceException;
import NGO.VolunteerHub.Exception.InvalidAttendanceException;
import NGO.VolunteerHub.Exception.ResourceNotFoundException;
import NGO.VolunteerHub.Model.AttendanceRecord;
import NGO.VolunteerHub.Model.SignUp;
import NGO.VolunteerHub.Repository.AttendanceRepository;
import NGO.VolunteerHub.Repository.EventRepository;
import NGO.VolunteerHub.Repository.SignUpRepository;
import NGO.VolunteerHub.Repository.VolunteerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    private static final double MAX_HOURS_PER_RECORD = 24.0;

    private final AttendanceRepository attendanceRepository;
    private final SignUpRepository signUpRepository;
    private final EventRepository eventRepository;
    private final VolunteerRepository volunteerRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            SignUpRepository signUpRepository,
            EventRepository eventRepository,
            VolunteerRepository volunteerRepository) {

        this.attendanceRepository = attendanceRepository;
        this.signUpRepository = signUpRepository;
        this.eventRepository = eventRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public AttendanceRecord createAttendance(
            Long signupId,
            boolean attended,
            double hours) {

        SignUp signup = signUpRepository.findById(signupId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Signup not found with ID: " + signupId));

        if (attendanceRepository.existsBySignupId(signupId)) {
            throw new DuplicateAttendanceException(
                    "Attendance already exists for signup ID: " + signupId);
        }

        validateAttendance(attended, hours);

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

    public List<AttendanceRecord> getAttendanceByEvent(Long eventId) {

        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(
                    "Event not found with ID: " + eventId);
        }

        return attendanceRepository.findBySignupEventId(eventId);
    }

    public List<SignUp> getSignupsForAttendance(Long eventId) {

        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(
                    "Event not found with ID: " + eventId);
        }

        return signUpRepository.findByEventId(eventId);
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

        validateAttendance(attended, hours);

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

    private void validateAttendance(
            boolean attended,
            double hours) {

        if (Double.isNaN(hours) || Double.isInfinite(hours)) {
            throw new InvalidAttendanceException(
                    "Hours must be a valid number");
        }

        if (hours < 0) {
            throw new InvalidAttendanceException(
                    "Hours cannot be negative");
        }

        if (hours > MAX_HOURS_PER_RECORD) {
            throw new InvalidAttendanceException(
                    "Hours cannot exceed 24 hours for one attendance record");
        }

        if (!attended && hours > 0) {
            throw new InvalidAttendanceException(
                    "Hours cannot be recorded when volunteer did not attend");
        }
    }

    public double getTotalHoursByVolunteer(Long volunteerId) {

        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResourceNotFoundException(
                    "Volunteer not found with ID: " + volunteerId);
        }

        return attendanceRepository
                .getTotalHoursByVolunteerId(volunteerId);
    }
}
