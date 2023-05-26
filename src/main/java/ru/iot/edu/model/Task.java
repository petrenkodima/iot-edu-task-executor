package ru.iot.edu.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.iot.edu.model.enums.TaskStatus;
import ru.iot.edu.model.enums.UserRole;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks_queue")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "initiator_username")
    private String initiatorUsername;

    @Column(name = "lab_id")
    private long labID;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.CREATED;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "stand_id")
    private long standId;

    @Column(name = "execute_date")
    private LocalDateTime executeDate;

    @Column(name = "role_username")
    @Enumerated(EnumType.STRING)
    private UserRole roleUsername;

    @Column(name = "duration")
    private long duration;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "usb_port")
    private String usbPort;
}
