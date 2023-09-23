package com.lior.applicaton.rh_test.controllers;

import com.lior.applicaton.rh_test.dto.UserCRUDDTO;
import com.lior.applicaton.rh_test.dto.UserDTO;
import com.lior.applicaton.rh_test.dto.UserLoginDTO;
import com.lior.applicaton.rh_test.model.User;
import com.lior.applicaton.rh_test.security.JWTUtil;
import com.lior.applicaton.rh_test.services.UsersService;
import com.lior.applicaton.rh_test.util.ErrorPrinter;
import com.lior.applicaton.rh_test.util.UserNotFoundException;
import com.lior.applicaton.rh_test.util.UserValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//TODO
@RestController
@RequestMapping("/users")
public class UserController {
    private final UsersService usersService;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;
    private final ErrorPrinter errorPrinter;
    private final JWTUtil jwtUtil;

    private final AuthenticationManager authManager;


    public UserController(UsersService usersService, ModelMapper modelMapper, UserValidator userValidator, ErrorPrinter errorPrinter, JWTUtil jwtUtil
            , AuthenticationManager authManager
    ) {
        this.usersService = usersService;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
        this.errorPrinter = errorPrinter;
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
    }

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody @Valid UserLoginDTO loginDTO,
                                            BindingResult bindingResult){
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                        loginDTO.getPassword());

        //TODO exceptionHandler
        errorPrinter.printErrors(bindingResult);

        authManager.authenticate(authInputToken);
        String jwt = jwtUtil.generateToken(loginDTO.getUsername());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + jwt);
        return ResponseEntity.ok().headers(headers).body(HttpStatus.OK);
    }


    @GetMapping("/{username}")
    public UserDTO userInfo (@PathVariable(name = "username") String username){
        User user = usersService.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return toDTO(user);
    }


    @PostMapping
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid UserCRUDDTO userCRUDDTO,
                                             BindingResult bindingResult){
        User user = modelMapper.map(userCRUDDTO, User.class);

        userValidator.validate(user, bindingResult);
        errorPrinter.printErrors(bindingResult);

        usersService.save(toUser(userCRUDDTO));
        String jwt = jwtUtil.generateToken(userCRUDDTO.getUsername());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + jwt);
        return ResponseEntity.ok().headers(headers).body(HttpStatus.OK);
    }




    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> edit(@RequestBody @Valid UserCRUDDTO userCRUDDTO,
                                           BindingResult bindingResult,
                                           @PathVariable(name = "id") int id){
        User user = modelMapper.map(userCRUDDTO, User.class);

        userValidator.validate(user, bindingResult);
        errorPrinter.printErrors(bindingResult);

        usersService.update(id, user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private User toUser(UserCRUDDTO userCRUDDTO){
        return modelMapper.map(userCRUDDTO, User.class);
    }

    private UserDTO toDTO (User user){
        return modelMapper.map(user, UserDTO.class);
    }
}
