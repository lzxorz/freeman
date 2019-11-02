package com.freeman.sys.service;


import com.freeman.common.base.domain.Tree;
import com.freeman.common.base.service.IBaseService;
import com.freeman.common.router.VueRouter;
import com.freeman.sys.domain.SysPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISysPermissionService extends IBaseService<SysPermission, Long> {

    /**
     * 通过用户ID构建 Vue路由
     * @description
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-7-11 下午6:25
     * @Param
     * @return
     **/
    List<VueRouter> getUserRouters(Long userId);

    List<SysPermission> findAllByTemplate(SysPermission sysPermission);

    /**
     * 通过sql模板查询 构建树结构
     * @description
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-7-12 上午11:45
     * @Param
     * @return
     **/
    Tree findTree(SysPermission permission);

    Page<SysPermission> findAllByTemplate(SysPermission menu, Pageable pageable);

    /**
     * 通过ID 自己和Children
     * @description
     * @author 刘志新
     * @email  lzxorz@163.com
     * @date   19-7-11 下午6:25
     * @Param
     * @return
     **/
    void deleteSelfAndChildrenById(List<Long> ids);
}
