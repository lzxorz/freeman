package com.freeman.sys.service;


import com.freeman.common.base.domain.Tree;
import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysOrg;
import org.springframework.data.domain.Sort;

public interface ISysOrgService extends IBaseService<SysOrg,Long> {
    // 测试jpa调用mysql存储过程
    void testStoredProcedure(Long pid);

    Tree findOrgTableTree(SysOrg org, Sort sort);
    Tree findOrgSelectTree(SysOrg org);
}
