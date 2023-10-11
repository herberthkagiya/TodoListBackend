package com.kagiya.todolist.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private IUserRepository repository;

    @PostMapping("")
    public ResponseEntity create(
        @RequestBody UserModel newUser
    ){

        var user = this.repository.findByUsername(newUser.getUsername());
        
        if(user != null){
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("User already exists");
        }

        var passwordHashered = BCrypt
            .withDefaults()
            .hashToString(
                12, 
                newUser.getPassword().toCharArray()
            );
            
        newUser.setPassword(passwordHashered);


        var userCreated = this.repository.save(newUser);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userCreated);
    }
}
