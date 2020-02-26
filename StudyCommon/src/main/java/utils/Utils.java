package utils;

import com.alibaba.fastjson.JSON;
import com.tunnelkey.tktim.infrastructure.LegendUtils;
import com.tunnelkey.tktim.model.Constant;
import com.tunnelkey.tktim.model.PageModel;
import com.tunnelkey.tktim.model.Pageable;
import com.tunnelkey.tktim.model.base.DictonaryModel;
import com.tunnelkey.tktim.model.common.UserCompanyModel;
import com.tunnelkey.tktim.model.schedule.WeekReportTimeRange;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @version: V1.0
 * @Description: 存放一些与业务无关的工具类
 * @author: dgp
 * @date: 2018-11-14 18:48
 */
public class Utils {

    private final static Pattern PREFIX_PATTERN = Pattern.compile("[a-z|A-Z|0-9]*[0-9]*[a-z|A-Z]");//DK 或 123D123K
    private final static Pattern LEGEND_PATTERN = Pattern.compile("[0-9]+\\+[0-9]+(\\.[0-9]+)?");//000+000.000

    /**
     * 获取对象除ID的参数
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<String> getProperties(T t) {
        if (!ObjectUtils.isEmpty(t)) {
            Field[] fields = t.getClass().getDeclaredFields();
            return Arrays.stream(fields).filter(field -> field.getName() != "UUID" && field.getName() != "Int32" && field.getName() != "Nullable")
                    .map(Field::getName).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * @throws
     * @Description:获取user和company的对应关系
     * @param: userCompanyModels
     * @date: 2018/11/23.12:22
     */
    public static Map<UUID, List<UUID>> transUserCompanyModeToMap(List<UserCompanyModel> userCompanyModels, List<UUID> userIdList) {
        Map<UUID, List<UUID>> userCompanyMap = new HashMap<>();
        userCompanyModels.stream().forEach(uc -> {
            userIdList.add(uc.UserId);
            if (userCompanyMap.containsKey(uc.CompanyId)) {
                userCompanyMap.get(uc.CompanyId).add(uc.UserId);
            } else {
                List<UUID> idList = new ArrayList<>();
                idList.add(uc.UserId);
                userCompanyMap.put(uc.CompanyId, idList);
            }
        });
        return userCompanyMap;
    }

    /**
     * @throws
     * @Description:string转double，并排序
     * @param: start, end
     * @date: 2018/12/26.10:30
     */
    public static List<Double> parseAndSortString(String start, String end) {
        List<Double> ret = new ArrayList<>();
        Double startD = null;
        Double endD = null;
        if (!StringUtils.isEmpty(start)) {
            startD = Double.parseDouble(start);
        }
        if (!StringUtils.isEmpty(end)) {
            endD = Double.parseDouble(end);
        }
        if (startD != null && endD != null && startD > endD) {
            Double tmp = startD;
            startD = endD;
            endD = startD;
        }
        if (startD != null) {
            ret.add(startD);
        }
        if (endD != null) {
            ret.add(endD);
        }
        return ret;
    }

    /**
     * 设置查询中 分页
     *
     * @param page  分页对象
     * @param query 查询条件
     */
    public static Query setQueryPage(Pageable page, Query query, PageModel retData, int count) {
        if (page == null) return query;
        long pageNum = page.PageNum;
        long pageSize = page.PageSize;

        if (pageNum > 0 && pageSize > 0) {
            query.skip((pageNum - 1) * pageSize).limit((int) pageSize);
            retData.PageNum = pageNum;
            retData.PageSize = pageSize;
            retData.setRecordCount(count);
            retData.PageCount = count;
        }
        return query;
    }

    public static String formateTimeToString(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(time);
    }


    public static Integer getGradeByString(String grade) {
        if (StringUtils.isEmpty(grade)) return null;
        return grade.length() > 1 ? Integer.parseInt(grade.substring(0, 1)) : Integer.parseInt(grade);
    }

    public static Integer getLevelByString(String level) {
        if (StringUtils.isEmpty(level)) return 1;
        return level.length() > 1 ? Integer.parseInt(level.substring(0, 1)) : Integer.parseInt(level);
    }


    public static Boolean isInt(String num) {
        if (StringUtils.isEmpty(num)) return false;
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(num);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String str) {
        if (StringUtils.isEmpty(str)) return false;
        Pattern pattern = Pattern.compile("^[0-9.]*");
        return pattern.matcher(str).matches();
    }

    public static String subString(String str, int maxLength) {
        if (!StringUtils.isEmpty(str)) {
            return str.substring(0, str.length() > maxLength ? maxLength : str.length());
        }
        return "";
    }

    public static Double formateDouble(Double length, int num) {
        BigDecimal bg = new BigDecimal(length);
        return bg.setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static int comparaTime(LocalDateTime o1, LocalDateTime o2, String diction) {
        if (diction.equals("DESC")) {
            if (o1 != null && o2 != null && o2.compareTo(o1) > 0) {
                return 1;
            }
            return -1;
        } else {
            if (o1 != null && o2 != null && o1.compareTo(o2) > 0) {
                return 1;
            }
            return -1;
        }
    }

    public static Map<String, Object> transLegend(String legend) {
//        legend = StringExtension.replaceBlank(legend);
//        Map<String, Object> ret = new HashMap<>();
//        char[] temp = legend.toCharArray();
//        int letterMaxIndex = -1;
//        for (int i = 0; i < temp.length; i++) {
//            if (Character.isLetter(temp[i])) {
//                letterMaxIndex = i;
//            }
//        }
//        if (letterMaxIndex >= 0) {
//            ret.put("prefix", legend.substring(0, letterMaxIndex + 1));
//            ret.put("legend", LegendUtils.formateLegendFromString(legend.substring(letterMaxIndex + 1, temp.length).replace("+", "")));
//        }

        Map<String, Object> ret = new HashMap<>();
        Matcher prefixMatcher = PREFIX_PATTERN.matcher(legend);
        Matcher legendMatcher = LEGEND_PATTERN.matcher(legend);
        String reallegend = "";
        if (prefixMatcher.find()) {
            ret.put("prefix", prefixMatcher.group());
        }
        if (legendMatcher.find()) {
            String[] split = legendMatcher.group().split("[\\+,\\.]");
                if (split.length >= 1) {
                    String s1 = split[1];
                    if (s1.length() == 1) {
                        s1 = "00" + s1;
                    }
                    if (s1.length() == 2) {
                        s1 = "0" + s1;
                    }
                    if (split.length > 2) {
                        reallegend = split[0] + s1 + "." + split[2];
                    } else {
                        reallegend = split[0] + s1;
                    }
            } else {
                reallegend = legendMatcher.group().replace("+", "");
            }
            ret.put("legend", LegendUtils.formateLegendFromString(reallegend));
        }
        return ret;
    }

    public static String formatLegentDouble(double legend) {
        String s = String.valueOf(legend);
        if (s.endsWith(".0"))
            s = s.replace(".0", "");
        return s;
    }

    /**
     * 功能描述:获取map字符串的第一个value
     *
     * @param: str
     * @return: String
     * @auther: dengguiping
     * @date: 2019/3/12
     */
    public static String getDictonaryModelFirstValueByString(String str) {
        if (StringUtils.isEmpty(str)) return null;
        DictonaryModel model = JSON.parseObject(str, DictonaryModel.class);
        if (model != null) {
            return model.values.get(0).value;
        }
        return null;
    }

    /**
     * 罗马数字转Int
     *
     * @param s
     * @return
     */
    public static int romanToInt(String s) {
        if (s.length() < 1) return 0;
        char[] strchar = s.toCharArray();
        int result = 0;
        int current = 0;
        int pre = singleRomanToInt(strchar[0]);
        int temp = pre;
        for (int i = 1; i < s.length(); i++) {
            current = singleRomanToInt(strchar[i]);
            if (current == pre)
                temp += current;
            else if (current > pre) {
                temp = current - temp;
            } else if (current < pre) {
                result += temp;
                temp = current;
            }
            pre = current;
        }
        result += temp;
        return result;
    }

    public static int singleRomanToInt(char c) {
        switch (c) {
            case 'I':
                return 1;
            case 'V':
                return 5;
            case 'X':
                return 10;
            case 'L':
                return 50;
            case 'C':
                return 100;
            case 'D':
                return 500;
            case 'M':
                return 1000;
            default:
                return 0;
        }
    }

    public static int yajiRomanToInt(String s) {
        int retInt = 0;
        switch (s) {
            case "III1":
                retInt = 1;
                break;
            case "III2":
                retInt = 2;
                break;
            case "IV1":
                retInt = 3;
                break;
            case "IV2":
                retInt = 4;
                break;
            case "V1":
                retInt = 5;
                break;
            case "V2":
                retInt = 6;
                break;
        }
        return retInt;
    }

    /**
     * 跟今天比较，是否在24H以内
     *
     * @param time
     * @return
     */
    public static Boolean is24HRange(LocalDateTime time) {
        if (time == null) return false;
        LocalDateTime now = LocalDateTime.now().minusHours(24);
        return now.isBefore(time);
    }

    public static String formateTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return time.format(formatter);
    }

    public static String getLastedWeek() {
        WeekReportTimeRange weekReportTimeRange = getLastedWeekTime(LocalDateTime.now());
        if (!ObjectUtils.isEmpty(weekReportTimeRange)) {
            return formateTime(weekReportTimeRange.WeekStart) + "到" + formateTime(weekReportTimeRange.WeekEnd);
        } else {
            LocalDateTime now = LocalDateTime.now();
            return formateTime(now);
        }
    }

    public static WeekReportTimeRange getLastedWeekTime(LocalDateTime time) {
        if (time == null) time = LocalDateTime.now();
        WeekReportTimeRange weekReportTimeRange = new WeekReportTimeRange();
        LocalDateTime startWeek = LocalDateTime.now();
        LocalDateTime endWeek = LocalDateTime.now().minusWeeks(1);
        if (time.getDayOfWeek() == DayOfWeek.MONDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusWeeks(1), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        }
        if (time.getDayOfWeek() == DayOfWeek.TUESDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusDays(8), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.MAX);
        }
        if (time.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusDays(9), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.MAX);
        }
        if (time.getDayOfWeek() == DayOfWeek.THURSDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        }
        if (time.getDayOfWeek() == DayOfWeek.FRIDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusDays(4), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        }
        if (time.getDayOfWeek() == DayOfWeek.SATURDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusDays(5), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        }
        if (time.getDayOfWeek() == DayOfWeek.SUNDAY) {
            startWeek = LocalDateTime.of(LocalDate.now().minusDays(6), LocalTime.MIN);
            endWeek = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        }
        weekReportTimeRange.WeekStart = startWeek;
        weekReportTimeRange.WeekEnd = endWeek;
        return weekReportTimeRange;
    }


    public static LocalDateTime getLastedWeekStartDate() {
        WeekReportTimeRange weekReportTimeRange = new WeekReportTimeRange();
        if (!ObjectUtils.isEmpty(weekReportTimeRange)) {
            return weekReportTimeRange.WeekStart;
        } else {
            return LocalDateTime.of(LocalDate.now().minusWeeks(1), LocalTime.MIN);
        }
    }

    public static LocalDateTime transTimeToDawn(LocalDateTime time) {
        return LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 0, 0, 0);
    }

    public static LocalDateTime transTimeToNight(LocalDateTime time) {
        return LocalDateTime.of(time.getYear(), time.getMonth(), time.getDayOfMonth(), 23, 59, 59);
    }

    public static int getSubstractDay(LocalDateTime start,LocalDateTime end){
        if(start == null || end == null)return 0;
        return (int) Math.abs(end.toLocalDate().toEpochDay() - start.toLocalDate().toEpochDay())+1;
    }

    public static String transLocalDateTime(LocalDateTime time){
        if(time == null)return "";
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return time.format(df);
    }

    public static String transPointAlarmStr(Integer alarmStatus){
        if(alarmStatus == Constant.Measure_Point_Green)return "绿色";
        if(alarmStatus == Constant.Measure_Point_Yellow)return "黄色";
        if(alarmStatus == Constant.Measure_Point_Orange)return "橙色";
        if(alarmStatus == Constant.Measure_Point_Red)return "红色";
        return "";
    }

    public static String transPointStatusStr(Integer status){
        if(status == Constant.Measure_Point_Open)return "开启";
        if(status == Constant.Measure_Point_Close)return "停用";
        if(status == Constant.Measure_Point_BreakDown)return "损坏";
        return "";
    }

    public static Integer getWeekNumber(LocalDateTime time){
        if(time == null)return 0;
        //使用DateTimeFormatter获取当前周数
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY,1);
        return time.get(weekFields.weekOfYear());
    }
}
