package com.avionos.aem.foundation.api;

import com.avionos.aem.foundation.api.link.Link;
import com.avionos.aem.foundation.api.page.FoundationPage;

import java.util.List;
import java.util.Optional;

/**
 * An accessible instance (such as a <code>Resource</code> or <code>Page</code>) that supports hierarchy-based content
 * inheritance.
 */
public interface Inheritable {

    /**
     * Given a property on this node containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this component.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName);

    /**
     * Given a property on this node containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when appending ".html" to the resource path is desired only for valid CQ pages and not external paths.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have
     * ".html" appended
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName, boolean strict);

    /**
     * Given a property on this node containing the path of another resource, get the href to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when appending ".html" to the resource path is desired only for valid CQ pages and not external paths.  Setting
     * <code>mapped</code> to <code>true</code> will map the path value, if it exists, through the Sling Resource
     * Resolver.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have
     * ".html" appended
     * @param mapped if true, the property value will be routed through the Resource Resolver to determine the mapped
     * path for the value.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource
     * Resolver Factory OSGi configuration, getting the mapped href for the path "/content/avionos" will return
     * "/avionos.html".
     * @return <code>Optional</code> href
     */
    Optional<String> getAsHrefInherited(String propertyName, boolean strict, boolean mapped);

    /**
     * Given a property on this node containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this component.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link object, or null if the property does not contain a valid content path
     */
    Optional<Link> getAsLinkInherited(String propertyName);

    /**
     * Given a property on this node containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when including an extension for the link is desired only for valid CQ pages and not external paths.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have an
     * extension
     * @return <code>Optional</code> link object, or null if the property does not contain a valid content path
     */
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict);

    /**
     * Given a property on this node containing the path of another resource, get a link to the resource, using
     * inheritance if the value does not exist on this component.  Use this method with a <code>true</code> argument
     * when including an extension for the link is desired only for valid CQ pages and not external paths.  Setting
     * <code>mapped</code> to <code>true</code> will map the path value, if it exists, through the Sling Resource
     * Resolver.
     *
     * @param propertyName name of property containing a valid content path
     * @param strict if true, strict resource resolution will be applied and only valid CQ content paths will have an
     * extension
     * @param mapped if true, the property value will be routed through the Resource Resolver to determine the mapped
     * path for the value.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource
     * Resolver Factory OSGi configuration, the <code>Link</code> path will be "/avionos" rather than
     * "/content/avionos".
     * @return <code>Optional</code> link object, or null if the property does not contain a valid content path
     */
    Optional<Link> getAsLinkInherited(String propertyName, boolean strict, boolean mapped);

    /**
     * Get a multi-valued property from the current node as a list of the given type, using inheritance if the value
     * does not exist on this component.
     *
     * @param propertyName name of multi-valued property
     * @param type property type
     * @param <T> property type
     * @return list of property values or an empty list if the property does not exist
     */
    <T> List<T> getAsListInherited(String propertyName, Class<T> type);

    /**
     * Get a page from the value of the given property, using inheritance if the value does not exist on this component.
     * The property value will be localized to the current page context before getting the page.
     *
     * @param propertyName property name
     * @return <code>Optional</code> page for property value
     */
    Optional<FoundationPage> getAsPageInherited(String propertyName);

    /**
     * Get an <code>Optional</code> type instance for a property on this resource containing the path of another
     * <code>Resource</code> in the repository, using inheritance if the value does not exist on this component..
     *
     * @param propertyName name of property containing a resource path
     * @param type type to adapt from resource
     * @param <AdapterType> adapter class that is adaptable from <code>Resource</code>
     * @return <code>Optional</code> instance of the specified type, or absent if either the property does not exist or
     * the resource does not adapt to the provided type
     */
    <AdapterType> Optional<AdapterType> getAsTypeInherited(String propertyName, Class<AdapterType> type);

    /**
     * @param isSelf if true, component will attempt to find the image reference property on the current resource
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited(boolean isSelf);

    /**
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited();

    /**
     * @param name image name
     * @return <code>Optional</code> inherited image reference
     */
    Optional<String> getImageReferenceInherited(String name);

    /**
     * Get a property value from the current node. If no value is found, recurse up the content tree respective to the
     * page and relative node path until a value is found.
     *
     * @param <T> result type
     * @param propertyName property to get
     * @param defaultValue value if no result is found
     * @return inherited value
     */
    <T> T getInherited(String propertyName, T defaultValue);

    /**
     * Get a property value from the current node.   If no value is found, recurse up the content tree respective to the
     * page and relative node path until a value is found, returning an absent <code>Optional</code> if not.  This
     * returns the same value as the underlying <code>ValueMap</code> wrapped in an <code>Optional</code> instance
     * instead of returning null.
     *
     * @param propertyName property name
     * @param type property type
     * @param <T> type
     * @return <code>Optional</code> of the given type containing the property value or absent if no value is found
     */
    <T> Optional<T> getInherited(String propertyName, Class<T> type);
}
