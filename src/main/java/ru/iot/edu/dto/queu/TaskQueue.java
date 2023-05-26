package ru.iot.edu.dto.queu;

import lombok.Builder;
import lombok.Data;
import ru.iot.edu.model.enums.TaskStatus;
import ru.iot.edu.model.enums.UserRole;

@Data
@Builder
public class TaskQueue {
    private long id;
    private TaskStatus status;
    private String initiatorUsername;
    private long labID;
    private UserRole roleUsername;
    private long duration;
}
