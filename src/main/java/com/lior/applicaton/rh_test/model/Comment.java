package com.lior.applicaton.rh_test.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "comments")
@Getter@Setter
public class Comment {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @Size(max = 300,message = "Up to 300 characters")
    private String text;

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
    @JoinColumn(name = "id_news", referencedColumnName = "id")
    private News commentednews;
}
