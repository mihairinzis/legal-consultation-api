package com.code4ro.legalconsultation.model.persistence;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Size(max = 40)
    private String firstName;

    @Size(max = 40)
    private String lastName;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    @Column(unique = true)
    private String email;

    @Size(max = 40)
    private String phoneNumber;

    @Size(max = 40)
    private String district;

    @Size(max = 100)
    private String organisation;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User() {
    }

    public User(final String email, final UserRole role) {
        this.email = email;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return super.equals(user) && Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(email, user.email) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(district, user.district) &&
                Objects.equals(organisation, user.organisation) &&
                role == user.role;
    }
}
