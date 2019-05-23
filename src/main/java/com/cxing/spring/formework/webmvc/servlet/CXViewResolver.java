package com.cxing.spring.formework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class CXViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    private File templateRootDir;

    public CXViewResolver(String templateRoot){
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public CXView resolveViewName(String viewName, Locale locale) throws Exception {
        if(viewName == null || "".equals(viewName)){return null;}
        viewName = viewName.endsWith(".html") ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new CXView(templateFile);
    }

}
