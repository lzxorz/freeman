package com.freeman.sys.service;


import com.freeman.common.base.service.IBaseService;
import com.freeman.sys.domain.SysLoginLog;

import java.util.List;

public interface ISysLoginLogService extends IBaseService<SysLoginLog, Long> {

    List<SysLoginLog> findOnlineUserByUserId(List<Long> onlineUserId);
}
