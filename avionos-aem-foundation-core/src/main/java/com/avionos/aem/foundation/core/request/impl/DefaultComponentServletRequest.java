package com.avionos.aem.foundation.core.request.impl;

import com.avionos.aem.foundation.api.page.FoundationPage;
import com.avionos.aem.foundation.api.page.FoundationPageManager;
import com.avionos.aem.foundation.api.request.ComponentServletRequest;
import com.avionos.aem.foundation.api.resource.ComponentResource;
import com.day.cq.wcm.api.WCMMode;
import com.google.common.collect.ImmutableList;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultComponentServletRequest implements ComponentServletRequest {

    private static final Function<RequestParameter, String> REQUEST_PARAMETER_TO_STRING = RequestParameter :: getString;

    private static final Function<RequestParameter[], List<String>> REQUEST_PARAMETERS_TO_LIST = parameters -> Arrays
        .stream(parameters)
        .map(RequestParameter :: getString)
        .collect(Collectors.toList());

    private final SlingHttpServletRequest request;

    private final SlingHttpServletResponse response;

    public DefaultComponentServletRequest(final SlingHttpServletRequest request,
        final SlingHttpServletResponse response) {
        this.request = checkNotNull(request);
        this.response = checkNotNull(response);
    }

    @Override
    public ComponentResource getComponentResource() {
        return request.getResource().adaptTo(ComponentResource.class);
    }

    @Override
    public FoundationPage getCurrentPage() {
        return getPageManager().getContainingPage(request.getResource());
    }

    @Override
    public FoundationPageManager getPageManager() {
        return request.getResourceResolver().adaptTo(FoundationPageManager.class);
    }

    @Override
    public ValueMap getProperties() {
        return request.getResource().getValueMap();
    }

    @Override
    public Optional<String> getRequestParameter(final String parameterName) {
        checkNotNull(parameterName);

        return Optional.ofNullable(request.getRequestParameter(parameterName))
            .map(REQUEST_PARAMETER_TO_STRING);
    }

    @Override
    public Optional<List<String>> getRequestParameters(final String parameterName) {
        checkNotNull(parameterName);

        return Optional.ofNullable(request.getRequestParameters(parameterName))
            .map(REQUEST_PARAMETERS_TO_LIST);
    }

    @Override
    public String getRequestParameter(final String parameterName, final String defaultValue) {
        checkNotNull(parameterName);
        checkNotNull(defaultValue);

        final RequestParameter parameter = request.getRequestParameter(parameterName);

        return parameter == null ? defaultValue : parameter.getString();
    }

    @Override
    public Resource getResource() {
        return request.getResource();
    }

    @Override
    public ResourceResolver getResourceResolver() {
        return request.getResourceResolver();
    }

    @Override
    public List<String> getSelectors() {
        return ImmutableList.copyOf(request.getRequestPathInfo().getSelectors());
    }

    @Override
    public SlingHttpServletRequest getSlingRequest() {
        return request;
    }

    @Override
    public SlingHttpServletResponse getSlingResponse() {
        return response;
    }

    @Override
    public WCMMode getWCMMode() {
        return WCMMode.fromRequest(request);
    }
}
