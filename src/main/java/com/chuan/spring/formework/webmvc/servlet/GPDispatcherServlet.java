package com.chuan.spring.formework.webmvc.servlet;

import com.chuan.spring.formework.annotation.GPController;
import com.chuan.spring.formework.annotation.GPRequestMapping;
import com.chuan.spring.formework.context.GPApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet 只是作为MVC启动的入口
 * author:曲终、人散
 * Date:2019/5/3 19:52
 */
@Slf4j
public class GPDispatcherServlet extends HttpServlet {

    //SpringMVC的web XML文件
    private final String LOCATION = "contextConfigLocation";

    //GPHandlerMapping 是Spring最核心的设计，也是最经典的
    //它厉害的地方就是直接干掉了Strtus,Webwork等MVC框架
    private List<GPHandlerMapping> handlerMappings = new ArrayList<GPHandlerMapping>();

    private Map<GPHandlerMapping, GPHandlerAdapter> handlerAdapters = new HashMap<GPHandlerMapping, GPHandlerAdapter>();

    private List<GPViewResolver> viewResolvers = new ArrayList<GPViewResolver>();

    private GPApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
//            new GPModelAndView("500");
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //根据用户请求的 URL来获得一个Handler
        GPHandlerMapping handler = getHandler(req);
        if(null == handler){
            processDispatchResult(req,resp,new GPModelAndView("404"));
            return;
        }
        GPHandlerAdapter ha = getHandlerAdapter(handler);

        //这一步只是调用方法，得到返回值
        GPModelAndView mv = ha.handle(req, resp, handler);

        //这一步才是整的输出
        processDispatchResult(req,resp,mv);

    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){
            return null;
        }
        GPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GPModelAndView mv) throws Exception{
        //把给我的ModleAndView变成一个HTML、OuputStream、json、freemark、veolcity
        //ContextType
        if(null == mv){return;}

        //如果ModelAndView不为null，怎么办？
        if(this.viewResolvers.isEmpty()){return;}

        for (GPViewResolver viewResolver : this.viewResolvers) {
            GPView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }
    }

    private GPHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();

        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (GPHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        //相当于把IOC容器初始化
        context = new GPApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    private void initStrategies(GPApplicationContext context) {
        //有九种策略
        //正对每个用户的请求，都会经过一定处理的策略之后，最终才能有输出
        //每种策略可以自定义干预，但最终的结果是一致

        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板、主题处理器
        initThemeResolver(context);

        /**这个我们自己实现*/
        //GPHandlerMapping 用来保存Controller 中配置的RequestMapping 和 Method 的一个对应关系
        initHandlerMappings(context);
        /**这个我们自己实现*/
        //handlerAdapters 用来动态匹配Method参数，包括类转换、动态赋值
        //通过 HandlerAdapter 进行多类型的参数动态匹配
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);

        /**这个我们自己实现*/
        //通过GPViewResolver 实现动态模板解析
        //自己解析一套模板语言
        //通过 ViewResolver 解析逻辑视图到具体视图实现
        initViewResolvers(context);
        //Flash映射管理器
        initFlashMapManager(context);
    }

    private void initFlashMapManager(GPApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    private void initThemeResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context) {
    }

    private void initMultipartResolver(GPApplicationContext context) {
    }

    //将Controller 中配置的RequestMapping和Method 进行一一对应
    private void initHandlerMappings(GPApplicationContext context) {
        //按照我们通常的理解应该是一个Map
        //Map<String,Method> map
        //map.put(url,Method)

        //首先从容器中取到所有的实例
        String[] beanNames = context.getBeanDefinitionNames();

        try {
            for (String beanName : beanNames) {
                //到了MVC层，对外提供的方法只有一个getBean 方法
                //返回的对象不是BeanWrapper,怎么办呢？
                Object controller = context.getBean(beanName);

                Class<?> clazz = controller.getClass();

                if (!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //扫描所有的public方法
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);

                    String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*"))
                            .replaceAll("/+", "/");

                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new GPHandlerMapping(pattern, controller, method));

                    log.info("Mapping:" + regex + " , " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initHandlerAdapters(GPApplicationContext context) {
        //在初始化阶段，我们能最的就是，将这些参数的名字或者类型按一定顺序保存下来
        //因为后面反射调用的时候，传的是一个形参数组
        //可以通过记录这些参数的位置index,挨个从数组中填值，这样的话，就和参数顺序无关了
        //把一个requet请求变成一个handler，参数都是字符串的，自动配到handler中的形参

        //可想而知，他要拿到HandlerMapping才能干活
        //就意味着，有几个HandlerMapping就有几个HandlerAdapter
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            //每个方法有一个参数列表，那么这里保存的就是形参列表
            this.handlerAdapters.put(handlerMapping, new GPHandlerAdapter());
        }
    }


    private void initViewResolvers(GPApplicationContext context) {
        //在页面敲一个http://localhost/first.html
        //解决页面名字和模板文件相关联得我问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            //这里主要是为了兼容多模板，所有模仿Spring用List保存
            //在我写的代码中简化了，其实只有需要一个模板就可以搞定
            //只是为了仿真，所有还是搞了个List
            this.viewResolvers.add(new GPViewResolver(templateRoot));
        }
    }
}
