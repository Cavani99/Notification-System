package project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @OneToMany(mappedBy = "sender")
    private List<Notification> notificationsSent;

    @OneToMany(mappedBy = "receiver")
    private List<Notification> notificationsReceived;
}
