package com.praneeth.taskmanager.repository;

import com.praneeth.taskmanager.model.Task;
import com.praneeth.taskmanager.model.TaskPriority;
import com.praneeth.taskmanager.model.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity containing custom queries and basic operations.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId, Sort sort);

    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status, Sort sort);

    List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority, Sort sort);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = :status AND t.priority = :priority")
    List<Task> findByUserIdAndStatusAndPriority(@Param("userId") Long userId, 
                                                @Param("status") TaskStatus status, 
                                                @Param("priority") TaskPriority priority, 
                                                Sort sort);

    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
