package com.freeman.sys.component;

import com.freeman.sys.domain.SysDictItem;
import com.freeman.sys.domain.SysUser;
import com.freeman.sys.domain.SysUserConfig;
import com.freeman.sys.domain.vo.SysDictItemVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper//(componentModel = "spring")
public interface O2O {
    O2O INSTANCE = Mappers.getMapper(O2O.class);

    // @Mapping(target = "createTime", source = "createTime")
    // @Mapping(target = "createdBy", source = "createdBy")
    // @Mapping(target = "updateTime", source = "updateTime")
    // @Mapping(target = "updateBy", source = "updateBy")
    // @Mapping(target = "delFlag", source = "delFlag")
    // @Mapping(target = "params", ignore = true)
    //List<SysUser> userDto2user(List<SysUserDto> userDtos);


    default SysUser config2User(SysUserConfig config) {
        if (config == null) return null;
        SysUser user = new SysUser();
        user.setConfig(config);
        return user;
    }


    //List<SysRole> roleDto2role(List<SysRoleDto> roleDtos);


    @Mappings({
            @Mapping(target ="label", source = "label"),
            @Mapping(target ="value", source = "value")
    })
    SysDictItemVo dictItem2Vo(SysDictItem dictItem);



}
