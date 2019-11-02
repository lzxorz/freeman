/* eslint-disable no-unused-vars */
import $ from '../utils/request'

const Index = '/sys/index/:userId'

export const index = (params = {}, pathVariable = {}) => $.get(Index, params, pathVariable)

/**
 * login func
 * parameter: {
 *     username: '',
 *     password: '',
 *     remember_me: true,
 *     captcha: ''
 * }
 * @param parameter
 * @returns {*}
 */
export const $login = params => $.post('/sys/login', params)
export const $phoneLogin = params => $.post('/sys/phoneLogin', params)
export const sendSms = params => $.post('/sys/sms', params)
export const $logout = params => $.post('/sys/logout', params)
export const forgePassword = params => $.post('/sys/forgePassword', params)
export const register = params => $.post('/sys/register', params)

// 权限管理
export const $getUserRouter = (params = {}, pathVariable = {}) => $.get('/sys/permission/:userId', params, pathVariable)
export const $getPermissions = (params = {}, pathVariable = {}) => $.get('/sys/permission', params, pathVariable)
export const $addPermission = (params = {}, pathVariable = {}) => $.post('/sys/permission', params, pathVariable)
export const $editPermission = (params = {}, pathVariable = {}) => $.put('/sys/permission', params, pathVariable)
export const $deletePermissionByIds = (params = {}, pathVariable = {}) => $.delete('/sys/permission/:ids', params, pathVariable)
// export const deletePermissionList = (params = {}, pathVariable = {}) => $.delete('/sys/permission/deleteBatch', params, pathVariable)
// export const queryTreeList = (params = {}, pathVariable = {}) => $.get('/sys/permission/queryTreeList', params, pathVariable)
// export const queryTreeListForRole = (params = {}, pathVariable = {}) => $.get('/sys/role/queryTreeList', params, pathVariable)
// export const queryListAsync = (params = {}, pathVariable = {}) => $.get('/sys/permission/queryListAsync', params, pathVariable)
// export const queryRolePermission = (params = {}, pathVariable = {}) => $.get('/sys/permission/queryRolePermission', params, pathVariable)
// export const saveRolePermission = (params = {}, pathVariable = {}) => $.post('/sys/permission/saveRolePermission', params, pathVariable)
// export const loadAllRoleIds = (params = {}, pathVariable = {}) => $.get('/sys/permission/loadAllRoleIds', params, pathVariable)
// export const getPermissionRuleList = (params = {}, pathVariable = {}) => $.get('/sys/permission/getPermRuleListByPermId', params, pathVariable)
// export const queryPermissionRule = (params = {}, pathVariable = {}) => $.get('/sys/permission/queryPermissionRule', params, pathVariable)

// 用户管理
export const $getusers = (params = {}, pathVariable = {}) => $.get('/sys/user', params, pathVariable)
export const $getUser = (params = {}, pathVariable = {}) => $.get('/sys/user/:userId', params, pathVariable)
export const $addUser = (params = {}, pathVariable = {}) => $.post('/sys/user', params, pathVariable)
// 验证用户是否可用true/false
export const $checkUsername = (params = {}, pathVariable = {}) => $.get('/sys/user/check/:username', params, pathVariable)
export const $editUser = (params = {}, pathVariable = {}) => $.put('/sys/user', params, pathVariable)
export const $deleteUserById = (params = {}, pathVariable = {}) => $.delete('/sys/user/:ids', params, pathVariable)
export const $changPassword = (params = {}, pathVariable = {}) => $.put('/sys/user/password', params, pathVariable)
export const $resetPassword = (params = {}, pathVariable = {}) => $.put('/sys/user/password/reset/:ids', params, pathVariable)

/*
export const queryUserRole = (params = {}, pathVariable = {}) => $.get('/sys/user/queryUserRole', params, pathVariable)
export const frozenBatch = (params = {}, pathVariable = {}) => $.put('/sys/user/frozenBatch', params, pathVariable)
*/

// 角色管理
export const $getRoles = (params = {}, pathVariable = {}) => $.get('/sys/role', params, pathVariable)
export const $addRole = (params = {}, pathVariable = {}) => $.post('/sys/role', params, pathVariable)
export const $editRole = (params = {}, pathVariable = {}) => $.put('/sys/role', params, pathVariable)
export const $getRolePerms = (params = {}, pathVariable = {}) => $.get('/sys/role/perms/:id', params, pathVariable)
export const $checkRoleCode = (params = {}, pathVariable = {}) => $.get('/sys/role/check/:code', params, pathVariable)
export const $deleteRoleById = (params = {}, pathVariable = {}) => $.delete('/sys/role/:ids', params, pathVariable)

// 部门管理
export const $getOrgs = (params = {}, pathVariable = {}) => $.get('/sys/org', params, pathVariable)
export const $addOrg = (params = {}, pathVariable = {}) => $.post('/sys/org', params, pathVariable)
export const $editOrg = (params = {}, pathVariable = {}) => $.put('/sys/org', params, pathVariable)
export const $deleteOrgById = (params = {}, pathVariable = {}) => $.delete('/sys/org/:ids', params, pathVariable)

// 数据字典
export const $getDicts = (params = {}, pathVariable = {}) => $.get('/sys/dict', params, pathVariable)
export const $addDict = (params = {}, pathVariable = {}) => $.post('/sys/dict', params, pathVariable)
export const $editDict = (params = {}, pathVariable = {}) => $.put('/sys/dict', params, pathVariable)
export const $delDict = (params = {}, pathVariable = {}) => $.delete('/sys/dict/:ids', params, pathVariable)
// 通过类型获取字典数组
export const $getDictItems = (params = {}, pathVariable = {}) => $.get('/sys/dict/dictItems', params, pathVariable)
export const $addDictItem = (params = {}, pathVariable = {}) => $.post('/sys/dict/dictItem', params, pathVariable)
export const $editDictItem = (params = {}, pathVariable = {}) => $.put('/sys/dict/dictItem', params, pathVariable)
export const $delDictItem = (params = {}, pathVariable = {}) => $.delete('/sys/dict/dictItem/:ids', params, pathVariable)

// 日志管理
// export const getLogList = params => reques.get('/sys/log/list', params)
// export const deleteLog = params => $.delete('/sys/log/delete', params)
// export const deleteLogList = params => $.delete('/sys/log/deleteBatch', params)

// 系统通告
// export const doReleaseData = params => $.get('/sys/annountCement/doReleaseData', params)
// export const doReovkeData = params => $.get('/sys/annountCement/doReovkeData', params)
// 获取系统访问量
// export const getLoginfo = params => $.get('/sys/loginfo', params)
// export const getVisitInfo = params => $.get('/sys/visitInfo', params)
// 数据日志访问
// export const getDataLogList = params => reques.get('/sys/dataLog/list', params)

// 根据部门主键查询用户信息
// export const queryUserByDepId = params => $.get('/sys/user/queryUserByDepId', params)

// 查询用户角色表里的所有信息
// export const queryUserRoleMap = params => $.get('/sys/user/queryUserRoleMap', params)
// 重复校验
// export const duplicateCheck = params => $.get('/sys/duplicate/check', params)

// 计划任务
export const $checkCron = (params = {}, pathVariable = {}) => $.get('/job/cron/check', params, pathVariable)
