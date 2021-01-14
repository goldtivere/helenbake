package com.helenbake.helenbake.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helenbake.helenbake.domain.enums.GenericStatus;


import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "tbuser")
public class User extends BaseEntity{

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "first_name")
    private String firstName;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "last_name")
    private String lastName = "";

    @NotNull
    @Pattern(regexp="(\\+)?[0-9]{11,20}$")
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @JsonIgnore
    @Column
    @Lob
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] profileImage;
    @JsonIgnore
    @Transient
    private String pictureBase64;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GenericStatus status = GenericStatus.INACTIVE;
    @JsonIgnore
    @Column(nullable = false)
    private Boolean deleted = Boolean.FALSE;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    @JsonIgnore
    @Transient
    private String passcode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public String getPictureBase64() {
        return pictureBase64;
    }

    public void setPictureBase64(String pictureBase64) {
        this.pictureBase64 = pictureBase64;
    }

    public GenericStatus getStatus() {
        return status;
    }

    public void setStatus(GenericStatus status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }
}
