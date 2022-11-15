package com.instantsystem.demo.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ResourceConverter {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceConverter.class.getName());

    public static String convertResourceToString(Resource resource) {
        try(Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            LOG.error("Failed to convert resource to String", e);
            throw new RuntimeException(e);
        }
    }
}
