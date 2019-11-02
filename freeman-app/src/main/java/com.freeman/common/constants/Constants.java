package com.freeman.common.constants;


/**
 * 常量
 * @author 刘志新
 */
public interface Constants {

	/**
	 * 这种方式要写很多 ==> public static final
	 */
	/*public class Constants {
		public static class STATE {
			public static final long A = 600;
			public static final long B = 600;
			public static final long C = 600;
			public static final long D = 600;
			public static final long E = 600;
			public static final long F = 600;
			public static final long G = 600;
		}
	}*/


	interface CACHE {
		// 用户缓存前缀
		String USER_PREFIX = "freeman:user";
		// 用户角色缓存前缀
		String ROLE_PREFIX = "freeman:role";
		// 用户角色缓存前缀
		String USER_ROLE_PREFIX = "freeman:user:role";
		// 用户权限缓存前缀
		String PERMISSION_PREFIX = "freeman:permission";
		// 用户权限缓存前缀
		String ROLE_PERMISSION_PREFIX = "freeman:role:permission";
		// 字典缓存前缀 hash
		String DICT_PREFIX = "freeman:dict";
	}

	interface USER {
		// 登录用户的 jwt
		String LOGIN_JWT_PREFIX = "freeman:jwt:";
		// 存储在线用户的 zset前缀
		String ACTIVE_USERS_ZSET_PREFIX = "freeman:user:active";

		/** 用户状态 */
		String STATUS_DISABLE = "0"; // 锁定
		String STATUS_ENABLE = "1"; // 有效

		/** 密码长度限制 */
		int PASSWORD_MIN_LENGTH = 5;
		int PASSWORD_MAX_LENGTH = 20;

		/** 手机号码格式限制 */
		String MOBILE_PHONE_NUMBER_PATTERN = "^0{0,1}(13[0-9]|15[0-9]|14[0-9]|18[0-9])[0-9]{8}$";

		/** 邮箱格式限制 */
		String EMAIL_PATTERN = "^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?";
	}

	interface DATA_SCOPE {

		/** 数据权限过滤关键字 */
		String FIELD = "dataScope";

		/** 全部数据权限 */
		String ALL = "0";

		/** 自定义数据权限 */
		String CUSTOM = "1";

		/** 本公司及以下数据权限 */
		String COMPANY_AND_CHILD = "2";

		/** 本公司数据权限 */
		String COMPANY = "3";

		/** 本部门及以下数据权限 */
		String DEPT_AND_CHILD = "4";

		/** 本部门数据权限 */
		String DEPT = "5";

		/** 仅本人数据 */
		String SELF = "6";

		/** 无 */
		String NO = "7";
	}

	interface PERMISSION {
		/** 菜单 */
		String TYPE_MENU = "1";
		/** 按钮 */
		String TYPE_BUTTON = "2";

		/** 查询 */
		String LIST = "list";

		/** 显示 */
		String VIEW = "view";

		/** 新增 */
		String ADD = "add";

		/** 修改 */
		String EDIT = "edit";

		/** 删除 */
		String DELETE = "delete";

		/** 导出 */
		String EXPORT = "export";
	}

    interface ORG {
        String TYPE_ALL= "0"; // 公司和部门
        String TYPE_COMMPANY= "1"; // 公司
        String TYPE_DEPT = "2"; // 部门
    }



	interface OTHER {
		// 网络资源 Url
		String MEIZU_WEATHER_URL = "http://aider.meizu.com/app/weather/listWeather";
		String MRYW_TODAY_URL = "https://interface.meiriyiwen.com/article/today";
		String MRYW_DAY_URL = "https://interface.meiriyiwen.com/article/day";
		String TIME_MOVIE_HOT_URL = "https://api-m.mtime.cn/Showtime/LocationMovies.api";
		String TIME_MOVIE_DETAIL_URL = "https://ticket-api-m.mtime.cn/movie/detail.api";
		String TIME_MOVIE_COMING_URL = "https://api-m.mtime.cn/Movie/MovieComingNew.api";
		String TIME_MOVIE_COMMENTS_URL = "https://ticket-api-m.mtime.cn/movie/hotComment.api";
	}

}
