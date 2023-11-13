package ccm.server.convert;

import ccm.server.module.service.base.IUtilityService;

import java.util.Date;

public interface IValueConvertService extends IUtilityService {

    String[] getSupportedDateTimeFormats();

    String[] getSupportedYMDFormats();

    String getRegexTemplateOfDouble();

    String getRegexTemplateOfInteger();

    String[] getSupportedDateFormats();

    Double Double(Object str);

    Integer Integer(Object obj);

    Boolean isDateTime(Object obj);

    Boolean isYMD(Object obj);

    Date Date(Object obj);

    Date DateTime(Object obj);

    Date YMD(Object obj);

    Boolean isNumeric(Object obj);

    Boolean isDate(Object obj);

    Boolean isDouble(Object obj);

    Boolean isInteger(Object obj);

    String regexMatch(String str, String regEx) throws Exception;

    Boolean Boolean(Object obj);

}
