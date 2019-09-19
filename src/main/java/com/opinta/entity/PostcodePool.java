package com.opinta.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * PostcodePool holds all postcodes ("00000"-"99999") and pool of the inner numbers for each postcode
 * It shouldn't have field like Client or PostOffice.
 * Client and PostOffice should have reference to it instead
 */
@Entity
@Data
@NoArgsConstructor
public class PostcodePool {
    @Id
    @GeneratedValue
    private long id;
    @NotNull
    @Size(min = 5, max = 5)
    private String postcode;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "postcode_pool_id")
    private List<BarcodeInnerNumber> barcodeInnerNumbers = new ArrayList<>();
    private boolean closed;

    public PostcodePool(String postcode, boolean closed) {
        this.postcode = postcode;
        this.closed = closed;
    }
}
