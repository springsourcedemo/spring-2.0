package com.chuan.spring.formework.aop.support;

import com.chuan.spring.formework.aop.GPAopConfig;
import com.chuan.spring.formework.aop.aspect.GPAfterReturningAdvice;
import com.chuan.spring.formework.aop.aspect.GPAfterThrowingAdvice;
import com.chuan.spring.formework.aop.aspect.GPMethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author:曲终、人散
 * Date:2019/5/5 21:43
 */
public class GPAdvisedSupport {

    private Class targetClass;

    private Object target;

    private Pattern pointCutClassPattern;

    private transient Map<Method, List<Object>> methodCache;

    private GPAopConfig config;



    public GPAdvisedSupport(GPAopConfig config) {
        this.config = config;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));

        methodCache = new HashMap<Method, List<Object>>();
        Pattern pattern = Pattern.compile(pointCut);

        try{
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }

            //在这里得到的都是原生方法
            for (Method m : targetClass.getMethods()) {
                String methodString = m.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //能满足切面规则的类，添加到AOP配置中
                    List<Object> advices = new LinkedList<Object>();

                    //前置通知
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore().trim()))){
                        advices.add(new GPMethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }

                    //后置通知
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter().trim()))){
                        advices.add(new GPAfterReturningAdvice(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }

                    //异常通知
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow().trim()))){
                        GPAfterThrowingAdvice afterThrowingAdvice = new
                                GPAfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow()),
                                aspectClass.newInstance());

                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdvice);
                    }
                    methodCache.put(m,advices);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorAndDynamicInterceptionAdvice(Method method,Class<?> targetClass) throws Exception{
        List<Object> cache = methodCache.get(method);
        if(cache == null){
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cache = methodCache.get(m);
            this.methodCache.put(m,cache);
        }
        return cache;
    }


    public Pattern getPointCutClassPattern() {
        return pointCutClassPattern;
    }

    public void setPointCutClassPattern(Pattern pointCutClassPattern) {
        this.pointCutClassPattern = pointCutClassPattern;
    }

    public Map<Method, List<Object>> getMethodCache() {
        return methodCache;
    }

    public void setMethodCache(Map<Method, List<Object>> methodCache) {
        this.methodCache = methodCache;
    }

    public GPAopConfig getConfig() {
        return config;
    }

    public void setConfig(GPAopConfig config) {
        this.config = config;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
