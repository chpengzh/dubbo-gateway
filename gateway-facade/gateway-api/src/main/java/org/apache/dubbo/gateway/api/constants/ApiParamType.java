package org.apache.dubbo.gateway.api.constants;

/**
 * 参数类型
 */
public enum ApiParamType {

    //基本类型
    P_INT(10, "int", "int"),
    P_LONG(11, "long", "long"),
    P_SHORT(12, "short", "short"),
    P_BYTE(13, "byte", "byte"),
    P_CHAR(14, "char", "char"),
    P_FLOAT(15, "float", "float"),
    P_DOUBLE(16, "double", "double"),
    P_BOOLEAN(17, "boolean", "boolean"),

    //包装类型
    W_INT(20, "Integer", "java.lang.Integer"),
    W_LONG(21, "Long", "java.lang.Long"),
    W_SHORT(22, "Short", "java.lang.Short"),
    W_BYTE(23, "Byte", "java.lang.Byte"),
    W_CHAR(24, "Character", "java.lang.Character"),
    W_FLOAT(25, "Float", "java.lang.Float"),
    W_DOUBLE(26, "Double", "java.lang.Double"),
    W_BOOLEAN(27, "Boolean", "java.lang.Boolean"),

    STRING(50, "string", "java.lang.String"),
    FILE(51, "byte[]", "byte[]"),
    OBJECT(60, "Object", "json object");

    private final int code;
    private final String name;
    private final String className;

    ApiParamType(int code, String name, String className) {
        this.code = code;
        this.name = name;
        this.className = className;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public static ApiParamType getByCode(int code) {
        ApiParamType[] values = ApiParamType.values();
        for (ApiParamType v : values) {
            if (v.getCode() == code) {
                return v;
            }
        }
        return P_INT;
    }

    public static ApiParamType getByClassName(String className) {
        ApiParamType[] values = ApiParamType.values();
        for (ApiParamType v : values) {
            if (v.getClassName().equals(className)) {
                return v;
            }
        }
        return P_INT;
    }
}
