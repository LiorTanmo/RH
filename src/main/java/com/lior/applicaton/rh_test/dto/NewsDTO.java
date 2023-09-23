package com.lior.applicaton.rh_test.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

//TODO probably needs adjustments to user

@Getter
@Setter
public class NewsDTO {

    @Size(min=2, max = 150, message = "Up to 150 characters, at least 2 characters")
    private String title;

    @Size(min = 2, max = 2000,message = "Up to 2000 characters, at least 2 characters")
    private String text;

    private UserDTO inserted_by;

   // private List<NewsDTO> comments;
}
