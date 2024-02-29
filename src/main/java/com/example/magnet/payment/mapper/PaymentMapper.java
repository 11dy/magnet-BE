package com.example.magnet.payment.mapper;

import com.example.magnet.payment.dto.ChargingHistoryDto;
import com.example.magnet.payment.dto.PaymentResponseDto;
import com.example.magnet.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PaymentMapper {

    public static List<ChargingHistoryDto> chargingHistoryToChargingHistoryResponses(List<Payment> chargingHistories) {
        if (chargingHistories == null) {
            return null;
        }

        return chargingHistories.stream()
                .map(chargingHistory -> {
                    return ChargingHistoryDto.builder()
                            .paymentHistoryId(chargingHistory.getId())
                            .amount(chargingHistory.getAmount())
                            .orderName(chargingHistory.getOrderName())
                            .createdAt(chargingHistory.getCreatedDate())
                            .isPaySuccessYN(chargingHistory.isPaySuccessYN())
                            .build();
                }).toList();
    }

    public static PaymentResponseDto PaymentToPaymentDto(Payment payment){
        return PaymentResponseDto.builder()
                .payType(payment.getPayType().toString())
                .orderName(payment.getOrderName())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedDate().toString())
                .build();
    }
}