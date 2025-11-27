package cn.smallyoung.springbootdemo.user.dto.mapper;


import cn.smallyoung.springbootdemo.user.entity.Role;
import cn.smallyoung.springbootdemo.user.dto.RoleRequest;
import cn.smallyoung.springbootdemo.user.dto.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author smallyoung
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleResponse toResponse(Role request);

    void init(@MappingTarget Role entity, RoleRequest request);


}
