package com.kagiya.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kagiya.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepostiory repostiory;

    @PostMapping("/")
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


    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){

        var idUser = request.getAttribute("idUser");
        var tasks = this.repostiory.findByIdUser((UUID) idUser);

        return tasks;
    }

    
    @PutMapping("/{id}")
    public ResponseEntity update(
        @RequestBody TaskModel taskModel,
        HttpServletRequest request,
        @PathVariable UUID id
    ){

        var task = this.repostiory.findById(id).orElse(null);

        if(task == null){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Task doesn't exists");
        }

        var idUser = request.getAttribute("idUser");

        if(!idUser.equals(task.getIdUser())){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Autenticated user doesn't have permission to update this task");
        }

        Utils.copyNonNullProperties(taskModel, task);

        var updatedTask = this.repostiory.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
