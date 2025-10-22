package lenicorp.admin.archive.controller.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class ResourceLoaderService implements IResourceLoader
{
    @Override
    public InputStream getStaticImages(String path) throws IOException
    {
        URL resourceUrl = getClass().getClassLoader().getResource(path);
        if (resourceUrl == null) {
            throw new IOException("Resource not found: " + path);
        }
        return resourceUrl.openStream();
    }

    @Override
    public InputStream getLocalImages(String path) throws IOException
    {
        return new FileInputStream(new File(path));
    }
}
