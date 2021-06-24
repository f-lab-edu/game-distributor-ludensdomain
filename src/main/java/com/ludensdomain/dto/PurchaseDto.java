package com.ludensdomain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder
@AllArgsConstructor
public class PurchaseDto {

    long purchaseId;

    long userId;

    long gameId;

    int refund;

    Date purchaseDate;

}
