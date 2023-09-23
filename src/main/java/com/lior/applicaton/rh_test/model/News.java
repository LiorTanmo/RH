package com.lior.applicaton.rh_test.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Table
@Getter@Setter
public class News {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @Size(max = 150, message = "Up to 150 characters")
    private String title;

    @Column
    @Size(max = 2000,message = "Up to 2000 characters")
    private String text;

    @OneToMany(mappedBy = "commentednews")
    private List<Comment> comments;

    @Column
    @Temporal(TemporalType.DATE)
    private Date creation_date;

    @Column
    @Temporal(TemporalType.DATE)
    private Date last_edit_date;

    @ManyToOne
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id")
    private User inserted_by;

    @ManyToOne
    @JoinColumn(name = "updated_by_id", referencedColumnName = "id")
    private User updated_by;
}
