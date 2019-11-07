package com.freeman.sys.service.impl;


import com.freeman.common.base.domain.Tree;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.StrUtil;
import com.freeman.common.base.domain.TreeUtil;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.sys.domain.SysOrg;
import com.freeman.sys.repository.SysOrgRepository;
import com.freeman.sys.service.ISysOrgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
        List<SysOrg> orgs = findAllByNativeSql(org,sort);
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
        List<SysOrg> orgs = findAllByNativeSql(org, null);
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

    private List<SysOrg> findAllByNativeSql(SysOrg org, Sort sort) {

        Object createTime = org.getParams("createTime"); //传进来是数组
        String orderBy = sort != null ? StrUtil.camelStr2UnderlineStr(sort.toString().replace(":", " ")) : null;

        NativeSqlQuery nativeSql = NativeSqlQuery.builder()
                .from("sys_org so")
                .eq("so.id", org.getId())
                .contains("so.name", org.getName())
                .between( "date_format(so.create_time,'%Y-%m-%d')", createTime)
                .sqlStrPart((String)org.getParams("dataScope"))
                .orderBy(orderBy!=null ? ("so." + orderBy) : "")
                .build();

        return dao.findAllByNativeSql(nativeSql, SysOrg.class);
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
