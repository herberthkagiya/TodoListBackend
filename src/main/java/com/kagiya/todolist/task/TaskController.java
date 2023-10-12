package com.kagiya.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity create(
        @RequestBody TaskModel newTask, 
        HttpServletRequest request
    ){
        var idUser = request.getAttribute("idUser");
        newTask.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();  
        if(
            currentDate.isAfter(newTask.getStartAt()) || 
            currentDate.isAfter(newTask.getEndAt())
        ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start and end date must be higher than current");
        }

        if(newTask.getStartAt().isAfter(newTask.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The start must be before end date");
        }

        var createdTask = this.repostiory.save(newTask);
        return ResponseEntity.status(HttpStatus.OK).body(createdTask);
    }
}
