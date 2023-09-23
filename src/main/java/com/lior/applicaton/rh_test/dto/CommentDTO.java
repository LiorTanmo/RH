package com.lior.applicaton.rh_test.dto;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

//TODO probably needs adjustments to User
@Getter@Setter
public class CommentDTO {

    @Size(max = 300, message = "Up to 300 characters")
    private String text;

    private UserDTO comment_author;
}
