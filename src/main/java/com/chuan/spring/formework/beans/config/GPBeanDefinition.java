package com.chuan.spring.formework.beans.config;

import lombok.Data;

/**
 * author:曲终、人散
 * Date:2019/4/24 20:13
 */
@Data
public class GPBeanDefinition {

    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
    private boolean isSingletion = true;


}
