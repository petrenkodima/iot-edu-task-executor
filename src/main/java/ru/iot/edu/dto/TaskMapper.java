package ru.iot.edu.dto;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;
import ru.iot.edu.model.Task;

@Component
public class TaskMapper extends ConfigurableMapper {
    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(Task.class, TaskDto.class)
                .byDefault()
                .register();
    }
}



