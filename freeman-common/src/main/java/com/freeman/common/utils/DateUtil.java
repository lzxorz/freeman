package com.freeman.common.utils;

import cn.hutool.core.date.DateException;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.format.DateParser;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class DateUtil extends cn.hutool.core.date.DateUtil {

    /**时间格式化 */
    public static final ThreadLocal<SimpleDateFormat> dateTimeFormatter = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormatter;
        }
    };

    /**
     * 自带时分秒 ==> 直接转
     * 不带时分秒　==> 转换为当天的23:59:59
     *
     * @param dateStr
     * @return
     */
    public static DateTime parseDateTimeOrEndOfDay(String dateStr) {
        if (null != dateStr) {
            dateStr = dateStr.trim().replace("日", "");
            int length = dateStr.length();
            if (Validator.isNumber(dateStr)) {
                if (length == "yyyyMMddHHmmss".length()) {
                    return parse(dateStr, (DateParser) DatePattern.PURE_DATETIME_FORMAT);
                }

                if (length == "yyyyMMddHHmmssSSS".length()) {
                    return parse(dateStr, (DateParser) DatePattern.PURE_DATETIME_MS_FORMAT);
                }

                if (length == "yyyyMMdd".length()) {
                    DateTime parse = parse(dateStr, (DateParser) DatePattern.PURE_DATE_FORMAT);
                    return endOfDay(parse);
                }

                if (length == "HHmmss".length()) {
                    return parse(dateStr, (DateParser) DatePattern.PURE_TIME_FORMAT);
                }
            }

            if (length == "yyyy-MM-dd HH:mm:ss.SSS".length()) {
                return parse(dateStr, (DateParser) DatePattern.NORM_DATETIME_MS_FORMAT);
            }

            if (length == "yyyy-MM-dd HH:mm:ss".length()) {
                return parse(dateStr, (DateParser) DatePattern.NORM_DATETIME_FORMAT);
            }

            if (length == "yyyy-MM-dd HH:mm".length()) {
                return parse(dateStr, (DateParser) DatePattern.NORM_DATETIME_MINUTE_FORMAT);
            }

            if (length == "yyyy-MM-dd".length()) {
                DateTime parse = parse(dateStr, (DateParser) DatePattern.NORM_DATE_FORMAT);
                return endOfDay(parse);
            }

            if (length != "yyyy-MM-dd HH:mm:ss".length() && length != "yyyy-MM-dd HH:mm:ss".length() + 1) {
                if (length == "yyyy-MM-dd".length()) {
                    return parseDate(dateStr);
                } else if (length != "HH:mm:ss".length() && length != "HH:mm:ss".length() + 1) {
                    if (length != "yyyy-MM-dd HH:mm".length() && length != "yyyy-MM-dd HH:mm".length() + 1) {
                        if (length >= "yyyy-MM-dd HH:mm:ss.SSS".length() - 2) {
                            return parse(normalize(dateStr), (DateParser)DatePattern.NORM_DATETIME_MS_FORMAT);
                        } else {
                            throw new DateException("No format fit for date String [{}] !", new Object[]{dateStr});
                        }
                    } else {
                        return parse(normalize(dateStr), (DateParser)DatePattern.NORM_DATETIME_MINUTE_FORMAT);
                    }
                } else {
                    return parseTimeToday(dateStr);
                }
            } else {
                return dateStr.contains("T") ? parseUTC(dateStr) : parseDateTime(dateStr);
            }
        }
        return null;
    }


    private static String normalize(String dateStr) {
        if (StrUtil.isBlank(dateStr)) {
            return dateStr;
        } else {
            List<String> dateAndTime = StrUtil.splitTrim(dateStr, ' ');
            int size = dateAndTime.size();
            if (size >= 1 && size <= 2) {
                StringBuilder builder = StrUtil.builder();
                String datePart = ((String)dateAndTime.get(0)).replaceAll("[\\/.年月]", "-");
                datePart = StrUtil.removeSuffix(datePart, "日");
                builder.append(datePart);
                if (size == 2) {
                    builder.append(' ');
                    String timePart = ((String)dateAndTime.get(1)).replaceAll("[时分秒]", ":");
                    timePart = StrUtil.removeSuffix(timePart, ":");
                    builder.append(timePart);
                }

                return builder.toString();
            } else {
                return dateStr;
            }
        }
    }
}
