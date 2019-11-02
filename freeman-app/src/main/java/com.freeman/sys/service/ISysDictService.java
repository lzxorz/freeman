package com.freeman.sys.service;

import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysDict;
import com.freeman.sys.domain.SysDictItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ISysDictService extends IBaseService<SysDict, Long> {

    Page<SysDict> findAll(SysDict dict, Pageable pageable);

    void deleteByIds(Long[] dictIds);

    List<SysDictItem> findDictItems(SysDictItem dictItem);

    void saveDictItem(SysDictItem dictItem);

    void deleteDictItemById(List<Long> ids);
}
