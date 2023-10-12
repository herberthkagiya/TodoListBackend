package com.kagiya.todolist.task;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepostiory repostiory;

    @PostMapping("")
    public TaskModel create(
        @RequestBody TaskModel newTask, 
        HttpServletRequest request
    ){
        var idUser = request.getAttribute("idUser");
        newTask.setIdUser((UUID) idUser);
        var createdTask = this.repostiory.save(newTask);
        return createdTask;
    }
}