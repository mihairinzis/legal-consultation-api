package com.code4ro.legalconsultation.model.dto;

import com.code4ro.legalconsultation.model.persistence.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserDto extends BaseEntityDto {
    @Size(max = 40, message = "user.save.firstName.tooLong")
    private String firstName;
    @Size(max = 40, message = "user.save.lastName.tooLong")
    private String lastName;
    @Size(max = 40, message = "user.save.phoneNumber.tooLong")
    private String phoneNumber;
    @NotBlank
    @Size(max = 40)
    @Email
    @UniqueEmailConstraint
    private String email;
    @Size(max = 40, message = "user.save.district.tooLong")
    private String district;
    @Size(max = 100, message = "user.save.organisation.tooLong")
    private String organisation;
    @NotNull
    private UserRole role;
}
