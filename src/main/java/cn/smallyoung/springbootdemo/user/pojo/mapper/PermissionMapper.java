package cn.smallyoung.springbootdemo.user.pojo.mapper;


import cn.smallyoung.springbootdemo.user.entity.Permission;
import cn.smallyoung.springbootdemo.user.pojo.PermissionRequest;
import cn.smallyoung.springbootdemo.user.pojo.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author smallyoung
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {

    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionResponse toResponse(Permission request);

    void init(@MappingTarget Permission entity, PermissionRequest request);

}
