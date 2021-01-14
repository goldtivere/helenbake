package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.domain.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommandToCategory implements Converter<CategoryCommand, Category> {

    @Override
    public Category convert(CategoryCommand categoryCommand) {
        if(categoryCommand== null)
        {
            return null;
        }

        final Category category= new Category();
        category.setName(categoryCommand.getName());
        category.setDescription(categoryCommand.getDescription());


        return category;
    }
}
