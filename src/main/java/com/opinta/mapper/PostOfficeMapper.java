package com.opinta.mapper;

import com.opinta.dto.PostOfficeDto;
import com.opinta.entity.PostOffice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostOfficeMapper extends BaseMapper<PostOfficeDto, PostOffice> {

    @Override
    @Mappings({
            @Mapping(source = "address.id", target = "addressId"),
            @Mapping(source = "postcodePool.id", target = "postcodePoolId")})
    PostOfficeDto toDto(PostOffice postOffice);

    @Override
    @Mappings({
            @Mapping(source = "addressId", target = "address.id"),
            @Mapping(source = "postcodePoolId", target = "postcodePool.id")})
    PostOffice toEntity(PostOfficeDto postOfficeDto);
}
