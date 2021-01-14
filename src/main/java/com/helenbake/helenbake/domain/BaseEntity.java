package com.helenbake.helenbake.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @Column(name = "createdBy")
    private Long createdBy;
    @JsonIgnore
    @Column(name = "datecreated")
    private LocalDateTime datecreated=LocalDateTime.now();
    @JsonIgnore
    @Column(name = "datecreatedAlone")
    private LocalDate datecreatedAlone=LocalDate.now();

    @JsonIgnore
    @Column(name = "updatedBy")
    private Long updatedBy;
    @JsonIgnore
    @Column(name = "dateupdated")
    private LocalDate dateupdated;
    @JsonIgnore
    @Column(name = "deletedBy")
    private Long deletedBy;
    @JsonIgnore
    @Column(name = "dateDeleted")
    private LocalDate dateDeleted;
    @JsonIgnore
    @Column(name = "isdeleted")
    private Boolean isdeleted=Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(LocalDateTime datecreated) {
        this.datecreated = datecreated;
    }

    public LocalDate getDatecreatedAlone() {
        return datecreatedAlone;
    }

    public void setDatecreatedAlone(LocalDate datecreatedAlone) {
        this.datecreatedAlone = datecreatedAlone;
    }

    public Boolean getIsdeleted() {
        return isdeleted;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDate getDateupdated() {
        return dateupdated;
    }

    public void setDateupdated(LocalDate dateupdated) {
        this.dateupdated = dateupdated;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public LocalDate getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(LocalDate dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    public Boolean isIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(Boolean isdeleted) {
        this.isdeleted = isdeleted;
    }
}
