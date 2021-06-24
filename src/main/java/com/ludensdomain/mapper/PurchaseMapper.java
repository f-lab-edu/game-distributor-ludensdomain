package com.ludensdomain.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PurchaseMapper {

    void buy(long gameId, long userId);

    void refund(long purchaseId);

}
