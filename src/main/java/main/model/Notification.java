package main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    @NotNull
    private String title;

    @Column(name = "message")
    @NotNull
    private String message;

    @Column(name = "link")
    private String link;

    @Column(name = "link_title")
    private String linkTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull
    private NotificationType type;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "sender", referencedColumnName = "id")
    private User sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver", referencedColumnName = "id")
    private User receiver;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "seen", nullable = false)
    private boolean seen = false;
}
