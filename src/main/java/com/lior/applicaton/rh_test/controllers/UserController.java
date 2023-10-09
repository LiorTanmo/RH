package com.lior.applicaton.rh_test.controllers;

import com.lior.applicaton.rh_test.dto.UserCRUDDTO;
import com.lior.applicaton.rh_test.dto.UserDTO;
import com.lior.applicaton.rh_test.dto.UserLoginDTO;
import com.lior.applicaton.rh_test.model.User;
import com.lior.applicaton.rh_test.security.JWTUtil;
import com.lior.applicaton.rh_test.security.UserAccountDetails;
import com.lior.applicaton.rh_test.services.UsersService;
import com.lior.applicaton.rh_test.util.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

//TODO
@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UsersService usersService;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;
    private final ErrorPrinter errorPrinter;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody @Valid UserLoginDTO loginDTO,
                                            BindingResult bindingResult){
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),
                        loginDTO.getPassword());

        errorPrinter.printFieldErrors(bindingResult);

        authManager.authenticate(authInputToken);
        String jwt = jwtUtil.generateToken(loginDTO.getUsername());
        HttpHeaders headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + jwt);
        return ResponseEntity.ok().headers(headers).body(HttpStatus.OK);
    }


    @GetMapping("/{username}")
    public UserDTO userPage (@PathVariable(name = "username") String username){
        User user = usersService.findUserByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        return toDTO(user);
    }

    @GetMapping("/userInfo")
    public UserDTO currentUserInfo (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccountDetails userAccountDetails = (UserAccountDetails) authentication.getPrincipal();
        User currentUser = userAccountDetails.getUser();
        return toDTO(currentUser);
    }


    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid UserCRUDDTO userCRUDDTO,
                                             BindingResult bindingResult){
        User user = toUser(userCRUDDTO);
        if (user.getRole() == null){user.setRole("ROLE_SUBSCRIBER");}
        userValidator.validate(user, bindingResult);
        errorPrinter.printFieldErrors(bindingResult);

        usersService.save(user);
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
        errorPrinter.printFieldErrors(bindingResult);

        usersService.update(id, user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> exceptionHandler (Exception e){
        ErrorResponse response = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    private User toUser(UserCRUDDTO userCRUDDTO){
        User user = modelMapper.map(userCRUDDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return user;
    }

    private UserDTO toDTO (User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return userDTO;
    }
}
