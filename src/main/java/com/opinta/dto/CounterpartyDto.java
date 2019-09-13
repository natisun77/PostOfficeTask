package com.opinta.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CounterpartyDto {
    private long id;
    @Size(max = 255)
    private String name;
    private long postcodePoolId;
    @Size(max = 255)
    private String description;
}
