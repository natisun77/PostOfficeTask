package com.opinta.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
@Setter
public class ParcelItemDto {
    private long id;

    @Size(max = 255)
    private String name;

    @Min(1)
    private int quantity;

    private float weight;
    private BigDecimal price;
}
