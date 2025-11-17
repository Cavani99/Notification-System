package main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private UUID userId;

    @OneToMany(mappedBy = "sender")
    private List<Notification> notificationsSent;

    @OneToMany(mappedBy = "receiver")
    private List<Notification> notificationsReceived;
}
