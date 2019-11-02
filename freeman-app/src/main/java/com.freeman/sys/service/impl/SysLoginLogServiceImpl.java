package com.freeman.sys.service.impl;


import com.freeman.common.base.service.impl.BaseServiceImpl;
import com.freeman.common.utils.SpringContextUtil;
import com.freeman.common.utils.network.IPUtil;
import com.freeman.sys.domain.SysLoginLog;
import com.freeman.sys.repository.SysLoginLogRepository;
import com.freeman.sys.service.ISysLoginLogService;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SysLoginLogServiceImpl extends BaseServiceImpl<SysLoginLogRepository, SysLoginLog,Long> implements ISysLoginLogService {


    @Override
    @Transactional
    public SysLoginLog save(SysLoginLog loginLog) {
        loginLog.setLoginTime(new Date());
        HttpServletRequest request = SpringContextUtil.getHttpServletRequest();
        String ip = IPUtil.getIpAddr(request);
        loginLog.setIp(ip);
        loginLog.setLocation(IPUtil.getCityInfo(DbSearcher.BTREE_ALGORITHM, ip));
        return super.save(loginLog);
    }

    /**
     * 获取在线用户
     *
     * @param onlineUserId 在线用户ID
     * @return 在线用户的登录日志信息
     */
    @Override
    public List<SysLoginLog> findOnlineUserByUserId(List<Long> onlineUserId) {
        return dao.findOnlineUserByUserIdIn(onlineUserId);
    }
}
