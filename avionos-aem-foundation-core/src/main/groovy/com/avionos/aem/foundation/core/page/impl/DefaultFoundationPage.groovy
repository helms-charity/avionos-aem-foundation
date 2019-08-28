package com.avionos.aem.foundation.core.page.impl

import com.avionos.aem.foundation.api.link.ImageLink
import com.avionos.aem.foundation.api.link.Link
import com.avionos.aem.foundation.api.link.NavigationLink
import com.avionos.aem.foundation.api.link.builders.LinkBuilder
import com.avionos.aem.foundation.api.page.FoundationPage
import com.avionos.aem.foundation.api.page.FoundationPageManager
import com.avionos.aem.foundation.api.page.enums.TitleType
import com.avionos.aem.foundation.api.resource.ComponentResource
import com.avionos.aem.foundation.core.link.builders.factory.LinkBuilderFactory
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyExistsPredicate
import com.avionos.aem.foundation.core.resource.predicates.ComponentResourcePropertyValuePredicate
import com.day.cq.commons.Filter
import com.day.cq.tagging.Tag
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.Page
import com.day.cq.wcm.commons.DeepResourceIterator
import com.google.common.base.Objects
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ValueMap

import java.util.function.Predicate

import static com.avionos.aem.foundation.core.resource.impl.ComponentResourceFunctions.RESOURCE_TO_COMPONENT_NODE
import static com.google.common.base.Preconditions.checkNotNull

final class DefaultFoundationPage implements FoundationPage {

    private static final Filter<Page> ALL_PAGES = new Filter<Page>() {
        @Override
        boolean includes(Page page) {
            true
        }
    }

    private static final Predicate<FoundationPage> DISPLAYABLE_ONLY = new Predicate<FoundationPage>() {
        @Override
        boolean test(FoundationPage page) {
            page.contentResource && !page.hideInNav
        }
    }

    @Delegate
    private final Page delegate

    private final Optional<ComponentResource> contentResource

    DefaultFoundationPage(Page page) {
        this.delegate = page

        contentResource = Optional.ofNullable(page.contentResource).map(RESOURCE_TO_COMPONENT_NODE)
    }

    @Override
    boolean equals(Object other) {
        new EqualsBuilder().append(path, (other as FoundationPage).path).equals
    }

    @Override
    int hashCode() {
        new HashCodeBuilder().append(path).hashCode()
    }

    @Override
    String toString() {
        Objects.toStringHelper(this)
            .add("path", path)
            .add("title", title)
            .toString()
    }

    @Override
    @SuppressWarnings("unchecked")
    <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        def result

        if (type == ComponentResource) {
            def resource = delegate.contentResource

            result = (AdapterType) resource?.adaptTo(ComponentResource)
        } else {
            result = delegate.adaptTo(type)
        }

        result
    }

    @Override
    ValueMap asMap() {
        getInternal({ componentResource -> componentResource.asMap() }, ValueMap.EMPTY)
    }

    @Override
    <T> T get(String propertyName, T defaultValue) {
        getInternal({ componentResource -> componentResource.get(propertyName, defaultValue) }, defaultValue)
    }

    @Override
    <T> Optional<T> get(String propertyName, Class<T> type) {
        getInternal({ componentResource -> componentResource.get(propertyName, type) }, Optional.empty())
    }

    @Override
    Optional<String> getAsHref(String propertyName) {
        getInternal({ componentResource -> componentResource.getAsHref(propertyName) }, Optional.empty())
    }

    @Override
    Optional<String> getAsHref(String propertyName, boolean strict) {
        getInternal({ componentResource -> componentResource.getAsHref(propertyName, strict) }, Optional.empty())
    }

    @Override
    Optional<String> getAsHref(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentResource -> componentResource.getAsHref(propertyName, strict, mapped) },
            Optional.empty())
    }

    @Override
    Optional<Link> getAsLink(String propertyName) {
        getInternal({ componentResource -> componentResource.getAsLink(propertyName) }, Optional.empty())
    }

    @Override
    Optional<Link> getAsLink(String propertyName, boolean strict) {
        getInternal({ componentResource -> componentResource.getAsLink(propertyName, strict) }, Optional.empty())
    }

    @Override
    Optional<Link> getAsLink(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentResource -> componentResource.getAsLink(propertyName, strict, mapped) },
            Optional.empty())
    }

    @Override
    <T> List<T> getAsList(String propertyName, Class<T> type) {
        getInternal({ componentResource -> componentResource.getAsList(propertyName, type) }, Collections.emptyList())
    }

    @Override
    Optional<FoundationPage> getAsPage(String propertyName) {
        getInternal({ componentResource -> componentResource.getAsPage(propertyName) }, Optional.empty())
    }

    @Override
    <AdapterType> Optional<AdapterType> getAsType(String propertyName, Class<AdapterType> type) {
        getInternal({ componentResource -> componentResource.getAsType(propertyName, type) }, Optional.empty())
    }

    @Override
    <AdapterType> List<AdapterType> getAsTypeList(String propertyName, Class<AdapterType> type) {
        getInternal({ componentResource -> componentResource.getAsTypeList(propertyName, type) },
            Collections.emptyList())
    }

    @Override
    Optional<String> getImageReference(boolean isSelf) {
        getInternal({ componentResource -> componentResource.getImageReference(isSelf) }, Optional.empty())
    }

    @Override
    Optional<String> getImageReference() {
        getInternal({ componentResource -> componentResource.imageReference }, Optional.empty())
    }

    @Override
    Optional<String> getImageReference(String name) {
        getInternal({ componentResource -> componentResource.getImageReference(name) }, Optional.empty())
    }

    @Override
    Optional<String> getImageRendition(String renditionName) {
        getInternal({ componentResource -> componentResource.getImageRendition(renditionName) }, Optional.empty())
    }

    @Override
    Optional<String> getImageRendition(String name, String renditionName) {
        getInternal({ componentResource -> componentResource.getImageRendition(name, renditionName) }, Optional.empty())
    }

    @Override
    List<Tag> getTags(String propertyName) {
        getInternal({ componentResource -> componentResource.getTags(propertyName) }, Collections.emptyList())
    }

    @Override
    <T> T getInherited(String propertyName, T defaultValue) {
        getInternal({ componentResource -> componentResource.getInherited(propertyName, defaultValue) }, defaultValue)
    }

    @Override
    <T> Optional<T> getInherited(String propertyName, Class<T> type) {
        getInternal({ componentResource -> componentResource.getInherited(propertyName, type) }, Optional.empty())
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName) {
        getInternal({ componentResource -> componentResource.getAsHrefInherited(propertyName) }, Optional.empty())
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict) {
        getInternal({ componentResource -> componentResource.getAsHrefInherited(propertyName, strict) },
            Optional.empty())
    }

    @Override
    Optional<String> getAsHrefInherited(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentResource -> componentResource.getAsHrefInherited(propertyName, strict, mapped) },
            Optional.empty())
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName) {
        getInternal({ componentResource -> componentResource.getAsLinkInherited(propertyName) }, Optional.empty())
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict) {
        getInternal({ componentResource -> componentResource.getAsLinkInherited(propertyName, strict) },
            Optional.empty())
    }

    @Override
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict, boolean mapped) {
        getInternal({ componentResource -> componentResource.getAsLinkInherited(propertyName, strict, mapped) },
            Optional.empty())
    }

    @Override
    <T> List<T> getAsListInherited(String propertyName, Class<T> type) {
        getInternal({ componentResource -> componentResource.getAsListInherited(propertyName, type) },
            Collections.emptyList())
    }

    @Override
    Optional<FoundationPage> getAsPageInherited(String propertyName) {
        getInternal({ componentResource -> componentResource.getAsPageInherited(propertyName) }, Optional.empty())
    }

    @Override
    <AdapterType> Optional<AdapterType> getAsTypeInherited(String propertyName, Class<AdapterType> type) {
        getInternal({ componentResource -> componentResource.getAsTypeInherited(propertyName, type) }, Optional.empty())
    }

    @Override
    Optional<String> getImageReferenceInherited(boolean isSelf) {
        getInternal({ componentResource -> componentResource.getImageReferenceInherited(isSelf) }, Optional.empty())
    }

    @Override
    Optional<String> getImageReferenceInherited() {
        getInternal({ componentResource -> componentResource.imageReferenceInherited }, Optional.empty())
    }

    @Override
    Optional<String> getImageReferenceInherited(String name) {
        getInternal({ componentResource -> componentResource.getImageReferenceInherited(name) }, Optional.empty())
    }

    @Override
    Optional<FoundationPage> findAncestor(Predicate<FoundationPage> predicate) {
        findAncestor(predicate, false)
    }

    @Override
    Optional<FoundationPage> findAncestor(Predicate<FoundationPage> predicate, boolean excludeCurrentResource) {
        FoundationPage page = excludeCurrentResource ? parent : this
        FoundationPage ancestorPage = null

        while (page) {
            if (predicate.test(page)) {
                ancestorPage = page
                break
            } else {
                page = page.parent
            }
        }

        Optional.ofNullable(ancestorPage)
    }

    @Override
    Optional<FoundationPage> findAncestorWithProperty(String propertyName) {
        findAncestorForPredicate(new ComponentResourcePropertyExistsPredicate(propertyName), false)
    }

    @Override
    Optional<FoundationPage> findAncestorWithProperty(String propertyName, boolean excludeCurrentResource) {
        findAncestorForPredicate(new ComponentResourcePropertyExistsPredicate(propertyName), excludeCurrentResource)
    }

    @Override
    <V> Optional<FoundationPage> findAncestorWithPropertyValue(String propertyName, V propertyValue) {
        findAncestorForPredicate(new ComponentResourcePropertyValuePredicate(propertyName, propertyValue), false)
    }

    @Override
    <V> Optional<FoundationPage> findAncestorWithPropertyValue(String propertyName, V propertyValue,
        boolean excludeCurrentResource) {
        findAncestorForPredicate(new ComponentResourcePropertyValuePredicate(propertyName, propertyValue),
            excludeCurrentResource)
    }

    @Override
    List<FoundationPage> findDescendants(Predicate<FoundationPage> predicate) {
        def pages = []
        def pageManager = this.pageManager

        delegate.listChildren(ALL_PAGES, true).each { child ->
            FoundationPage page = pageManager.getPage(child)

            if (predicate.test(page)) {
                pages.add(page)
            }
        }

        pages
    }

    @Override
    List<FoundationPage> getChildren() {
        filterChildren({ true }, false)
    }

    @Override
    List<FoundationPage> getChildren(boolean displayableOnly) {
        displayableOnly ? filterChildren(DISPLAYABLE_ONLY, false) : filterChildren({ true }, false)
    }

    @Override
    List<FoundationPage> getChildren(Predicate<FoundationPage> predicate) {
        filterChildren(checkNotNull(predicate), false)
    }

    @Override
    Iterator<FoundationPage> listChildPages() {
        listChildPages({ true })
    }

    @Override
    Iterator<FoundationPage> listChildPages(Predicate<FoundationPage> predicate) {
        listChildPages(predicate, false)
    }

    @Override
    Iterator<FoundationPage> listChildPages(Predicate<FoundationPage> predicate, boolean deep) {
        def resource = delegate.adaptTo(Resource)
        def iterator = deep ? new DeepResourceIterator(resource) : resource.listChildren()

        new FoundationPageIterator(iterator, predicate)
    }

    @Override
    Optional<FoundationPage> getChild(String name) {
        def child = Optional.empty()

        if (hasChild(name)) {
            child = Optional.of(delegate.adaptTo(Resource).getChild(name).adaptTo(FoundationPage))
        }

        child
    }

    @Override
    Optional<ComponentResource> getComponentResource() {
        contentResource
    }

    @Override
    Optional<ComponentResource> getComponentResource(String relativePath) {
        Optional<ComponentResource> componentResource

        if (contentResource.present) {
            componentResource = contentResource.get().getComponentResource(relativePath)
        } else {
            componentResource = Optional.empty()
        }

        componentResource
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
    ImageLink getImageLink(String imageSource) {
        LinkBuilderFactory.forPage(this).setImageSource(checkNotNull(imageSource)).buildImageLink()
    }

    @Override
    boolean isHasImage() {
        getInternal({ ComponentResource componentResource -> componentResource.hasImage }, false)
    }

    @Override
    boolean isHasImage(String name) {
        getInternal({ componentResource -> componentResource.isHasImage(name) }, false)
    }

    @Override
    Link getLink() {
        getLink(false)
    }

    @Override
    Link getLink(TitleType titleType) {
        getLinkBuilder(titleType, false).build()
    }

    @Override
    Link getLink(boolean mapped) {
        getLinkBuilder(mapped).build()
    }

    @Override
    Link getLink(TitleType titleType, boolean mapped) {
        getLinkBuilder(titleType, mapped).build()
    }

    @Override
    LinkBuilder getLinkBuilder() {
        getLinkBuilder(false)
    }

    @Override
    LinkBuilder getLinkBuilder(TitleType titleType) {
        getLinkBuilder(titleType, false)
    }

    @Override
    LinkBuilder getLinkBuilder(boolean mapped) {
        LinkBuilderFactory.forPage(this, mapped, TitleType.TITLE)
    }

    @Override
    LinkBuilder getLinkBuilder(TitleType titleType, boolean mapped) {
        LinkBuilderFactory.forPage(this, mapped, titleType)
    }

    @Override
    NavigationLink getNavigationLink() {
        getNavigationLink(false, false)
    }

    @Override
    NavigationLink getNavigationLink(boolean isActive) {
        getNavigationLink(isActive, false)
    }

    @Override
    NavigationLink getNavigationLink(boolean isActive, boolean mapped) {
        LinkBuilderFactory.forPage(this, mapped, TitleType.NAVIGATION_TITLE)
            .setActive(isActive)
            .buildNavigationLink()
    }

    // overrides

    @Override
    FoundationPage getAbsoluteParent(int level) {
        pageManager.getPage(delegate.getAbsoluteParent(level))
    }

    @Override
    FoundationPageManager getPageManager() {
        delegate.adaptTo(Resource).resourceResolver.adaptTo(FoundationPageManager)
    }

    @Override
    FoundationPage getParent() {
        pageManager.getPage(delegate.parent)
    }

    @Override
    FoundationPage getParent(int level) {
        pageManager.getPage(delegate.getParent(level))
    }

    @Override
    String getTemplatePath() {
        properties.get(NameConstants.NN_TEMPLATE, "")
    }

    @Override
    Optional<String> getTitle(TitleType titleType) {
        get(titleType.propertyName, String)
    }

    @Override
    String getTitle() {
        properties.get(NameConstants.PN_TITLE, "")
    }

    // internals

    private <T> T getInternal(Closure<T> closure, T defaultValue) {
        contentResource.map { componentResource -> closure.call(componentResource) }.orElse(defaultValue)
    }

    private Optional<FoundationPage> findAncestorForPredicate(Predicate<ComponentResource> predicate,
        boolean excludeCurrentResource) {
        FoundationPage page = excludeCurrentResource ? parent : this
        FoundationPage ancestorPage = null

        while (page) {
            Optional<ComponentResource> optionalComponentResource = page.componentResource

            if (optionalComponentResource.present && predicate.test(optionalComponentResource.get())) {
                ancestorPage = page
                break
            } else {
                page = page.parent
            }
        }

        Optional.ofNullable(ancestorPage)
    }

    private List<FoundationPage> filterChildren(Predicate<FoundationPage> predicate, boolean deep) {
        def pages = []
        def pageManager = this.pageManager

        delegate.listChildren(ALL_PAGES, deep).each { child ->
            def page = pageManager.getPage(child)

            if (page && predicate.test(page)) {
                pages.add(page)
            }
        }

        pages
    }
}
