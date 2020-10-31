package com.code4ro.legalconsultation.security.model;

import com.code4ro.legalconsultation.authentication.model.persistence.ApplicationUser;
import com.code4ro.legalconsultation.user.model.persistence.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class CurrentUser implements UserDetails {
    private UUID id;

    private String fullName;

    private String username;

    private UserRole role;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;


    public CurrentUser(UUID id, String fullName, String username, String email, String password, UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static CurrentUser create(ApplicationUser applicationUser) {
        return new CurrentUser(
                applicationUser.getId(),
                getFullName(applicationUser),
                applicationUser.getUsername(),
                applicationUser.getUser().getEmail(),
                applicationUser.getPassword(),
                applicationUser.getUser().getRole()
        );
    }

    private static String getFullName(final ApplicationUser applicationUser) {
        return String
                .format("%s %s", applicationUser.getUser().getFirstName(), applicationUser.getUser().getLastName());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
