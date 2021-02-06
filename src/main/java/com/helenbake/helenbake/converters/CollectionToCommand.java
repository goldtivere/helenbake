package com.helenbake.helenbake.converters;

import com.helenbake.helenbake.command.CollectionCommand;
import com.helenbake.helenbake.domain.Collections;
import com.helenbake.helenbake.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;



@Component
public class CollectionToCommand implements Converter<Collections, CollectionCommand> {
    @Autowired
    private UserRepository userRepository;
    @Override
    public CollectionCommand convert(Collections source) {
        if(source==null)
        {
            return null;
        }
        final CollectionCommand collectionCommand=new CollectionCommand();
        collectionCommand.setId(source.getId());
        collectionCommand.setCreatedBy(userRepository.findById(source.getCreatedBy()).get().getFirstName());
        collectionCommand.setCustomerName(source.getCustomerName());
        collectionCommand.setFromDate(source.getAccount().getFromDate());
        collectionCommand.setToDate(source.getAccount().getToDate());
        collectionCommand.setPaymentType(source.getPaymentType());
        collectionCommand.setReceiptNumber(source.getReceiptNumber());
        collectionCommand.setTotal(source.getTotal());
        collectionCommand.setDateCreated(source.getDatecreatedAlone());

        return collectionCommand;
    }
}
