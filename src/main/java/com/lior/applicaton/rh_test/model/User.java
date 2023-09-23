package com.lior.applicaton.rh_test.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter@Setter
public class User {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @NotEmpty
    @Size(max = 40, message = "No more than 40 characters")
    private String username;

    @Column
    @NotEmpty
    @Size(max = 80, message = "No more than 80 characters")
    private String password;

    @Column
    @Size(max = 20, message = "No more than 20 characters")
    private String name;

    @Column
    @Size(max = 20, message = "No more than 20 characters")
    private String surname;

    @Column(name = "parent_name")
    @Size(max = 20, message = "No more than 20 characters")
    private String parentName;

//    @Column
//    @Temporal(TemporalType.DATE)
//    private Date creation_date;
//
//    @Column
//    @Temporal(TemporalType.DATE)
//    private Date last_edit_date;

    @OneToMany(mappedBy = "inserted_by")
    private List<News> newsCreated;

    @OneToMany(mappedBy = "updated_by")
    private List<News> newsUpdated;

    @OneToMany(mappedBy = "inserted_by")
    private List<Comment> commentsCreated;

    @Column
    private String role;
}
