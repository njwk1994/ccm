package ccm.server.convert.impl;

import ccm.server.convert.IValueConvertService;
import ccm.server.module.impl.general.UtilityServiceImpl;
import ccm.server.util.CommonUtility;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("valueConverterService")
public class ValueConvertService extends UtilityServiceImpl implements IValueConvertService {
    public final static String REGEX_TEMPLATE_DOUBLE = "[+-]?\\d+[.]?\\d+";
    public final static String REGEX_TEMPLATE_INTEGER = "[+-]?\\d+";
    public final static String[] SUPPORTED_DATE_FORMATS = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "EEE MMM dd HH:mm:ss zzz yyyy", "yyyy-MM-dd HH:mm:ss.SSS"};
    public final static String[] SUPPORTED_YMD_FORMATS = new String[]{"yyyy-MM-dd", "yyyy/MM/dd"};

    public final static String[] SUPPORTED_CTS_FORMATS = new String[]{"EEE MMM dd HH:mm:ss z yyyy", "EEE MMM dd HH:mm:ss zzz yyyy"};
    public static ValueConvertService Instance;

    @PostConstruct
    public void init() {
        Instance = this;
    }


    @Override
    public Boolean Boolean(Object obj) {
        if (obj != null) {
            if (obj instanceof Boolean)
                return (Boolean) obj;
            String str = obj.toString();
            try {
                return Boolean.parseBoolean(str);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                return null;
            }
        }
        return null;
    }

    @Override
    public Double Double(Object obj) {
        Double result = null;
        if (obj != null) {
            if (obj instanceof Double)
                return (Double) obj;

            try {
                String str = obj.toString();
                String actualValue = this.regexMatch(str, REGEX_TEMPLATE_DOUBLE);
                result = Double.parseDouble(actualValue);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                result = null;
            }
        }
        return result;
    }


    @Override
    public Integer Integer(Object obj) {
        Integer result = null;
        if (obj != null) {
            if (obj instanceof Integer)
                return (Integer) obj;
            try {
                String str = obj.toString();
                String actualValue = this.regexMatch(str, REGEX_TEMPLATE_INTEGER);
                result = Integer.parseInt(actualValue);
            } catch (Exception exception) {
                log.error(exception.getMessage());
                result = null;
            }
        }
        return result;
    }

    @Override
    public Boolean isDateTime(Object obj) {
        boolean result = false;
        if (obj != null) {
            if (obj instanceof Date)
                return true;
            String str = obj.toString();
            for (String t : SUPPORTED_DATE_FORMATS
            ) {
                try {
                    new SimpleDateFormat(t).parse(str);
                    result = true;
                    break;
                } catch (ParseException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    @Override
    public Boolean isYMD(Object obj) {
        boolean result = false;
        if (obj != null) {
            if (obj instanceof Date)
                return true;
            String str = obj.toString();
            for (String t : SUPPORTED_YMD_FORMATS
            ) {
                try {
                    new SimpleDateFormat(t).parse(str);
                    result = true;
                    break;
                } catch (ParseException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }

    @Override
    public Date Date(Object obj) {
        Date result = null;
        if (obj != null && !StringUtils.isEmpty(obj.toString())) {
            if (obj instanceof Timestamp) {
                return new Date(((Timestamp) obj).getTime());
            } else if (obj instanceof Date)
                return (Date) obj;
            String str = obj.toString();
            try {
                if (str.contains("CST")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    result = sdf.parse(str);
                } else {
                    result = DateUtils.parseDate(str, this.getSupportedDateFormats());
                }
            } catch (ParseException e) {
                log.warn("parse date:" + str + " failed", ExceptionUtil.getMessage(e));
            }
        }
        return result;
    }

    @Override
    public Date DateTime(Object obj) {
        Date result = null;
        if (obj != null && !StringUtils.isEmpty(obj.toString())) {
            if (obj instanceof Timestamp) {
                return new Date(((Timestamp) obj).getTime());
            } else if (obj instanceof Date)
                return (Date) obj;
            String str = obj.toString();
            try {
                result = DateUtils.parseDate(str, SUPPORTED_DATE_FORMATS);
            } catch (ParseException e) {
                log.error(e.getMessage(), false);
                result = null;
            }
        }
        return result;
    }

    @Override
    public Date YMD(Object obj) {
        Date result = null;
        if (obj != null && !StringUtils.isEmpty(obj.toString())) {
            if (obj instanceof Timestamp) {
                return new Date(((Timestamp) obj).getTime());
            } else if (obj instanceof Date)
                return (Date) obj;
            String str = obj.toString();
            try {
                result = DateUtils.parseDate(str, SUPPORTED_YMD_FORMATS);
            } catch (ParseException e) {
                log.error(e.getMessage(), false);
                result = null;
            }
        }
        return result;
    }

    @Override
    public Boolean isNumeric(Object obj) {
        if (obj != null) {
            if (obj instanceof Double)
                return true;
            if (obj instanceof Integer)
                return true;
            String str = obj.toString();
            return this.isDouble(str) || this.isInteger(str);
        }
        return false;
    }

    @Override
    public Boolean isDate(Object obj) {
        if (obj != null) {
            if (obj instanceof Date)
                return true;
            String str = obj.toString();
            return this.isDateTime(str) || this.isYMD(str);
        }
        return false;
    }

    @Override
    public Boolean isDouble(Object obj) {
        if (obj != null) {
            if (obj instanceof Double)
                return true;
            String str = obj.toString();
            String s = Pattern.compile(REGEX_TEMPLATE_DOUBLE).matcher(str).replaceAll("");
            return StringUtils.isEmpty(s.trim());
        }
        return false;
    }

    @Override
    public Boolean isInteger(Object obj) {
        if (obj != null) {
            if (obj instanceof Integer)
                return true;
            String str = obj.toString();
            String s = Pattern.compile(REGEX_TEMPLATE_INTEGER).matcher(str).replaceAll("");
            return StringUtils.isEmpty(s.trim());
        }
        return false;
    }

    @Override
    public String regexMatch(String str, String regEx) throws Exception {
        Matcher m = Pattern.compile(regEx).matcher(str);
        String value = "";
        if (m.find()) {
            value = m.group(0);
        } else {
            throw new Exception("no part matched with provided Regex pattern");
        }
        return value;
    }

    @Override
    public String[] getSupportedDateTimeFormats() {
        return SUPPORTED_DATE_FORMATS;
    }

    @Override
    public String[] getSupportedYMDFormats() {
        return SUPPORTED_YMD_FORMATS;
    }

    @Override
    public String getRegexTemplateOfDouble() {
        return REGEX_TEMPLATE_DOUBLE;
    }

    @Override
    public String getRegexTemplateOfInteger() {
        return REGEX_TEMPLATE_INTEGER;
    }

    @Override
    public String[] getSupportedDateFormats() {
        List<String> formats = new ArrayList<>();
        formats.addAll(Arrays.asList(SUPPORTED_DATE_FORMATS));
        formats.addAll(Arrays.asList(SUPPORTED_YMD_FORMATS));
        formats.addAll(Arrays.asList(SUPPORTED_CTS_FORMATS));
        return CommonUtility.convertToArray(formats);
    }
}
