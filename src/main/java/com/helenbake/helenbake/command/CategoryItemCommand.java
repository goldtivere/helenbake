package com.helenbake.helenbake.command;

import com.helenbake.helenbake.domain.Category;

import javax.validation.constraints.NotNull;

public class CategoryItemCommand {
    private Long id;
    @NotNull
    private Category category;
    @NotNull
    private String name;
    @NotNull
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
