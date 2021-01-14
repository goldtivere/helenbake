package com.helenbake.helenbake.converters;


import com.helenbake.helenbake.command.CategoryCommand;
import com.helenbake.helenbake.domain.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryToCommand implements Converter<Category, CategoryCommand> {
    @Override
    public CategoryCommand convert(Category category) {
        if(category== null)
        {
            return null;
        }
        final CategoryCommand categoryCommand= new CategoryCommand();
        categoryCommand.setId(category.getId());
        categoryCommand.setName(category.getName());
        categoryCommand.setDescription(category.getDescription());

        return categoryCommand;
    }
}
