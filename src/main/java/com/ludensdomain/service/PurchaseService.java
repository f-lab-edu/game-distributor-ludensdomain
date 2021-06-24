package com.ludensdomain.service;

import com.ludensdomain.mapper.PurchaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseMapper purchaseMapper;

    public void buy(long gameId, long userId) {
        purchaseMapper.buy(gameId, userId);
    }

    public void refund(long purchaseId) {
        purchaseMapper.refund(purchaseId);
    }
}
