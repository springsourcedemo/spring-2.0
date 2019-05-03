package com.chuan.spring.formework.webmvc.servlet;

import com.chuan.spring.formework.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 专人干专事
 * author:曲终、人散
 * Date:2019/5/3 19:59
 */
public class GPHandlerAdapter {

    public boolean supports(Object handler){ return (handler instanceof GPHandlerMapping);}

    GPModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        GPHandlerMapping handlerMapping = (GPHandlerMapping)handler;
        //把方法的形参列表和requset的参数列表所在顺序进行一一对应
        Map<String,Integer> paramIndexMapping = new HashMap<String,Integer>();

        //提取方法中加了注解的参数
        //把方法上的注解拿到，得到的是一个二维数组
        //因为一个参数可以有多个注解，而一个方法又有多个参数
        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length ; i ++) {
            for(Annotation a : pa[i]){
                if(a instanceof GPRequestParam){
                    String paramName = ((GPRequestParam) a).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }

        //提取方法中的request和response参数
        Class<?> [] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length ; i ++) {
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class ||
                    type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }

        //2、获得方法的形参列表
        Map<String,String[]> params = request.getParameterMap();

        //3、构造实参列表
        Object [] paramValues = new Object[paramsTypes.length];

        for (Map.Entry<String, String[]> parm : params.entrySet()) {
            String value = Arrays.toString(parm.getValue()).replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");

            if(!paramIndexMapping.containsKey(parm.getKey())){continue;}

            int index = paramIndexMapping.get(parm.getKey());
            //因为页面上传过来的值都是String类型的，而在方法定义的类型是千变万化的
            //要针对我们传过来的参数进行类型转换
            paramValues[index] = caseStringValue(value,paramsTypes[index]);
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        //4、从handler中取出controller、method，然后利用反射机制进行调用
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if(result == null || result instanceof Void){ return null; }

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GPModelAndView.class;
        if(isModelAndView){
            return (GPModelAndView) result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if(String.class == paramsType){
            return value;
        }
        //如果是int
        if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }
        else if(Double.class == paramsType){
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }
        //如果还有double或者其他类型，继续加if
        //这时候，我们应该想到策略模式了
        //在这里暂时不实现，希望小伙伴自己来实现

    }
}
