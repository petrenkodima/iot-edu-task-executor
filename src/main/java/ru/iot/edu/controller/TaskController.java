package ru.iot.edu.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import ru.iot.edu.dto.TaskDto;
import ru.iot.edu.dto.TaskMapper;
import ru.iot.edu.dto.queu.StandsQueue;
import ru.iot.edu.model.Task;
import ru.iot.edu.model.enums.TaskStatus;
import ru.iot.edu.model.enums.UserRole;
import ru.iot.edu.service.TaskExecutorService;
import ru.iot.edu.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("api/task")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;
    private final TaskExecutorService taskExecutorService;
    private final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    public TaskController(TaskService taskService, TaskMapper taskMapper, TaskExecutorService taskExecutorService) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
        this.taskExecutorService = taskExecutorService;
    }

    @GetMapping
    public List<Task> getAllTask() {
        return taskService.getAllTask();
    }

    @PostMapping
    public Task createTask(@ModelAttribute TaskDto taskDto) {
        Task task = taskMapper.map(taskDto, Task.class);
        task.setCreationDate(LocalDateTime.now());
        String fileName = String.join(
                "_",
                taskDto.getNameFile(),
                taskDto.getInitiatorUsername(),
                Long.toString(taskDto.getLabID()),
                task.getCreationDate().format(customFormatter)
        );
        taskExecutorService.uploadScript(fileName, taskDto.getFile());
        task.setFilePath(fileName);
        task.setStatus(TaskStatus.CREATED);

        //todo what return?
        return taskService.createTask(task);
    }

    @GetMapping("/{status}")
    @Operation(summary = "find all by status and sort by creation_date", tags = "user")
    public List<Task> findByStatusAndOrderCreationDate(@PathVariable TaskStatus status) {
        return taskService.findByStatusAndOrderCreationDate(status);
    }

    @GetMapping("find_task")
    public Optional<Task> findTopByStandIdAndRoleUsernameOrderByCreationDateAsc(
            @RequestParam long standId,
            @RequestParam UserRole roleUsername,
            @RequestParam TaskStatus status) {
        return taskService.findTopByStandIdAndRoleUsernameAndStatusOrderByCreationDateAsc(standId, roleUsername, status);
    }

    @GetMapping("find_free_stands_id")
    public List<Long> findFreeStandsId() {
        return taskService.findFreeStandsId();
    }

    @GetMapping("queue_stands")
    public StandsQueue getQueueStands() {
        return  taskService.getQueueStands();
    }
}
