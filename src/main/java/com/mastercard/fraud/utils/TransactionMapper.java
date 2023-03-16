package com.mastercard.fraud.utils;

import com.mastercard.fraud.model.TransactionList;
import com.mastercard.fraud.model.TransactionPO;
import com.mastercard.fraud.model.transactionPost.AnalyzeRequest;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);

    @Mappings({
            @Mapping(target = "cardNum", source = "propertiesRoot.transaction.propertiesTransaction.cardNum.examples"),
            @Mapping(target = "amount", source = "propertiesRoot.transaction.propertiesTransaction.amount.examples")

    })
    TransactionList transactionPOList(AnalyzeRequest analyzeRequest);

}
