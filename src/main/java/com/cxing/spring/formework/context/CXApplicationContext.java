package com.cxing.spring.formework.context;

import com.cxing.spring.formework.beans.config.CXBeanDefinition;
import com.cxing.spring.formework.beans.support.CXBeanDefinitionReader;
import com.cxing.spring.formework.beans.support.CXDefaultListableBeanFactory;
import com.cxing.spring.formework.annotation.CXAutowired;
import com.cxing.spring.formework.annotation.CXController;
import com.cxing.spring.formework.annotation.CXService;
import com.cxing.spring.formework.aop.CXAopProxy;
import com.cxing.spring.formework.aop.CXCglibAopProxy;
import com.cxing.spring.formework.aop.CXJdkDynamicAopProxy;
import com.cxing.spring.formework.aop.config.CXAopConfig;
import com.cxing.spring.formework.aop.support.CXAdvisedSupport;
import com.cxing.spring.formework.beans.CXBeanWrapper;
import com.cxing.spring.formework.beans.config.CXBeanPostProcessor;
import com.cxing.spring.formework.core.CXBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * IOC核心容器
 */
public class CXApplicationContext extends CXDefaultListableBeanFactory implements CXBeanFactory {

    private String[] configLoactions;
    private CXBeanDefinitionReader reader;


    //单例的IOC容器缓存
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap();
    //通用的IOC容器
    private Map<String,CXBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap();

    public CXApplicationContext(String... configLoactions){
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void refresh() throws Exception {
        //1.定位，定位配置文件
        reader = new CXBeanDefinitionReader(this.configLoactions);

        //2.加载，扫米哦啊相关的类，把它们封装成BeanDefinition
        List<CXBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3.注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类有提前初始化
        doAutowrited();
    }

    private void doAutowrited() {
        for (Map.Entry<String,CXBeanDefinition> beanDefinitionEntry: super.beanDefinitionMap.entrySet()) {
            String beanName =  beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<CXBeanDefinition> beanDefinitions) throws Exception{
        for (CXBeanDefinition beanDefinition: beanDefinitions) {
//            if(super.beanDefinitionMap.containsKey(beanDefinition.getBeanClassName())){
//                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
//            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            //到这里为止，容器初始化完毕
        }
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) throws Exception  {
        CXBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        Object instance = null;

        //这个逻辑还不严谨，自己可以去参考Spring源码
        //工厂模式 + 策略模式
        CXBeanPostProcessor postProcessor = new CXBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance,beanName);

        //1初始化
        instance = instantiateBean(beanName, beanDefinition);

        //3、把这个对象封装到BeanWrapper中
        CXBeanWrapper beanWrapper = new CXBeanWrapper(instance);

        //先有鸡还是先有蛋的问题，一个方法是搞不定的，要分两次
        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        //3注入
        populateBean(beanName,new CXBeanDefinition(),beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    private void populateBean(String beanName, CXBeanDefinition beanDefinition, CXBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();

        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if(!(clazz.isAnnotationPresent(CXController.class) || clazz.isAnnotationPresent(CXService.class))){
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field f: fields) {
            if(!f.isAnnotationPresent(CXAutowired.class)){ continue;}

            CXAutowired autowired = f.getAnnotation(CXAutowired.class);
            String autowiredBeanName =  autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = f.getType().getName();
            }
            //强制访问
            f.setAccessible(true);

            try {
                //为什么会为NULL，先留个坑
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
                f.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }


        }


    }

    private Object instantiateBean(String beanName, CXBeanDefinition beanDefinition) {
        //1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();
        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
            //BeanDefinition.getFactoryBeanName()
            //假设默认就是单例,细节暂且不考虑，先把主线拉通
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                CXAdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);

                //符合PointCut的规则的话，闯将代理对象
                if(config.pointCutMatch()) {
                    instance = createProxy(config).getProxy();
                }

                this.factoryBeanObjectCache.put(className,instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(),instance);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;

    }

    private CXAopProxy createProxy(CXAdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if(targetClass.getInterfaces().length > 0){
            return new CXJdkDynamicAopProxy(config);
        }
        return new CXCglibAopProxy(config);
    }

    private CXAdvisedSupport instantionAopConfig(CXBeanDefinition beanDefinition) {
        CXAopConfig config = new CXAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAround(this.reader.getConfig().getProperty("aspectAround"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new CXAdvisedSupport(config);
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new  String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

}

