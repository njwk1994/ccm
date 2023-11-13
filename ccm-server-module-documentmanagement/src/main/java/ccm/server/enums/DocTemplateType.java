package ccm.server.enums;

/**
 * @author HuangTao
 * @version 1.0
 * @since 2022/3/30 10:12
 */
public enum DocTemplateType {

    DD("详设", "图纸设计数据导入模板-详设"),//详设
    SD("加设", "图纸设计数据导入模板-加设")// 加设
    ;

    private final String type;
    private final String templateName;

    DocTemplateType(String type, String templateName) {
        this.type = type;
        this.templateName = templateName;
    }

    public String getType() {
        return type;
    }

    public String getTemplateName() {
        return templateName;
    }

    public static DocTemplateType typeValueOf(String type) {
        DocTemplateType result = null;
        for (DocTemplateType value : DocTemplateType.values()) {
            if (type.equalsIgnoreCase(value.getType())) {
                result = value;
            }
        }
        if (null == result) {
            throw new RuntimeException("获取模版类型失败!");
        }
        return result;
    }



}
