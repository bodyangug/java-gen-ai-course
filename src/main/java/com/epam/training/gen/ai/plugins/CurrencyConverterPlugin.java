package com.epam.training.gen.ai.plugins;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;

/**
 * A plugin for converting currency from UAH (Ukrainian Hryvnia) to USD (United States Dollar).
 */
@Slf4j
public class CurrencyConverterPlugin {

    /**
     * Converts the specified amount in UAH to USD.
     *
     * @param amount The amount in UAH to convert to USD.
     * @return A string representation of the amount in USD or an error message if the input is invalid.
     */
    @DefineKernelFunction(name = "convertCurrency", description = "Converts UAH to USD.")
    public String convertCurrency(
            @KernelFunctionParameter(description = "Amount in UAH to convert to USD", name = "amount") String amount
    ) {
        log.info("Currency Converter Plugin was called with amount: [{}]", amount);
        try {
            double amountInUAH = Double.parseDouble(amount);
            double exchangeRate = 0.0274;
            double convertedAmount = amountInUAH * exchangeRate;
            return String.format("%f UAH is equal to %f USD", amountInUAH, convertedAmount);
        } catch (NumberFormatException e) {
            return "Invalid amount. Please provide a numeric value.";
        }
    }
}
