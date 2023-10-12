package com.kagiya.todolist.task;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepostiory extends JpaRepository<TaskModel, UUID> {
    
}
