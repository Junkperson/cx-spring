package com.cxing.spring.formework.webmvc.servlet;

import com.cxing.spring.formework.annotation.CXController;
import com.cxing.spring.formework.annotation.CXRequestMapping;
import com.cxing.spring.formework.context.CXApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CXDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private CXApplicationContext context;

    private List<CXHandlerMapping> handlerMappings = new ArrayList();

    private Map<CXHandlerMapping,CXHandlerAdapter> handlerAdapters = new HashMap();

    private List<CXViewResolver> viewResolvers = new ArrayList();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req,resp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp)throws Exception {
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        CXHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req,resp,new CXModelAndView("404"));
            return;
        }
        //2、准备调用前的参数
        CXHandlerAdapter ha = getHandlerAdapter(handler);

        //3、真正的调用方法,返回ModelAndView存储了要穿页面上值，和页面模板的名称
        CXModelAndView mv = ha.handle(req,resp,handler);

        //这一步才是真正的输出
        processDispatchResult(req, resp, mv);


    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, CXModelAndView mv) throws Exception {
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        if(mv ==null){return;}

        //如果ModelAndView不为null，怎么办？
        if(this.viewResolvers.isEmpty()){return; }

        for (CXViewResolver viewResolver : this.viewResolvers) {
            CXView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }

    }

    private CXHandlerAdapter getHandlerAdapter(CXHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        CXHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }

    private CXHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (CXHandlerMapping handlerMapping : this.handlerMappings){
            try {
                Matcher matcher = handlerMapping.getPattern().matcher(url);
                if(!matcher.matches()){continue;}
                return  handlerMapping;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        context = new CXApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));
        //2、初始化Spring MVC 九大组件
        initStrategies(context);
    }

    private void initStrategies(CXApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);


        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);


        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);

    }

    private void initFlashMapManager(CXApplicationContext context) {
    }

    private void initViewResolvers(CXApplicationContext context) {
        //拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i=0; i<templates.length; i++){
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new CXViewResolver(templateRoot));

        }

    }

    private void initRequestToViewNameTranslator(CXApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(CXApplicationContext context) {
    }

    private void initHandlerAdapters(CXApplicationContext context) {
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (CXHandlerMapping handlerMapping: this.handlerMappings){
            handlerAdapters.put(handlerMapping,new CXHandlerAdapter());
        }
    }

    private void initHandlerMappings(CXApplicationContext context) {
        String [] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName: beanNames){
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(CXController.class)){continue;}

                String baseUrl = "";
                //获取Controller的url配置
                if(clazz.isAnnotationPresent(CXRequestMapping.class)){
                    CXRequestMapping requestMapping = clazz.getAnnotation(CXRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                Method[] methods = clazz.getMethods();
                for (Method method: methods) {
                    if(!method.isAnnotationPresent(CXRequestMapping.class)){continue;}

                    CXRequestMapping requestMapping = method.getAnnotation(CXRequestMapping.class);
                    String regex = ("/" + baseUrl + "/"+ requestMapping.value().replaceAll("\\*","*")).replaceAll("/+","/");

                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new CXHandlerMapping(pattern,controller,method));
                    System.out.println("Mapped " + regex + "," + method);
                    // log.info("Mapped " + regex + "," + method);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initThemeResolver(CXApplicationContext context) {
    }

    private void initLocaleResolver(CXApplicationContext context) {

    }

    private void initMultipartResolver(CXApplicationContext context) {
    }
}
