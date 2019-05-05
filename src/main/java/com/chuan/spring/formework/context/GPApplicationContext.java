package com.chuan.spring.formework.context;

import com.chuan.spring.formework.annotation.GPAutowired;
import com.chuan.spring.formework.annotation.GPController;
import com.chuan.spring.formework.annotation.GPService;
import com.chuan.spring.formework.aop.GPAopConfig;
import com.chuan.spring.formework.aop.GPAopProxy;
import com.chuan.spring.formework.aop.GPCglibAopProxy;
import com.chuan.spring.formework.aop.GPJdkDynamicAopProxy;
import com.chuan.spring.formework.aop.support.GPAdvisedSupport;
import com.chuan.spring.formework.core.GPBeanFactory;
import com.chuan.spring.formework.beans.GPBeanWrapper;
import com.chuan.spring.formework.beans.config.GPBeanDefinition;
import com.chuan.spring.formework.beans.config.GPBeanPostProcessor;
import com.chuan.spring.formework.beans.support.GPBeanDefinitionReader;
import com.chuan.spring.formework.beans.support.GPDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * ioc/di/aop
 * author:曲终、人散
 * Date:2019/4/24 19:56
 */
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private String[] configLoactions;

    private GPBeanDefinitionReader reader;

    //单例的ioc容器缓存
    private Map<String,Object> singletonBeanCacheMap = new ConcurrentHashMap<String, Object>();

    //通用ioc容器
    private Map<String,GPBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, GPBeanWrapper>();

    public GPApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() throws Exception{
        //1.定位、配置文件
        reader = new GPBeanDefinitionReader(this.configLoactions);

        //2、加在配置文件，扫描所有的类，把它封装成BeanDefinition
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册、把配置信息放在伪IOC容器中
        doBeanDefinitionRegistry(beanDefinitions);

        //4、把不是延迟加载的类，提前初始化
        doAutowired();

    }

    //只处理非延时加载的情况
    private void doAutowired() {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    private void doBeanDefinitionRegistry(List<GPBeanDefinition> beanDefinitions) throws Exception {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            if( super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The"+ beanDefinition.getFactoryBeanName() +"is exists!!");
            }

            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }

    //依赖注入，从这里开始，通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //spring的做法，不会吧最原始的对象放出去，会用一个BeanWrapper来进行包装
    //装饰器模式
    //1、保留原来的OOP 关系
    //2、需要我们对它进行扩展，增强（伪以后的AOP打基础）
    public Object getBean(String beanName) {

        GPBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        try {
            //生成通知事件
            //工厂模式 + 策略模式
            GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
            //1、初始化
            Object instance = instantiateBean(beanDefinition);
            if(null == instance){
                return null;
            }
            //class A { B b;}
            //class B { A a;}
            //在实例初始化以前调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            GPBeanWrapper gpBeanWrapper = new GPBeanWrapper(instance);
            //2、拿到BeanWrapper之后，把BeanWrapper保存在IOC容器中
            this.beanWrapperMap.put(beanName,gpBeanWrapper);

            //在实例初始化之后调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            //2、注入
            populateBean(beanName,instance);
            //通过这样一调用，相当于给我们自己留有了可操作得到空间
            return  this.beanWrapperMap.get(beanName).getWrappedInstance();
        }catch (Exception e){
            return null;
        }
    }

    public Object getBean(Class<?> beanClass){
        return getBean(beanClass.getName());
    }

    private void populateBean(String beanName, Object instance) {
        Class<?> clazz = instance.getClass();
        //不是所有的牛奶都叫特仑苏
        if(!(clazz.isAnnotationPresent(GPController.class) ||
                clazz.isAnnotationPresent(GPService.class))){
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(GPAutowired.class)){
                continue;
            }
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);

            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }

            field.setAccessible(true);
            try {
                //这里 为什么会有 null
                GPBeanWrapper gpBeanWrapper = this.beanWrapperMap.get(autowiredBeanName);
                if(null == gpBeanWrapper){
                    continue;
                }
                field.set(instance,this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //传一个BeanDefinition,就返回一个实例Bean
    private Object instantiateBean(GPBeanDefinition gpBeanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = gpBeanDefinition.getBeanClassName();

        //2、反射实例化、得到一个对象
        Object instance = null;
        try {
            //因为根据Class才能确定一个类是否有实例
            if(this.singletonBeanCacheMap.containsKey(className)){
                instance = this.singletonBeanCacheMap.get(className);
            }else{
                //假设默认就是单例,细节暂时不考虑
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //AOP
                GPAdvisedSupport config = instantionAopConfig(gpBeanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);
                if(config.pointCutMatch()){

                    instance = createProxy(config).getProxy();
                }
//                this.singletonBeanCacheMap.put(className,instance);
                this.singletonBeanCacheMap.put(gpBeanDefinition.getFactoryBeanName(),instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //3、把这个对象封装到BeanWrapper中
        //singletonObjects
        //factoryBeanInstanceCache

        //4、把BeanWrapper存在IOC容器中
        return instance;
    }

    private GPAopProxy createProxy(GPAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new GPJdkDynamicAopProxy(config);
        }
        return new GPCglibAopProxy(config);
    }

    private GPAdvisedSupport instantionAopConfig(GPBeanDefinition beanDefinition) {
        GPAopConfig config = new GPAopConfig();
        config.setPointCut(this.reader.getContextConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getContextConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getContextConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getContextConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getContextConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getContextConfig().getProperty("aspectAfterThrowingName"));
        return new GPAdvisedSupport(config);
    }

    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getContextConfig();
    }
}
