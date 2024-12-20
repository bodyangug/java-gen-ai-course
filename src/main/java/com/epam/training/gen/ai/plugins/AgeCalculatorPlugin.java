package com.epam.training.gen.ai.plugins;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

/**
 * A plugin for calculating age based on the given date of birth.
 */
@Slf4j
public class AgeCalculatorPlugin {

    /**
     * Calculates the age based on the provided date of birth.
     *
     * @param dateOfBirth The date of birth in the format yyyy-MM-dd.
     * @return A string representing the age in years, months, and days or an error message if the input is invalid.
     */
    @DefineKernelFunction(name = "calculateAge", description = "Calculates the age based on the given date of birth.")
    public String calculateAge(
            @KernelFunctionParameter(description = "Date of birth in the format yyyy-MM-dd", name = "dateOfBirth")
            String dateOfBirth
    ) {
        log.info("Age Calculator Plugin was called with date of birth: [{}]", dateOfBirth);
        try {
            LocalDate birthDate = LocalDate.parse(dateOfBirth);
            LocalDate currentDate = LocalDate.now();
            if (birthDate.isAfter(currentDate)) {
                return "The birth date cannot be in the future.";
            }
            Period age = Period.between(birthDate, currentDate);
            return String.format("The person is %d years, %d months, and %d days old.",
                    age.getYears(), age.getMonths(), age.getDays());
        } catch (DateTimeParseException e) {
            return "Invalid date format. Please provide the date in yyyy-MM-dd format.";
        }
    }
}
