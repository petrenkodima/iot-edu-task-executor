package ru.iot.edu.dto.queu;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StandsQueue {
    private List<StandQueue> queue;
}
