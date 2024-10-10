package com.swd392.mentorbooking.dto.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateServiceRequestDTO {
    @NotNull(message = "Please input the price!")
    @DecimalMin(value = "10.0", message = "Price must be greater than 10")
    private double price;

    @NotNull(message = "Please input the description!")
    @NotBlank(message = "Please input the description!")
    private String description;
}
