package com.lior.applicaton.rh_test.model;


import com.lior.applicaton.rh_test.util.NotAuthorizedException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Entity
@Table(name = "comments")
@Data
public class Comment {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @Size(max = 300,message = "Up to 300 characters")
    private String text;

//    @Column
//    @Temporal(TemporalType.DATE)
//    private Date creation_date;
//
//    @Column
//    @Temporal(TemporalType.DATE)
//    private Date last_edit_date;

    @ManyToOne
    @JoinColumn(name = "inserted_by_id", referencedColumnName = "id")
    private User inserted_by;

    @ManyToOne
    @JoinColumn(name = "id_news", referencedColumnName = "id")
    private News commentednews;

    //TODO make more scalable solution
    //prevents unauthorized modifications by comparing usernames of creator
    //and active session user (or if active user is Admin)
    @PreRemove
    @PreUpdate
    private void preventUnAuthorizedAccess() throws NotAuthorizedException {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SimpleGrantedAuthority> roles = (List<SimpleGrantedAuthority>) SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();
        //checks if active user is Admin, comment author, or commented News Author
        if(roles.stream().noneMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"))
                && !(name.equals(this.inserted_by.getUsername())
                || name.equals(this.commentednews.getInserted_by().getUsername()))){
            throw new NotAuthorizedException("You can alter and remove only your own comments");
        }

    }
}
