package ru.iot.edu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import ru.iot.edu.model.enums.UserRole;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TaskDto {
    private String initiatorUsername;
    private long labID;
    private long standId;
    private UserRole roleUsername;
    private long duration;
    private String usbPort;
    private String nameFile;
    private MultipartFile file;
}








