package NGO.VolunteerHub.Model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Entity
@Table(
        name = "signups",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"event_id", "volunteer_id"}
        )
)
@Data
@NoArgsConstructor
public class SignUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;
    private LocalDate signupDate;
}