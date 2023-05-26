package ru.iot.edu.service;

import org.springframework.stereotype.Service;
import ru.iot.edu.dto.queu.StandQueue;
import ru.iot.edu.dto.queu.StandsQueue;
import ru.iot.edu.dto.queu.TaskQueue;
import ru.iot.edu.model.Task;
import ru.iot.edu.model.enums.TaskStatus;
import ru.iot.edu.model.enums.UserRole;
import ru.iot.edu.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        //todo add status to create
        return taskRepository.save(task);
    }

    public List<Task> findByStatusAndOrderCreationDate(TaskStatus status) {
        return taskRepository.findAllByStatusOrderByCreationDateAsc(status);
    }

    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> findByStatusAndByStand(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> findByStatusAndExpirationDateNotNull(TaskStatus status) {
        return taskRepository.findByStatusAndExpirationDateNotNull(status);
    }

    public List<Task> updateMultipleTask(List<Task> taskForUpdate) {
        return taskRepository.saveAll(taskForUpdate);
    }

    public List<Long> findFreeStandsId() {
        return taskRepository.findFreeStandsId();
    }

    public Optional<Task> findTopByStandIdAndRoleUsernameAndStatusOrderByCreationDateAsc(long standId, UserRole roleUsername, TaskStatus status) {
        return taskRepository.findTopByStandIdAndRoleUsernameAndStatusOrderByCreationDateAsc(standId, roleUsername, status);
    }

    public Optional<Task> findById(long id) {
        return taskRepository.findById(id);
    }

    public Task save(Task task) {
        return taskRepository.save(task);
    }

    public StandsQueue getQueueStands() {
        List<StandQueue> standsQueue = new ArrayList<>();

        Map<Long, List<Task>> queueMap = taskRepository.findAll().stream()
                .filter(task -> task.getStatus().equals(TaskStatus.EXECUTE) ||
                        task.getStatus().equals(TaskStatus.CREATED)
                )
                .collect(Collectors.groupingBy(Task::getStandId));

        Map<Long, List<Task>> allStands = taskRepository.findAll().stream()
                .collect(Collectors.groupingBy(Task::getStandId));

        for (Map.Entry<Long, List<Task>> entry : allStands.entrySet()) {
            if (!queueMap.containsKey(entry.getKey())) {
                queueMap.put(entry.getKey(), new ArrayList<>());
            }
        }
        for (Map.Entry<Long, List<Task>> entry : queueMap.entrySet()) {
            List<Task> newListTask = new ArrayList<>();

            List<Task> teachersTask = entry.getValue().stream()
                    .filter(task -> task.getRoleUsername().equals(UserRole.TEACHER))
                    .sorted((t1, t2) -> t1.getCreationDate().compareTo(t2.getCreationDate()))
                    .collect(Collectors.toList());

            if (teachersTask.size() >= 5) {
                newListTask.addAll(teachersTask.subList(0, 5));
            } else {
                List<Task> studentsTask = entry.getValue().stream()
                        .filter(task -> task.getRoleUsername().equals(UserRole.STUDENT))
                        .sorted((t1, t2) -> t1.getCreationDate().compareTo(t2.getCreationDate()))
                        .collect(Collectors.toList());
                newListTask.addAll(teachersTask);
                for (Task t : studentsTask) {
                    newListTask.add(t);
                    if (newListTask.size() >= 5) break;
                }
            }

            entry.setValue(newListTask);

        }

        for (Map.Entry<Long, List<Task>> entry : queueMap.entrySet()) {
            standsQueue.add(
                    StandQueue.builder()
                            .standId(entry.getKey())
                            .queue(entry.getValue()
                                    .stream()
                                    .map(this::mapTaskToTaskQueue)
                                    .collect(Collectors.toList())
                            )
                            .build()
            );
        }

        return StandsQueue.builder()
                .queue(standsQueue)
                .build();
    }

    private TaskQueue mapTaskToTaskQueue(Task task) {
        return TaskQueue.builder()
                .id(task.getId())
                .status(task.getStatus())
                .labID(task.getLabID())
                .duration(task.getDuration())
                .initiatorUsername(task.getInitiatorUsername())
                .roleUsername(task.getRoleUsername())
                .build();
    }
}
