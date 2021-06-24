package com.ludensdomain.controller;

import com.ludensdomain.service.ChargeService;
import com.ludensdomain.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final ChargeService chargeService;

    @PostMapping("/{gameId}")
    public void buy(@PathVariable long gameId, long userId) {
        purchaseService.buy(gameId, userId);
    }

    @PostMapping("/{userId}")
    public void chargeCash(@PathVariable long userId, int amount) {
        chargeService.chargeCash(userId, amount);
    }

    @PutMapping("/{purchaseId}")
    public void refund(@PathVariable long purchaseId) {
        purchaseService.refund(purchaseId);
    }
}
