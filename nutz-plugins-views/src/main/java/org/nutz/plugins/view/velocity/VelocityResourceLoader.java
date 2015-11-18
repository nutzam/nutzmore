package org.nutz.plugins.view.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.nutz.mvc.Mvcs;

/**
 * @author wendal
 */
public class VelocityResourceLoader extends ClasspathResourceLoader {

    protected String path;
    
    protected ServletContext sc;

    public void init(ExtendedProperties configuration) {
        path = configuration.getString("path", "/WEB-INF/template/");
        if (!path.endsWith("/"))
            path += "/";
    }
    
    protected URL rs(String source) {
        try {
            URL url = sc().getResource(path + source);
            if (url == null)
                throw new ResourceNotFoundException(source) ;
            return url;
        }
        catch (MalformedURLException e) {
            throw new ResourceNotFoundException(source, e);
        }
    }

    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        try {
            return rs(source).openStream();
        }
        catch (IOException e) {
            throw new ResourceNotFoundException(source, e);
        }
    }

    public boolean isSourceModified(Resource resource) {
        return false;
    }

    public long getLastModified(Resource resource) {
        return 0;
    }
    
    public ServletContext sc() {
        if (sc == null)
            sc = Mvcs.getServletContext();
        return sc;
    }
}