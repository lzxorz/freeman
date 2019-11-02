package com.freeman.sys.service.impl;


import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.sys.domain.SysPost;
import com.freeman.sys.repository.SysPostRepository;
import com.freeman.sys.service.ISysPostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SysPostServiceImpl extends BaseServiceImpl<SysPostRepository, SysPost,Long> implements ISysPostService {


}
