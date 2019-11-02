package com.freeman.sys.repository;


import com.freeman.spring.data.annotation.TemplateQuery;
import com.freeman.spring.data.repository.BaseRepository;
import com.freeman.sys.domain.SysOrg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysOrgRepository extends BaseRepository<SysOrg,Long> {

    /** 需要写模板查询 */
    @TemplateQuery
    List<SysOrg> findAllByTemplate(@Param("sysOrg") SysOrg sysOrg);

    /** 需要写模板查询 */
    @TemplateQuery
    List<SysOrg> findAllByTemplate(SysOrg sysOrg, Sort sort);

    /** 需要写模板查询 */
    @TemplateQuery
    Page<SysOrg> findAllByTemplate(SysOrg sysOrg, Pageable pageable);


    @Query(value = "SELECT * FROM sys_org WHERE FIND_IN_SET( id, (fnOrgAncestor(:id)) )" ,nativeQuery = true)
    List<SysOrg> findAllParentsAndById(@Param("id")Long id);

    /** 必须把INOUT参数拆成IN和OUT两个参数才可以用, 不想拆, 放弃使用这种方式　*/
    //@Query(value = "call prOrgChildren(:id,@parentId,@name,:type)", nativeQuery = true)
    //Object[] findAllChildren(@Param("id")Long id, @Param("type")String type);

}

