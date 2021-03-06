package com.freeman.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.cache.redis.JedisDao;
import com.freeman.common.constants.Constants;
import com.freeman.common.utils.StrUtil;
import com.freeman.spring.data.repository.NativeSqlQuery;
import com.freeman.spring.data.utils.request.QueryRequest;
import com.freeman.sys.domain.SysDict;
import com.freeman.sys.domain.SysDictItem;
import com.freeman.sys.domain.SysRole;
import com.freeman.sys.repository.SysDictDataRepository;
import com.freeman.sys.repository.SysDictRepository;
import com.freeman.sys.service.ISysDictService;
import com.freeman.sys.service.ISysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
public class SysDictServiceImpl extends BaseServiceImpl<SysDictRepository, SysDict,Long> implements ISysDictService {

    @Autowired(required = false)
    private SysDictDataRepository dictDataDao;
    @Autowired(required = false)
    private ISysRoleService roleService;

    @Autowired(required = false)
    private JedisDao jedisDao;


    @Override
    public Page<SysDict> findAll(SysDict dict, QueryRequest queryRequest) {
        NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
                .from("sys_dict")
                .where(w -> w.eq("name", dict.getName()));
        return dao.findAllBySql(nativeSqlQuery, SysDict.class, queryRequest.getPageNo(), queryRequest.getPageSize());
    }


    @Override
    @Transactional
    public void deleteByIds(Long[] dictIds) {
        deleteByIds(dictIds);
    }

    @Override //@Cacheable(value = Constants.CACHE.DICT_PREFIX, key="#type")
    public List<SysDictItem> findDictItems(SysDictItem dictItem) {
        String dictType = dictItem.getType();
        String dictItems = jedisDao.hget(Constants.CACHE.DICT_PREFIX, dictType);
        if (StrUtil.isNotBlank(dictItems)) {
            return JSON.parseArray(dictItems, SysDictItem.class);
        }


        // TODO 角色　
        if ("sys_role".equals(dictType)) {
            List<SysDictItem> reslutList = CollectionUtil.newArrayList();
            List<SysRole> roleDictItems = roleService.findRoleDictItems(dictType);
            for (SysRole item : roleDictItems) {
                reslutList.add(SysDictItem.builder().label(item.getName()).value(item.getId().toString()).build());
            }
            return reslutList;
        }

        NativeSqlQuery nativeSqlQuery = NativeSqlQuery.builder()
                .select("sdi.*")
                .from("sys_dict_item sdi")
                .where(w -> w.eq("sdi.type", dictType))
                .orderBy("sdi.sort_no asc");
        List<SysDictItem> all = dictDataDao.findAllBySql(nativeSqlQuery, SysDictItem.class);

        jedisDao.hset(Constants.CACHE.DICT_PREFIX, dictType, JSON.toJSONString(all));

        return all;
    }

    @Override
    @Transactional
    public void saveDictItem(SysDictItem dictItem) {
        dictDataDao.save(dictItem);
    }

    @Override
    public void deleteDictItemById(List<Long> ids) {
        dictDataDao.deleteById(ids);
    }
}
