package com.asynctide.turnbridge.service.mapper;

import com.asynctide.turnbridge.domain.TrackRange;
import com.asynctide.turnbridge.service.dto.TrackRangeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TrackRange} and its DTO {@link TrackRangeDTO}.
 */
@Mapper(componentModel = "spring")
public interface TrackRangeMapper extends EntityMapper<TrackRangeDTO, TrackRange> {
    @Named("select2DTO")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TrackRangeDTO toSelect2DTO(TrackRange s);

    @Named("datatableDTO")
    TrackRangeDTO toDatatableDTO(TrackRange s);
}
