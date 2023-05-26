package ru.iot.edu.dto.queu;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StandQueue {
    private long standId;
    private List<TaskQueue> queue;
}
