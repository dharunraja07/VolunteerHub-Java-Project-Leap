package NGO.VolunteerHub.Repository;
import NGO.VolunteerHub.Model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendanceRepository
        extends JpaRepository<AttendanceRecord, Long> {

    boolean existsBySignupId(Long signupId);

    List<AttendanceRecord> findBySignupEventId(Long eventId);

    @Query("""
           SELECT COALESCE(SUM(a.hours), 0)
           FROM AttendanceRecord a
           WHERE a.signup.volunteer.id = :volunteerId
           AND a.attended = true
           """)
    double getTotalHoursByVolunteerId(
            @Param("volunteerId") Long volunteerId);
}