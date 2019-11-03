package com.freeman.sys.repository;


import com.freeman.spring.data.repository.BaseRepository;
import com.freeman.sys.domain.SysPermission;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysPermissionRepository extends BaseRepository<SysPermission,Long> {
    String permissionColumn = "sp.id,sp.parent_id,sp.name,sp.path,sp.component,sp.perms,sp.icon,sp.type,sp.sort_no,sp.hide,sp.description,sp.create_by,sp.create_time,sp.update_by,sp.update_time";
    String permissionFrom = "sys_permission sp";
    String treeColumn = "sp.id AS id,sp.id AS `key`,sp.parent_id AS parentId,sp.name AS `label`,sp.perms AS perms,sp.icon AS icon,sp.type AS type,sp.sort_no AS sortNo,sp.path,sp.component,sp.create_time AS createTime,sp.update_time AS updateTime";


    /**根据用户ID查询 所拥有的菜单/按钮 */
    @Query(nativeQuery = true, value =
            "SELECT p.* FROM sys_permission p WHERE " +
                    "p.id IN (SELECT DISTINCT id FROM sys_role_permission rp LEFT JOIN sys_user_role ur ON rp.role_id = ur.role_id AND ur.user_id = :userId)" +
                    "AND ORDER BY p.sort_no")
    List<SysPermission> findAllByUserId(@Param("userId")Long userId);

    /**根据用户ID和类型查询 所拥有的菜单/权限标识 */
    @Query(nativeQuery = true, value =
            "SELECT p.* FROM sys_permission p WHERE " +
            "p.id IN (SELECT DISTINCT id FROM sys_role_permission rp LEFT JOIN sys_user_role ur ON rp.role_id = ur.role_id AND ur.user_id = :userId) " +
            "AND p.type = :type ORDER BY p.sort_no")
    List<SysPermission> findAllByUserIdAndType(@Param("userId")Long userId, @Param("type")String type);


    /** 查找当前菜单/按钮关联的用户 ID */
    /*@Query(nativeQuery = true, value = "SELECT ur.user_id FROM sys_user_role ur LEFT JOIN sys_role_permission rp ON rp.role_id = ur.role_id WHERE rp.id = :id")
    List<Long> findUserIdById(@Param("id")Long id);*/

    /** 查找当前菜单/按钮关联的Children IDS */
    @Query(nativeQuery = true, value = "SELECT fnPermsChildren(:id) FROM DUAL")
    String findChildrenById(@Param("id")Long id);

    /** 根据ID删除菜单/按钮及其子系 */
    @Modifying(clearAutomatically=true)
    @Query(nativeQuery = true, value = "DELETE FROM sys_permission WHERE id IN (:ids)")
    void deleteSelfAndChildrenById(@Param("ids")List<Long> ids);


}