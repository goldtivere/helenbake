package com.helenbake.helenbake.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbcategoryItem")
public class CategoryItem extends  BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false, name = "category_id")
    private Category  category;
    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "description", unique = true)
    private String description;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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
