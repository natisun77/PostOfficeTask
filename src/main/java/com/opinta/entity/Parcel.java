package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"shipment"})
public class Parcel {
    @Id
    @GeneratedValue
    private long id;

    @OneToMany
    @JoinColumn(name = "parcel_id")
    private List<ParcelItem> parcelItems = new ArrayList<>();

    @JoinColumn(name = "shipment_Id")
    @ManyToOne
    private Shipment shipment;

    @NotNull
    private float weight;
    @NotNull
    private float length;
    private float width;
    private float height;
    private BigDecimal declaredPrice;
    private BigDecimal price;

    public Parcel(float weight, float length, float width, float height,
                  BigDecimal declaredPrice) {
        this.weight = weight;
        this.length = length;
        this.width = width;
        this.height = height;
        this.declaredPrice = declaredPrice;
        this.price = BigDecimal.ZERO;
    }
}
