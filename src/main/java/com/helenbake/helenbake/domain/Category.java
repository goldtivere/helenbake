package com.helenbake.helenbake.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbcategory")
public class Category extends BaseEntity {

    @NotNull
    @Column(name = "name")
    private String name;


    //    @Column(name = "description", unique = true)
    @NotNull
    @Column(name = "description")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


