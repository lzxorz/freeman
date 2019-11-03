package com.freeman.sys.service.impl;

import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.sys.domain.SysUserConfig;
import com.freeman.sys.repository.SysUserConfigRepository;
import com.freeman.sys.service.ISysUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Transactional(readOnly = true, rollbackFor = Exception.class)
@Service
public class SysUserConfigServiceImpl extends BaseServiceImpl<SysUserConfigRepository,SysUserConfig,Long> implements ISysUserConfigService {

    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Override
    public SysUserConfig findByUserId(Long userId) {
        return dao.findByUserId(userId);
    }

    @Override
    @Transactional
    public void initDefaultUserConfig(Long userId) {
        SysUserConfig sysUserConfig = new SysUserConfig();
        sysUserConfig.setUserId(userId);
        sysUserConfig.setColor(SysUserConfig.DEFAULT_COLOR);
        sysUserConfig.setFixHeader(SysUserConfig.DEFAULT_FIX_HEADER);
        sysUserConfig.setFixSiderbar(SysUserConfig.DEFAULT_FIX_SIDERBAR);
        sysUserConfig.setLayout(SysUserConfig.DEFAULT_LAYOUT);
        sysUserConfig.setTheme(SysUserConfig.DEFAULT_THEME);
        sysUserConfig.setMultiPage(SysUserConfig.DEFAULT_MULTIPAGE);
        dao.save(sysUserConfig);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long... userIds) {
        deleteById(Arrays.asList(userIds));
    }

    @Override
    @Transactional
    public SysUserConfig save(SysUserConfig sysUserConfig) {
        SysUserConfig save = dao.save(sysUserConfig);
        return save;
    }

}
