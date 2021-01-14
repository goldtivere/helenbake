package com.helenbake.helenbake.converters;

import org.springframework.core.convert.converter.Converter;
import com.helenbake.helenbake.command.CategoryItemCommand;
import com.helenbake.helenbake.domain.CategoryItem;
import org.springframework.stereotype.Component;

@Component
public class CategoryItemToCommand implements Converter<CategoryItem, CategoryItemCommand> {

    @Override
    public CategoryItemCommand convert(CategoryItem value) {
        if(value== null)
        {
            return null;
        }
        final CategoryItemCommand categoryCommand= new CategoryItemCommand();
        categoryCommand.setId(value.getId());
        categoryCommand.setCategory(value.getCategory());
        categoryCommand.setName(value.getName());
        categoryCommand.setDescription(value.getDescription());

        return  categoryCommand;
    }
}
