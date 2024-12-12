package com.epam.training.gen.ai.plugins;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import lombok.extern.slf4j.Slf4j;

/**
 * A plugin for retrieving weather forecasts for a specified location.
 */
@Slf4j
public class WeatherForecastPlugin {

    /**
     * Gets the weather forecast for the specified location.
     *
     * @param location The location for which to retrieve the weather forecast.
     * @return A string representation of the weather forecast for the specified location.
     */
    @DefineKernelFunction(name = "getWeather", description = "Gets the weather forecast for the specified location.")
    public String getWeather(
            @KernelFunctionParameter(description = "Location for the weather forecast", name = "location")
            String location) {

        log.info("Weather Forecast Plugin was called for location: [{}]", location);
        // Simulated weather forecast, replace with actual API call
        return String.format("Weather forecast for %s: Sunny, 25°C", location);
    }
}
