package com.avionos.aem.foundation.core.resource.impl

import com.avionos.aem.foundation.api.link.Link
import com.avionos.aem.foundation.api.link.builders.LinkBuilder
import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.link.builders.factory.LinkBuilderFactory
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyExistsPredicate
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyValuePredicate
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourceTypePredicate
import com.avionos.aem.foundation.core.resource.predicates.ResourcePathPredicate
import com.avionos.aem.foundation.core.resource.predicates.ResourceTypePredicate
import com.avionos.aem.foundation.core.utils.PathUtils
import com.day.cq.commons.DownloadResource
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap
import com.day.cq.commons.inherit.InheritanceValueMap
import com.day.cq.commons.jcr.JcrConstants
import com.day.cq.dam.api.Asset
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.foundation.Image
import com.google.common.base.Objects
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ValueMap

import java.lang.reflect.Array
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.IntStream

import static ComponentResourceFunctions.RESOURCE_TO_COMPONENT_NODE
import static com.avionos.aem.foundation.core.constants.ComponentConstants.DEFAULT_IMAGE_NAME
import static com.avionos.aem.foundation.core.link.impl.LinkFunctions.LINK_TO_HREF
import static com.google.common.base.Preconditions.checkNotNull

final class DefaultComponentResource implements ComponentResource {

    private final Resource resource

    private final InheritanceValueMap properties

    DefaultComponentResource(Resource resource) {
        this.resource = resource

        properties = new HierarchyNodeInheritanceValueMap(resource)
    }

    @Override
    boolean equals(Object other) {
        new EqualsBuilder().append(path, (other as ComponentResource).path).equals
    }

    @Override
    int hashCode() {
        new HashCodeBuilder().append(path).hashCode()
    }

    @Override
    ValueMap asMap() {
        properties
    }

    @Override
    <T> T get(String propertyName, T defaultValue) {
        properties.get(checkNotNull(propertyName), defaultValue)
    }

    @Override
    <T> Optional<T> get(String propertyName, Class<T> type) {
        Optional.ofNullable(properties.get(propertyName, type))
    }

    @Override
    <AdapterType> Optional<AdapterType> getAsType(String propertyName, Class<AdapterType> type) {
        getAsTypeOptional(properties.get(checkNotNull(propertyName), ""), type)
    }

    @Override
    Optional<String> getAsHref(String propertyName) {
        getAsHref(propertyName, false)
    }

    @Override
    Optional<String> getAsHref(String propertyName, boolean strict) {
        getAsHref(propertyName, strict, false)
    }

    @Override
    Optional<String> getAsHref(String propertyName, boolean strict, boolean mapped) {
        getAsLink(propertyName, strict, mapped).map(LINK_TO_HREF)
    }

    @Override
    Optional<Link> getAsLink(String propertyName) {
        getAsLink(propertyName, false)
    }

    @Override
    Optional<Link> getAsLink(String propertyName, boolean strict) {
        getAsLink(propertyName, strict, false)
    }

    @Override
    Optional<Link> getAsLink(String propertyName, boolean strict, boolean mapped) {
        getLinkOptional(get(propertyName, String), strict, mapped)
    }

    @Override
    <T> List<T> getAsList(String propertyName, Class<T> type) {
        properties.get(checkNotNull(propertyName), Array.newInstance(type, 0)) as List
    }

    @Override
    Optional<FoundationPage> getAsPage(String propertyName) {
        getPageOptional(properties.get(checkNotNull(propertyName), ""))
    }

    @Override
    String getHref() {
        getHref(false)
    }

    @Override
    String getHref(boolean mapped) {
        getLink(mapped).href
    }

    @Override
    String getId() {
        def path

        if (resource.name == JcrConstants.JCR_CONTENT) {
            path = resource.parent.path // use page path for jcr:content nodes
        } else if (resource.resourceType == NameConstants.NT_PAGE) {
            path = resource.path
        } else {
            def currentPage = resource.resourceResolver.adaptTo(PageManager).getContainingPage(resource)

            if (currentPage) {
                // remove page content path since resource path relative to jcr:content will always be unique
                path = StringUtils.removeStart(getPath(), currentPage.contentResource.path)
            } else {
                path = resource.path // non-content path
            }
        }

        path.substring(1).replaceAll("/", "-")
    }

    @Override
    Optional<String> getImageReference(boolean isSelf) {
        isSelf ? Optional.ofNullable(properties.get(DownloadResource.PN_REFERENCE, String)) : imageReference
    }

    @Override
    Optional<String> getImageReference() {
        getImageReference(DEFAULT_IMAGE_NAME)
    }

    @Override
    Optional<String> getImageReference(String name) {
        Optional.ofNullable(properties.get(checkNotNull(name) + "/" + DownloadResource.PN_REFERENCE, String))
    }

    @Override
    Optional<String> getImageRendition(String renditionName) {
        getImageRendition(DEFAULT_IMAGE_NAME, checkNotNull(renditionName))
    }

    @Override
    Optional<String> getImageRendition(String name, String renditionName) {
        checkNotNull(name)
        checkNotNull(renditionName)

        def imageReferenceOptional = getImageReference(name)
        def imageRenditionOptional = Optional.empty()

        if (imageReferenceOptional.present) {
            def asset = resource.resourceResolver.getResource(imageReferenceOptional.get()).adaptTo(Asset)

            if (asset) {
                def rendition = asset.renditions.find { it.name == renditionName }

                imageRenditionOptional = Optional.ofNullable(rendition?.path)
            }
        }

        imageRenditionOptional
    }

    @Override
    int getIndex() {
        getIndexForPredicate({ true })
    }

    @Override
    int getIndex(String resourceType) {
        getIndexForPredicate(new ResourceTypePredicate(resourceType))
    }

    @Override
    Link getLink() {
        getLink(false)
    }

    @Override
    Link getLink(boolean mapped) {
        getLinkBuilder(mapped).build()
    }

    @Override
    LinkBuilder getLinkBuilder() {
        getLinkBuilder(false)
    }

    @Override
    LinkBuilder getLinkBuilder(boolean mapped) {
        LinkBuilderFactory.forResource(resource, mapped)
    }

    @Override
    String getPath() {
        resource.path
    }

    @Override
    Resource getResource() {
        resource
    }

    @Override
    boolean isHasImage() {
        isHasImage(null) ?: isHasImage(DEFAULT_IMAGE_NAME)
    }

    @Override
    boolean isHasImage(String name) {
        if (name) {
            def child = resource.getChild(name)

            child && new Image(resource, name).hasContent()
        } else {
            new Image(resource).hasContent()
        }
    }

    @Override
    Optional<ComponentResource> findAncestor(Predicate<ComponentResource> predicate) {
        findAncestorForPredicate(predicate, false)
    }

    @Override
    Optional<ComponentResource> findAncestor(Predicate<ComponentResource> predicate, boolean excludeCurrentResource) {
        findAncestorForPredicate(predicate, excludeCurrentResource)
    }

    @Override
    List<ComponentResource> findDescendants(Predicate<ComponentResource> predicate) {
        def descendantNodes = []

        componentResources.each { componentResource ->
            if (predicate.test(componentResource)) {
                descendantNodes.add(componentResource)
            }

            descendantNodes.addAll(componentResource.findDescendants(predicate))
        }

        descendantNodes
    }

    @Override
    Optional<ComponentResource> findAncestorWithProperty(String propertyName) {
        findAncestorForPredicate(new ComponentResourcePropertyExistsPredicate(propertyName), false)
    }

    @Override
    Optional<ComponentResource> findAncestorWithProperty(String propertyName, boolean excludeCurrentResource) {
        findAncestorForPredicate(new ComponentResourcePropertyExistsPredicate(propertyName), excludeCurrentResource)
    }

    @Override
    <V> Optional<ComponentResource> findAncestorWithPropertyValue(String propertyName, V propertyValue) {
        findAncestorForPredicate(new ComponentResourcePropertyValuePredicate(propertyName, propertyValue), false)
    }

    @Override
    <V> Optional<ComponentResource> findAncestorWithPropertyValue(String propertyName, V propertyValue,
        boolean excludeCurrentResource) {
        findAncestorForPredicate(new ComponentResourcePropertyValuePredicate(propertyName, propertyValue),
            excludeCurrentResource)
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName) {
        getAsHrefInherited(propertyName, false)
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict) {
        getAsHrefInherited(propertyName, strict, false)
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict, boolean mapped) {
        getAsLinkInherited(propertyName, strict, mapped).map(LINK_TO_HREF)
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName) {
        getAsLinkInherited(propertyName, false)
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict) {
        getAsLinkInherited(propertyName, strict, false)
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict, boolean mapped) {
        getLinkOptional(getInherited(propertyName, String.class), strict, mapped)
    }

    @Override
    <T> List<T> getAsListInherited(String propertyName, Class<T> type) {
        properties.getInherited(checkNotNull(propertyName), Array.newInstance(type, 0)) as List
    }

    @Override
    Optional<FoundationPage> getAsPageInherited(String propertyName) {
        getPageOptional(properties.getInherited(checkNotNull(propertyName), ""))
    }

    @Override
    <AdapterType> Optional<AdapterType> getAsTypeInherited(String propertyName, Class<AdapterType> type) {
        getAsTypeOptional(properties.getInherited(checkNotNull(propertyName), ""), type)
    }

    @Override
    Optional<ComponentResource> getComponentResource(String relativePath) {
        Optional.ofNullable(resource.getChild(checkNotNull(relativePath))).map(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    List<ComponentResource> getComponentResources() {
        Lists.newArrayList(resource.children)
            .stream()
            .map(RESOURCE_TO_COMPONENT_NODE)
            .collect(Collectors.toList())
    }

    @Override
    List<ComponentResource> getComponentResources(Predicate<ComponentResource> predicate) {
        Lists.newArrayList(resource.children)
            .stream()
            .map(RESOURCE_TO_COMPONENT_NODE)
            .filter(checkNotNull(predicate))
            .collect(Collectors.toList())
    }

    @Override
    List<ComponentResource> getComponentResources(String relativePath) {
        Optional.of(resource.getChild(checkNotNull(relativePath)))
            .map { childResource ->
                Lists.newArrayList(childResource.children)
                    .stream()
                    .map(RESOURCE_TO_COMPONENT_NODE)
                    .collect(Collectors.toList())
            }
            .orElse([])
    }

    @Override
    List<ComponentResource> getComponentResources(String relativePath, String resourceType) {
        getComponentResources(relativePath, new ComponentResourceTypePredicate(resourceType))
    }

    @Override
    List<ComponentResource> getComponentResources(String relativePath, Predicate<ComponentResource> predicate) {
        getComponentResources(checkNotNull(relativePath))
            .stream()
            .filter(checkNotNull(predicate))
            .collect(Collectors.toList())
    }

    @Override
    Optional<String> getImageReferenceInherited() {
        getImageReferenceInherited(DEFAULT_IMAGE_NAME)
    }

    @Override
    Optional<String> getImageReferenceInherited(boolean isSelf) {
        isSelf ? Optional.ofNullable(
            properties.getInherited(DownloadResource.PN_REFERENCE, String)) : imageReferenceInherited
    }

    @Override
    Optional<String> getImageReferenceInherited(String name) {
        def propertyName = new StringBuilder(name)
            .append("/")
            .append(DownloadResource.PN_REFERENCE)
            .toString()

        Optional.ofNullable(properties.getInherited(propertyName, String))
    }

    @Override
    <T> T getInherited(String propertyName, T defaultValue) {
        properties.getInherited(propertyName, defaultValue)
    }

    @Override
    <T> Optional<T> getInherited(String propertyName, Class<T> type) {
        Optional.ofNullable(properties.getInherited(propertyName, type))
    }

    @Override
    Optional<ComponentResource> getComponentResourceInherited(String relativePath) {
        findChildResourceInherited(relativePath).map(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    List<ComponentResource> getComponentResourcesInherited(String relativePath) {
        findChildResourceInherited(relativePath)
            .map { child ->
                Lists.newArrayList(child.children)
                    .stream()
                    .map(RESOURCE_TO_COMPONENT_NODE)
                    .collect(Collectors.toList())
            }
            .orElse([])
    }

    @Override
    Optional<ComponentResource> getParent() {
        Optional.ofNullable(resource.parent).map(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    String toString() {
        Objects.toStringHelper(this)
            .add("path", getPath())
            .add("properties", Maps.newHashMap(asMap()))
            .toString()
    }

    // internals

    private <AdapterType> Optional<AdapterType> getAsTypeOptional(String path, Class<AdapterType> type) {
        def resource = path ? resource.resourceResolver.getResource(path) : null
        def result = (type == Resource ? resource : resource?.adaptTo(type)) as AdapterType

        Optional.ofNullable(result)
    }

    private Optional<Link> getLinkOptional(Optional<String> pathOptional, boolean strict, boolean mapped) {
        return pathOptional.map(new Function<String, Link>() {
            @Override
            Link apply(String path) {
                def resourceResolver = resource.resourceResolver
                def resource = resourceResolver.getResource(path)

                def builder

                if (resource) {
                    // internal link
                    builder = LinkBuilderFactory.forResource(resource, mapped)
                } else {
                    // external link
                    def mappedPath = mapped ? resourceResolver.map(path) : path

                    builder = LinkBuilderFactory.forPath(mappedPath)

                    if (strict) {
                        builder.external = PathUtils.isExternal(mappedPath, resourceResolver)
                    }
                }

                builder.build()
            }
        })
    }

    private int getIndexForPredicate(Predicate<Resource> resourceTypePredicate) {
        def resources = Lists.newArrayList(resource.parent.children)
            .stream()
            .filter(resourceTypePredicate)
            .collect(Collectors.toList())

        def resourcePathPredicate = new ResourcePathPredicate(path)

        IntStream.range(0, resources.size())
            .filter { i -> resourcePathPredicate.test(resources.get(i)) }
            .findFirst()
            .orElse(-1)
    }

    private Optional<FoundationPage> getPageOptional(String path) {
        Optional<FoundationPage> pageOptional

        if (path) {
            def page = resource.resourceResolver.adaptTo(FoundationPageManager).getPage(path)

            pageOptional = Optional.ofNullable(page)
        } else {
            pageOptional = Optional.empty()
        }

        pageOptional
    }

    private Optional<ComponentResource> findAncestorForPredicate(Predicate<ComponentResource> predicate,
        boolean excludeCurrentResource) {
        def containingPage = resource.resourceResolver.adaptTo(FoundationPageManager).getContainingPage(resource)

        def relativePath = resource.name == JcrConstants.JCR_CONTENT ? "" : resource.path.substring(
            containingPage.contentResource.path.length() + 1)

        def componentResourceFunction = new Function<FoundationPage, Optional<ComponentResource>>() {
            @Override
            Optional<ComponentResource> apply(FoundationPage page) {
                relativePath.empty ? page.componentResource : page.getComponentResource(relativePath)
            }
        }

        def pagePredicate = new Predicate<FoundationPage>() {
            @Override
            boolean test(FoundationPage page) {
                def componentResourceOptional = componentResourceFunction.apply(page)

                componentResourceOptional.present && predicate.test(componentResourceOptional.get())
            }
        }

        containingPage.findAncestor(pagePredicate, excludeCurrentResource)
            .map(new Function<FoundationPage, ComponentResource>() {
                @Override
                ComponentResource apply(FoundationPage page) {
                    componentResourceFunction.apply(page).get()
                }
            })
    }

    private Optional<Resource> findChildResourceInherited(String relativePath) {
        def containingPage = resource.resourceResolver.adaptTo(FoundationPageManager).getContainingPage(resource)

        def builder = new StringBuilder()

        if (resource.name == JcrConstants.JCR_CONTENT) {
            builder.append(relativePath)
        } else {
            builder.append(resource.path.substring(containingPage.contentResource.path.length() + 1))
            builder.append('/')
            builder.append(relativePath)
        }

        // path relative to jcr:content
        def resourcePath = builder.toString()

        def predicate = new Predicate<FoundationPage>() {
            @Override
            boolean test(FoundationPage page) {
                page.getContentResource(resourcePath)
            }
        }

        containingPage.findAncestor(predicate).map(new Function<FoundationPage, Resource>() {
            @Override
            Resource apply(FoundationPage page) {
                page.getContentResource(resourcePath)
            }
        })
    }
}
