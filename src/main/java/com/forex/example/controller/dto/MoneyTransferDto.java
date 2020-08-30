package com.forex.example.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferDto {

    @NotNull
    @JsonProperty(required = true)
    private Long fromAccount;
    @NotNull
    @JsonProperty(required = true)
    private Long toAccount;
    @NotNull
    @JsonProperty(required = true)
    private Long amount;
}
