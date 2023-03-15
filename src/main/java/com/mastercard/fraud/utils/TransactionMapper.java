package com.mastercard.fraud.utils;

import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")

public interface TransactionMapper {
    TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);


//    @Mappings({
//            @Mapping(source)
//    })
//    TransactionPO transactionPO(RequestPOJO requestPOJO);

}
