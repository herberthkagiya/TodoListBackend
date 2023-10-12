package com.kagiya.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kagiya.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository repository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain
    ) throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if(servletPath.equals("/tasks")){
            var authorization = request.getHeader("Authorization");

            var authEnconded = authorization.substring("Basic".length()).trim();
            byte[] authDecoded = Base64.getDecoder().decode(authEnconded);

            var authString = new String(authDecoded); 
            String[] credentials = authString.split(":");

            String username = credentials[0];
            String password = credentials[1];

            var user = this.repository.findByUsername(username);

            if(user == null){
                response.sendError(401);
            }
            else{
                var passwordVerify = BCrypt.verifyer().verify(
                    password.toCharArray(), 
                    user.getPassword().toCharArray()
                );

                if(passwordVerify.verified){
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                }
                else{
                    response.sendError(401);
                }
            }
        }
        else{
            filterChain.doFilter(request, response);
        }
    } 
}
