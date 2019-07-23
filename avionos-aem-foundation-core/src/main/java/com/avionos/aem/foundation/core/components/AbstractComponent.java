package com.avionos.aem.foundation.core.components;

import com.avionos.aem.foundation.api.link.Link;
import com.avionos.aem.foundation.api.link.builders.LinkBuilder;
import com.avionos.aem.foundation.api.resource.ComponentResource;
import com.avionos.aem.foundation.api.page.FoundationPage;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * Base class for AEM component classes.
 */
@JsonAutoDetect(fieldVisibility = NONE, getterVisibility = NONE, isGetterVisibility = NONE)
public abstract class AbstractComponent implements ComponentResource {

    @Inject
    private ComponentResource componentResource;

    @Override
    public String getHref() {
        return componentResource.getHref();
    }

    @Override
    public ValueMap asMap() {
        return componentResource.asMap();
    }

    @Override
    public String getHref(final boolean mapped) {
        return componentResource.getHref(mapped);
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName) {
        return componentResource.getAsHrefInherited(propertyName);
    }

    @Override
    public Link getLink() {
        return componentResource.getLink();
    }

    @Override
    public <T> T get(final String propertyName, final T defaultValue) {
        return componentResource.get(propertyName, defaultValue);
    }

    @Override
    public Optional<ComponentResource> getComponentResource(final String relativePath) {
        return componentResource.getComponentResource(relativePath);
    }

    @Override
    public Link getLink(final boolean mapped) {
        return componentResource.getLink(mapped);
    }

    @Override
    public List<ComponentResource> getComponentResources() {
        return componentResource.getComponentResources();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return componentResource.getLinkBuilder();
    }

    @Override
    public <T> Optional<T> get(final String propertyName, final Class<T> type) {
        return componentResource.get(propertyName, type);
    }

    @Override
    public String getId() {
        return componentResource.getId();
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict) {
        return componentResource.getAsHrefInherited(propertyName, strict);
    }

    @Override
    public List<ComponentResource> getComponentResources(final Predicate<ComponentResource> predicate) {
        return componentResource.getComponentResources(predicate);
    }

    @Override
    public boolean isHasImage() {
        return componentResource.isHasImage();
    }

    @Override
    public LinkBuilder getLinkBuilder(final boolean mapped) {
        return componentResource.getLinkBuilder(mapped);
    }

    @Override
    public int getIndex() {
        return componentResource.getIndex();
    }

    @Override
    public boolean isHasImage(final String name) {
        return componentResource.isHasImage(name);
    }

    @Override
    public List<ComponentResource> getComponentResources(final String relativePath) {
        return componentResource.getComponentResources(relativePath);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName) {
        return componentResource.getAsHref(propertyName);
    }

    @Override
    public int getIndex(final String resourceType) {
        return componentResource.getIndex(resourceType);
    }

    @Override
    public List<ComponentResource> getComponentResources(final String relativePath, final String resourceType) {
        return componentResource.getComponentResources(relativePath, resourceType);
    }

    @Override
    public String getPath() {
        return componentResource.getPath();
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict) {
        return componentResource.getAsHref(propertyName, strict);
    }

    @Override
    public Optional<String> getAsHrefInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return componentResource.getAsHrefInherited(propertyName, strict, mapped);
    }

    @Override
    public List<ComponentResource> getComponentResources(final String relativePath,
        final Predicate<ComponentResource> predicate) {
        return componentResource.getComponentResources(relativePath, predicate);
    }

    @Override
    public Resource getResource() {
        return componentResource.getResource();
    }

    @Override
    public Optional<Link> getAsLinkInherited(
        final String propertyName) {
        return componentResource.getAsLinkInherited(propertyName);
    }

    @Override
    public Optional<ComponentResource> getComponentResourceInherited(final String relativePath) {
        return componentResource.getComponentResourceInherited(relativePath);
    }

    @Override
    public Optional<String> getAsHref(final String propertyName, final boolean strict, final boolean mapped) {
        return componentResource.getAsHref(propertyName, strict, mapped);
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict) {
        return componentResource.getAsLinkInherited(propertyName, strict);
    }

    @Override
    public List<ComponentResource> getComponentResourcesInherited(final String relativePath) {
        return componentResource.getComponentResourcesInherited(relativePath);
    }

    @Override
    public Optional<ComponentResource> getParent() {
        return componentResource.getParent();
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName) {
        return componentResource.getAsLink(propertyName);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict) {
        return componentResource.getAsLink(propertyName, strict);
    }

    @Override
    public Optional<Link> getAsLinkInherited(final String propertyName, final boolean strict, final boolean mapped) {
        return componentResource.getAsLinkInherited(propertyName, strict, mapped);
    }

    @Override
    public <T> List<T> getAsListInherited(final String propertyName, final Class<T> type) {
        return componentResource.getAsListInherited(propertyName, type);
    }

    @Override
    public Optional<Link> getAsLink(final String propertyName, final boolean strict, final boolean mapped) {
        return componentResource.getAsLink(propertyName, strict, mapped);
    }

    @Override
    public Optional<FoundationPage> getAsPageInherited(final String propertyName) {
        return componentResource.getAsPageInherited(propertyName);
    }

    @Override
    public <T> List<T> getAsList(final String propertyName, final Class<T> type) {
        return componentResource.getAsList(propertyName, type);
    }

    @Override
    public <AdapterType> Optional<AdapterType> getAsTypeInherited(final String propertyName,
        final Class<AdapterType> type) {
        return componentResource.getAsTypeInherited(propertyName, type);
    }

    @Override
    public Optional<String> getImageReferenceInherited(final boolean isSelf) {
        return componentResource.getImageReferenceInherited(isSelf);
    }

    @Override
    public Optional<FoundationPage> getAsPage(final String propertyName) {
        return componentResource.getAsPage(propertyName);
    }

    @Override
    public Optional<String> getImageReferenceInherited() {
        return componentResource.getImageReferenceInherited();
    }

    @Override
    public Optional<String> getImageReferenceInherited(final String name) {
        return componentResource.getImageReferenceInherited(name);
    }

    @Override
    public <AdapterType> Optional<AdapterType> getAsType(final String propertyName, final Class<AdapterType> type) {
        return componentResource.getAsType(propertyName, type);
    }

    @Override
    public Optional<String> getImageReference(final boolean isSelf) {
        return componentResource.getImageReference(isSelf);
    }

    @Override
    public Optional<String> getImageReference() {
        return componentResource.getImageReference();
    }

    @Override
    public Optional<String> getImageReference(final String name) {
        return componentResource.getImageReference(name);
    }

    @Override
    public Optional<String> getImageRendition(final String renditionName) {
        return componentResource.getImageRendition(renditionName);
    }

    @Override
    public Optional<String> getImageRendition(final String name, final String renditionName) {
        return componentResource.getImageRendition(name, renditionName);
    }

    @Override
    public <T> T getInherited(final String propertyName, final T defaultValue) {
        return componentResource.getInherited(propertyName, defaultValue);
    }

    @Override
    public <T> Optional<T> getInherited(final String propertyName, final Class<T> type) {
        return componentResource.getInherited(propertyName, type);
    }

    @Override
    public Optional<ComponentResource> findAncestor(final Predicate<ComponentResource> predicate) {
        return componentResource.findAncestor(predicate);
    }

    @Override
    public Optional<ComponentResource> findAncestor(final Predicate<ComponentResource> predicate,
        final boolean excludeCurrentResource) {
        return componentResource.findAncestor(predicate, excludeCurrentResource);
    }

    @Override
    public Optional<ComponentResource> findAncestorWithProperty(final String propertyName) {
        return componentResource.findAncestorWithProperty(propertyName);
    }

    @Override
    public Optional<ComponentResource> findAncestorWithProperty(final String propertyName,
        final boolean excludeCurrentResource) {
        return componentResource.findAncestorWithProperty(propertyName, excludeCurrentResource);
    }

    @Override
    public <V> Optional<ComponentResource> findAncestorWithPropertyValue(final String propertyName, final V propertyValue) {
        return componentResource.findAncestorWithPropertyValue(propertyName, propertyValue);
    }

    @Override
    public <V> Optional<ComponentResource> findAncestorWithPropertyValue(final String propertyName, final V propertyValue,
        final boolean excludeCurrentResource) {
        return componentResource.findAncestorWithPropertyValue(propertyName, propertyValue, excludeCurrentResource);
    }

    @Override
    public List<ComponentResource> findDescendants(final Predicate<ComponentResource> predicate) {
        return componentResource.findDescendants(predicate);
    }
}
