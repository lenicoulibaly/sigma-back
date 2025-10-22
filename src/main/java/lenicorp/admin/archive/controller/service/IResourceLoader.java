package lenicorp.admin.archive.controller.service;

import java.io.IOException;
import java.io.InputStream;

public interface IResourceLoader
{
    InputStream getStaticImages(String path) throws IOException;
    InputStream getLocalImages(String path) throws IOException;
}
