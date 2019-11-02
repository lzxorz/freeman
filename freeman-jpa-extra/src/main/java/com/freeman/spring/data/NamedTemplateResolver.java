package com.freeman.spring.data;

import org.springframework.core.io.Resource;

import java.util.Iterator;

public interface NamedTemplateResolver {
    Iterator<Void> doInTemplateResource(Resource resource, final NamedTemplateCallback callback) throws Exception;
}
