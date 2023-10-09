package com.lior.applicaton.rh_test.model;

import com.lior.applicaton.rh_test.util.NotAuthorizedException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.mapping.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.management.relation.Role;
import java.util.List;

@Entity
@Table(name = "users")
@Data
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

    //на случай если понадобится

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

    @PreRemove
    @PreUpdate
    private void preventUnAuthorizedRemove() throws NotAuthorizedException {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SimpleGrantedAuthority> roles = (List<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if(roles.stream().noneMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"))
                && !name.equals(this.username)){
            throw new NotAuthorizedException("Access denied");
        }

    }
}
