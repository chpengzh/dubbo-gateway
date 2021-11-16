package org.apache.dubbo.gateway.server.chain.invoker;

import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.gateway.api.constants.ApiParamType;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

final class RpcDataUtils {

    static Object fixRpcResultClass(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Map) { //对象删除
            Map objMap = (Map) object;
            objMap.remove("class");
            Set keys = objMap.keySet();
            for (Object key : keys) {
                Object value = objMap.get(key);
                Object fixValue = fixRpcResultClass(value);
                //noinspection unchecked
                objMap.put(key, fixValue);
            }
            return objMap;
        }
        if (object instanceof Collection) { //集合删除
            Collection c = (Collection) object;
            for (Object obj : c) {
                fixRpcResultClass(obj);
            }
            return c;
        }
        if (object.getClass().isArray()) { //数组删除
            //校验元素是否基本类型
            //基本类型无需处理
            if (object.getClass().getComponentType().isPrimitive()) {
                return object;
            }

            Object[] objs = (Object[]) object;
            for (Object obj : objs) {
                fixRpcResultClass(obj);
            }
            return objs;
        }
        return object;
    }

    static Object convert(Object httpValue, String name, int type) {
        Object invokeValue;
        //String
        if (type == ApiParamType.STRING.getCode()) {
            invokeValue = httpValue;
        } else if (type == ApiParamType.P_INT.getCode()) {
            //说明：使用blank判断，避免客户端或者测试用例使用空字符串
            invokeValue = Integer.parseInt(httpValue.toString());
        } else if (type == ApiParamType.W_INT.getCode()) {
            if (httpValue == null || StringUtils.isBlank(httpValue.toString())) {
                invokeValue = null;
            } else {
                invokeValue = Integer.parseInt(httpValue.toString());
            }
        } else if (type == ApiParamType.P_DOUBLE.getCode()) {
            invokeValue = Double.parseDouble(httpValue.toString());
        } else if (type == ApiParamType.W_DOUBLE.getCode()) {
            if (httpValue == null || StringUtils.isBlank(httpValue.toString())) {
                invokeValue = null;
            } else {
                invokeValue = Double.parseDouble(httpValue.toString());
            }
        } else if (type == ApiParamType.P_BYTE.getCode()) {
            invokeValue = Byte.parseByte(httpValue.toString());
        } else if (type == ApiParamType.W_BYTE.getCode()) {
            if (httpValue == null || StringUtils.isBlank(httpValue.toString())) {
                invokeValue = null;
            } else {
                invokeValue = Byte.parseByte(httpValue.toString());
            }
        } else if (type == ApiParamType.P_FLOAT.getCode()) {
            invokeValue = Float.parseFloat(httpValue.toString());
        } else if (type == ApiParamType.W_FLOAT.getCode()) {
            if (httpValue == null || StringUtils.isBlank(httpValue.toString())) {
                invokeValue = null;
            } else {
                invokeValue = Float.parseFloat(httpValue.toString());
            }
        } else if (type == ApiParamType.P_LONG.getCode()) {
            invokeValue = Long.parseLong(httpValue.toString());
        } else if (type == ApiParamType.W_LONG.getCode()) {
            if (httpValue == null || StringUtils.isBlank(httpValue.toString())) {
                invokeValue = null;
            } else {
                invokeValue = Long.parseLong(httpValue.toString());
            }
        } else if (type == ApiParamType.P_SHORT.getCode()) {
            invokeValue = Short.parseShort(httpValue.toString());
        } else if (type == ApiParamType.W_SHORT.getCode()) {
            if (httpValue == null || StringUtils.isBlank(httpValue.toString())) {
                invokeValue = null;
            } else {
                invokeValue = Short.parseShort(httpValue.toString());
            }
        } else if (type == ApiParamType.P_BOOLEAN.getCode()) {
            String boolStr = httpValue.toString().trim();
            //修复boolean parse异常
            ApiParamType apt = ApiParamType.getByCode(type);
            if (StringUtils.isBlank(boolStr)) {
                throw new RuntimeException("Parse parameter error"
                        + ";type:" + (apt == null ? type : apt.getClassName())
                        + ",name:" + name + ",value:" + httpValue);
            } else {
                if (boolStr.equalsIgnoreCase("true")) {
                    invokeValue = true;
                } else if (boolStr.equalsIgnoreCase("false")) {
                    invokeValue = false;
                } else {
                    throw new RuntimeException("value is not type"
                            + ":'" + apt.getClassName()
                            + ", param name:" + name + ",param value:" + boolStr);
                }
            }
        } else if (type == ApiParamType.W_BOOLEAN.getCode()) {
            ApiParamType apt = ApiParamType.getByCode(type);
            if (httpValue == null) {
                invokeValue = null;
            } else {
                String boolStr = httpValue.toString();
                //修正
                if (StringUtils.isBlank(httpValue.toString())) {
                    invokeValue = null;
                } else {
                    if (boolStr.equalsIgnoreCase("true")) {
                        invokeValue = Boolean.TRUE;
                    } else if (boolStr.equalsIgnoreCase("false")) {
                        invokeValue = Boolean.FALSE;
                    } else {
                        throw new RuntimeException("value is not type"
                                + ":'" + apt.getClassName()
                                + ",param name:" + name
                                + ",param value:" + boolStr);
                    }
                }
            }
        } else if (type == ApiParamType.FILE.getCode()) {
            invokeValue = httpValue;
        } else {
            throw new RuntimeException("Unsupported parameter type"
                    + ":'" + type
                    + ", param name:" + name);
        }
        return invokeValue;
    }

}