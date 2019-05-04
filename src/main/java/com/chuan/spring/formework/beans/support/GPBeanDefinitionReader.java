package com.chuan.spring.formework.beans.support;

import com.chuan.spring.formework.beans.config.GPBeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * author:曲终、人散
 * Date:2019/4/24 20:39
 */
public class GPBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    //保存application.properties配置文件中的内容
    private Properties contextConfig = new Properties();

    //固定配置文件的key,相当于xml
    private final String SCAN_PACKAGE = "scanPackage";

    public GPBeanDefinitionReader(String... locations) {
        //直接从类路径下找到Spring主配置文件所在的路径
        //并且将其读取出来放在Properties对象中
        //相当于 scanPackage=com.myspring.demo 从文件中保存在内存中
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));

        try {
            contextConfig.load(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(contextConfig.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //scanPackage=com.myspring.demo ,存储的是包路径
        //转换为文件路径，实际上就是把 . 替换为 / 就行了
        //classpath
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));

        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }


    public Properties getContextConfig() {
        return this.contextConfig;
    }

    //把配置文件中扫描到的所有配置信息转换为GPBeanDefinition对象，以便于以后的ioc操作方便
    public List<GPBeanDefinition> loadBeanDefinitions() {
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        try{
            for (String className : registyBeanClasses) {
                Class<?> beanClass = Class.forName(className);

                //如果是一个接口，是不能实例化的
                //用它实现类来实例化
                if(beanClass.isInterface()){
                    continue;
                }
                //beanName 有三种情况
                //1、默认是类名首字母小写
                //2、自定义名字
                //3、接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候可以自定名字
                    result.add(doCreateBeanDefinition(anInterface.getName(), beanClass.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //把每一个配置信息解析成一个BeanDefinition
    public GPBeanDefinition doCreateBeanDefinition(String factorybeanName,String beanClassName) {
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factorybeanName);
        return beanDefinition;
    }

    //如果类名本身是小写字母，确实会出问题
    //但是我要说明的是：这个方法是我自己用，private的
    //传值也是自己传，类也都遵循了驼峰命名法
    //默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
