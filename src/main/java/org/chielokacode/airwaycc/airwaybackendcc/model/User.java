package org.chielokacode.airwaycc.airwaybackendcc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chielokacode.airwaycc.airwaybackendcc.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @JsonIgnore
    private Long id;

    @Size(min = 3, message = "First name must be at least 3 characters")
    private String firstName;

    @Size(min = 3, message = "Last name must be at least 3 characters")
    private String lastName;

    @Email(message = "Please enter a valid email address")
    @NotEmpty(message = "Email cannot be empty")
    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private String password;

    @Transient
    private String confirmPassword;

    @NotEmpty(message = "Role cannot be empty")
    @Enumerated(EnumType.STRING)
    @JsonIgnore
    private Role userRole;
    @JsonIgnore
    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordMatching(){
        return password != null && password.equals(confirmPassword);
    }

    @JsonIgnore
    private Boolean isEnabled = false;

    private String gender;
    private String dateOfBirth;
    private String state;
    private String country;
    private String passportNumber;
    private String membershipNo;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
