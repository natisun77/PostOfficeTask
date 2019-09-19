package com.opinta.mapper;

import java.util.List;
import org.mapstruct.InheritInverseConfiguration;

/**
 * Base dto mapper
 *
 * @param <D>    type of Dto
 * @param <E> type of Entity
 */
public interface BaseMapper<D, E> {

    D toDto(E e);

    List<D> toDto(List<E> entities);

    @InheritInverseConfiguration
    E toEntity(D dto);

    List<E> toEntity(List<D> dtos);
}
