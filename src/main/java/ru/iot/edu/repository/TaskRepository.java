package ru.iot.edu.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.iot.edu.model.Task;
import ru.iot.edu.model.enums.TaskStatus;
import ru.iot.edu.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByStatusOrderByCreationDateAsc(TaskStatus status);

    List<Task> findByStatusAndExpirationDateNotNull(TaskStatus status);

    List<Task> findByStatus(TaskStatus status);

    Optional<Task> findTopByStandIdAndRoleUsernameAndStatusOrderByCreationDateAsc(long standId, UserRole roleUsername, TaskStatus status);

    @Query(value = "select t.standId from Task t " +
            "where t.standId  not in " +
            "(select t2.standId from Task t2 where t2.status ='EXECUTE') group by t.standId")
    List<Long> findFreeStandsId();
}