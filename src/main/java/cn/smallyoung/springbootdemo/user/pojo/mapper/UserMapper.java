package cn.smallyoung.springbootdemo.user.pojo.mapper;

import cn.smallyoung.springbootdemo.user.entity.User;
import cn.smallyoung.springbootdemo.user.pojo.UserRequest;
import cn.smallyoung.springbootdemo.user.pojo.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author smallyoung
 */
@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toResponse(User request);

    void init(@MappingTarget User entity, UserRequest request);
}
