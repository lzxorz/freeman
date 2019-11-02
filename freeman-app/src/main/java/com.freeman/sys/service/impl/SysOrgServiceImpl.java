package com.freeman.sys.service.impl;


import com.freeman.common.base.domain.Tree;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.TreeUtil;
import com.freeman.sys.domain.SysOrg;
import com.freeman.sys.repository.SysOrgRepository;
import com.freeman.sys.service.ISysOrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SysOrgServiceImpl extends BaseServiceImpl<SysOrgRepository, SysOrg,Long> implements ISysOrgService {


    @Override
    public Tree findOrgTableTree(SysOrg org, Sort sort) {
        List<SysOrg> orgs = dao.findAllByTemplate(org,sort);
        List<Tree> trees = orgs.stream().map(o -> {
            return Tree.builder()
            .id(o.getId())
            .parentId(o.getParentId())
            .label(o.getName())
            .sortNo(o.getSortNo()).type(o.getType())
            .createTime(o.getCreateTime()).updateTime(o.getUpdateTime()).build();
        }).collect(Collectors.toList());

        Tree orgTree = TreeUtil.build(trees);
        return orgTree;
    }

    @Override
    public Tree findOrgSelectTree(SysOrg org) {
        List<SysOrg> orgs = dao.findAllByTemplate(org);
        List<Tree> trees = orgs.stream().map(o -> {
            return Tree.builder()
                .id(o.getId())
                .parentId(o.getParentId())
                .label(o.getName())
                .build();
        }).collect(Collectors.toList());

        Tree orgTree = TreeUtil.build(trees);
        return orgTree;
    }



    @Override
    @Transactional
    public SysOrg save(SysOrg org) {
        if (org.isNew()) {
            Long parentId = org.getParentId();
            if (parentId == null) {
                org.setParentId(0L);
            }
        }
        return super.save(org);
    }

    /** 需要写模板查询(一个模板查询方法findAllByTemplate) */
    @Override
    public List<SysOrg> findAllByTemplate(SysOrg sysOrg){
        return dao.findAllByTemplate(sysOrg);
    }

    // 测试jpa调用mysql存储过程
    @Override
    @Transactional(readOnly = false)
    public void testStoredProcedure(Long pid) {
        StoredProcedureQuery query = entityManager.createNamedStoredProcedureQuery("findAllChildren")
                .setParameter("id",pid).setParameter("type","0");
        query.execute();  //通过getOutputParameterValue方法获取存储过程中的OUT参数值
        List<Object[]> allChildren = query.getResultList();
        if (!ObjectUtils.isEmpty(allChildren)) {
            allChildren.forEach(o -> {
                BigInteger id = (BigInteger)o[0];    //query.getOutputParameterValue("id");
                BigInteger parentId = (BigInteger)o[1]; //query.getOutputParameterValue("parentId");
                String name = String.valueOf(o[2]);  //query.getOutputParameterValue("name");
                String type = String.valueOf(o[3]);  //query.getOutputParameterValue("type");

                log.info("id={}, parentId={}, name={}, type={}",id, parentId, name, type);
                // id=1, parentId=0, name=能力有限公司, type=1
            });
        }
    }

}
