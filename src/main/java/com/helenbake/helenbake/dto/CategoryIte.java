package com.helenbake.helenbake.dto;

import com.helenbake.helenbake.domain.Category;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

public class CategoryIte {
    private Long categoryId;

    private String name;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
