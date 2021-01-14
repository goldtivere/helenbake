package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.CategoryItemCommand;
import com.helenbake.helenbake.domain.CategoryItem;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CommandToCategoryItem implements Converter<CategoryItemCommand, CategoryItem> {

    @Override
    public CategoryItem convert(CategoryItemCommand source) {
        if(source == null)
        {
            return null;
        }
        final CategoryItem categoryItem = new CategoryItem();
        categoryItem.setCategory(source.getCategory());
        categoryItem.setName(source.getName());
        categoryItem.setDescription(source.getDescription());

        return  categoryItem;
    }
}
