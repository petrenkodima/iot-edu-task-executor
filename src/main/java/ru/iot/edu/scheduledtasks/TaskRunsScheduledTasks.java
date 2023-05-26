package ru.iot.edu.scheduledtasks;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.iot.edu.model.Task;
import ru.iot.edu.model.enums.TaskStatus;
import ru.iot.edu.model.enums.UserRole;
import ru.iot.edu.service.TaskExecutorService;
import ru.iot.edu.service.TaskService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.iot.edu.scheduledtasks.ConfigTimeScheduledTasks.RATE_FOR_CHECK_MILLISECONDS;


@Slf4j
@Component
@AllArgsConstructor
public class TaskRunsScheduledTasks {
    private final TaskService taskService;
    private final TaskExecutorService taskExecutorService;
    private final ru.iot.edu.config.SchedulingConfig schedulingConfig;

    @Scheduled(fixedRate = RATE_FOR_CHECK_MILLISECONDS)
    public void completeTask() {
        List<Task> taskExpirationList = taskService.findByStatusAndExpirationDateNotNull(TaskStatus.EXECUTE)
                .stream()
                .filter(task -> task.getExpirationDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        logForCompleteTask(taskExpirationList.toString());
        //todo add step wih run default script
        //todo add step wih move this rows in history table
        taskExpirationList.forEach(x -> x.setStatus(TaskStatus.COMPLETED));
        taskService.updateMultipleTask(taskExpirationList);
    }

    @Scheduled(fixedRate = RATE_FOR_CHECK_MILLISECONDS)
    public void findExecuteTask() {
        List<Long> freeStandsId = taskService.findFreeStandsId();
        logForFindExecuteTask("freeStandsId: " + freeStandsId.toString());


        freeStandsId.forEach(standId -> {
            Optional<Task> taskToExecute = findTaskWitRule(standId);
            if (taskToExecute.isEmpty()) return;
            logForFindExecuteTask("find task for execute: " + taskToExecute.get());
            try {
                runScript(taskToExecute.get());
            } catch (IOException e) {
                logErrorForFindExecuteTask("error when run script " + e.getMessage());
            }
        });


    }

    private void runScript(Task task) throws IOException {
        logForFindExecuteTask("runScript: " + task);
        task.setExecuteDate(LocalDateTime.now());
        task.setStatus(TaskStatus.EXECUTE);
        task.setExpirationDate(LocalDateTime.now().plusSeconds(task.getDuration()));
        logForFindExecuteTask("runScript:  task exp_date: " + task.getExpirationDate());
        taskService.save(task);
        //copy file to arduino
        // compile
        taskExecutorService.executeUploadedScript(task.getFilePath(), task.getUsbPort());
    }

    private Optional<Task> findTaskWitRule(Long standId) {
        Optional<Task> taskToExecute;
        taskToExecute = taskService.findTopByStandIdAndRoleUsernameAndStatusOrderByCreationDateAsc(
                standId,
                UserRole.TEACHER,
                TaskStatus.CREATED
        );
        if (taskToExecute.isEmpty()) {
            taskToExecute = taskService.findTopByStandIdAndRoleUsernameAndStatusOrderByCreationDateAsc(
                    standId,
                    UserRole.STUDENT,
                    TaskStatus.CREATED
            );
        }
        return taskToExecute;
    }

    private void logForFindExecuteTask(String message) {
        if (schedulingConfig.isLogForFindExecuteTask) log.info(message);
    }

    private void logErrorForFindExecuteTask(String message) {
        if (schedulingConfig.isLogForFindExecuteTask) log.error(message);
    }

    private void logForCompleteTask(String message) {
        if (schedulingConfig.isLogForCompleteTask) log.info(message);
    }
}