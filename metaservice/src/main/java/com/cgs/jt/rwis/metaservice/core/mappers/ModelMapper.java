package com.cgs.jt.rwis.metaservice.core.mappers;

import com.cgs.jt.rwis.metaservice.api.ModelDTO;
import com.cgs.jt.rwis.metaservice.db.entity.Model;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface ModelMapper {
    ModelDTO toDto(Model model);
    List<ModelDTO> toDtoList(List<Model> modelList);
    Model fromDto(ModelDTO modelDTO);
}
