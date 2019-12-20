# Avionos AEM Foundation

[Avionos](http://www.avionos.com)

## Overview

The Avionos AEM Foundation is a set of bundles designed to streamline and simplify development of Adobe Experience Manager projects.

* Accelerate project development by defining a simple process for implementing the underlying component logic.
* Ensure a consistent structure and implementation pattern for all component classes.
* Isolate AEM-specific concerns (accessing the JCR, adapting Sling and AEM objects, building links) into common modules.
* Achieve a high level of code reuse both within and across AEM projects.

Bundle | Description
------------ | -------------
API | The Foundation API includes extensions for the Sling `Resource` and AEM `Page`/`PageManager` interfaces, as well as a `Link` interface encapsulating the typical attributes of a link object.
Core | The Core bundle implements the Foundation API in addition to providing a set of abstract servlets for returning JSON responses and supplying Touch UI data sources.
Injectors | Sling Model Injectors for a variety of common use cases - e.g. component context objects, enums, tags, images, references.

## Compatibility

Avionos AEM Foundation Version(s) | AEM Version(s)
------------ | -------------
0.x.x | 6.3, 6.4, 6.5

## Javadocs

https://avionosllc.github.io/avionos-aem-foundation/apidocs/index.html

## User Guide

### Adapters

Sling `Resource` and `ResourceResolver` instances are adaptable to Avionos AEM Foundation types as outlined below.

#### Resource

Adapter | Details
:-------|:-----
`FoundationPage` | Only applies when the `Resource` path is a valid page path, returns `null` otherwise.
`ComponentResource` | Applies to all `Resource` instances.

#### ResourceResolver

Adapter | Details
:-------|:-----
`FoundationPageManager` | Applies to all `ResourceResolver` instances.

### Components

#### Overview

The introduction of the HTL templating language has eliminated the need for custom JSP tags, scriptlets, and other unpleasantries when separating a component's view from it's supporting business logic.  The [Sling Models](https://sling.apache.org/documentation/bundles/models.html) framework offers a robust, POJO-based development pattern that the Avionos AEM Foundation augments to greatly simplify AEM component development.

#### Abstract Component Class

Model classes may extend the `com.avionos.aem.foundation.core.components.AbstractComponent` class to expose numerous convenience methods for retrieving/transforming property values, traversing the content repository, and generally reducing the amount of boilerplate code needed to perform common node- and property-based operations for a component.

The Java/Groovy model class for the component should expose getters for the values that required to render the component's view.

    import com.avionos.aem.foundation.core.components.AbstractComponent
    import com.avionos.aem.foundation.api.content.page.FoundationPage
    import org.apache.sling.models.annotations.Model
   
	@Model(adaptables = [Resource, SlingHttpServletRequest])
    class Navigation extends AbstractComponent {
    
        @Inject
        FoundationPage currentPage

        String getTitle() {
            get("jcr:title", "")
        }

        List<FoundationPage> getPages() {
            currentPage.getChildren(true)
        }
    } 

#### Injectable Component Resource

Alternatively, model classes may inject an instance of the `com.avionos.aem.foundation.api.resource.ComponentResource` class to provide the same functionality as the abstract class described above.

    import com.avionos.aem.foundation.api.resource.ComponentResource
    
    @Model(adaptables = [Resource, SlingHttpServletRequest])
    class Navigation {
    
        @Inject
        ComponentResource componentResource

        String getTitle() {
            componentResource.get("jcr:title", "")
        }
    }

See the `ComponentResource` [Javadoc](http://avionosllc.github.io/avionos-aem-foundation/apidocs/com/avionos/aem/foundation/api/resource/ComponentResource.html) for details of the available methods.

#### Sling Models Injectors

In addition to the Avionos AEM Foundation's component API, the library also supplies a set of custom Sling Models injectors to support injection of common Sling and AEM objects for the current component.  See the [Injectors](/avionos-aem-foundation/injectors.html) page for additional information.

#### HTL Integration

Sling Models-based components (i.e. POJOs with the `@org.apache.sling.models.annotations.Model` annotation) can be instantiated in HTL templates with a [data-sly-use](https://github.com/Adobe-Marketing-Cloud/sightly-spec/blob/master/SPECIFICATION.md#221-use) block statement.  Since the Avionos AEM Foundation components are just "decorated" Sling Models, nothing additional is required.

The HTL template for the preceding `Navigation` component would be implemented as follows:

    <sly data-sly-use.navigation="com.projectname.components.content.Navigation">
        <h1>${navigation.title}</h1>
    
        <ul data-sly-list.page="${navigation.pages}">
            <li><a href="${page.href}">${page.title}</a></li>
        </ul>
    </sly>

#### Component Development Guidelines

* Component classes should be **read-only** since requests in publish mode are generally bound to an anonymous user without write access.  Repository write operations should be performed only in author mode (and replicated only when a page is activated by a content author).  Since component classes are executed in both author and publish modes, ideally one should consider alternative approaches to performing write operations in a component bean:
    * Delegate write operations to an OSGi service containing a service-appropriate Sling Resource Resolver.
    * Refactor the component to perform dialog-based content modifications by attaching a listener to a [dialog event](https://helpx.adobe.com/experience-manager/using/creating-touchui-events.html).
    * Register a [JCR](https://docs.adobe.com/docs/en/spec/javax.jcr/javadocs/jcr-2.0/javax/jcr/observation/ObservationManager.html) or an OSGi Event Handler to trigger event-based repository updates.
* Classes should remain stateless and contain no setter methods.  Since the lifecycle of a component/model is bound to a request, state should be maintained client-side using cookies, local storage, or HTML data attributes.

### Constants

See the [Javadoc](http://avionosllc.github.io/avionos-aem-foundation/apidocs/com/avionos/aem/foundation/core/constants/package-summary.html) for constant value details.

### Including in AEM Projects

The including project's Core and UI module POM files should include the Avionos AEM Foundation API, Core, and Injectors modules as provided dependencies.

    <dependencies>
        [...]
        <dependency>
            <groupId>com.avionos.aem.foundation</groupId>
            <artifactId>avionos-aem-foundation-api</artifactId>
            <version>${project.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.avionos.aem.foundation</groupId>
            <artifactId>avionos-aem-foundation-core</artifactId>
            <version>${project.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.avionos.aem.foundation</groupId>
            <artifactId>avionos-aem-foundation-injectors</artifactId>
            <version>${project.version}</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

Optionally, the Avionos AEM Foundation parent POM can be defined as the project's parent to inherit dependency and plugin management.

    <parent>
        <groupId>com.avionos.aem.foundation</groupId>
        <artifactId>avionos-aem-foundation</artifactId>
        <version>${project.version}</version>
    </parent>

Finally, add the Foundation and Groovy bundle dependencies as embedded artifacts in the package plugin configuration in the project's UI module.

    <build>
        <plugins>
            [...]
            <plugin>
                <groupId>com.day.jcr.vault</groupId>
                <artifactId>content-package-maven-plugin</artifactId>
                <version>0.5.1</version>
                <extensions>true</extensions>
                <configuration>
                    [...]
                    <embeddeds>
                        <embedded>
                            <groupId>com.avionos.aem.foundation</groupId>
                            <artifactId>avionos-aem-foundation-core</artifactId>
                            <target>/apps/project-name/install</target>
                            <excludeTransitive>true</excludeTransitive>
                        </embedded>
                        <embedded>
                            <groupId>com.avionos.aem.foundation</groupId>
                            <artifactId>avionos-aem-foundation-api</artifactId>
                            <target>/apps/project-name/install</target>
                        </embedded>
                        <embedded>
                            <groupId>com.avionos.aem.foundation</groupId>
                            <artifactId>avionos-aem-foundation-injectors</artifactId>
                            <target>/apps/project-name/install</target>
                        </embedded>
                        <embedded>
                            <groupId>org.codehaus.groovy</groupId>
                            <artifactId>groovy-all</artifactId>
                            <target>/apps/project-name/install</target>
                        </embedded>
                    </embeddeds>
                </configuration>
            </plugin>
        </plugins>
    </build>

### Links

The `Link` object encapsulates the properties of an HTML link, including the decomposition of the URL "parts" according to Sling (i.e. path, selectors, extension).  A `Link` or collection of Links can be returned from a component bean to represent a navigation structure, for example.  The `LinkBuilder` class provides numerous methods to build immutable `Link` instances.  The builder itself is acquired through one of several static factory methods that accept a `Page`, `Resource`, or JCR path value.

### Servlets

#### Abstract JSON Response Servlet

`com.avionos.aem.foundation.core.servlets.AbstractJsonResponseServlet`

Servlets should extend this class when writing a JSON response.  Objects passed to any of the `writeJsonResponse` methods will be serialized to the response writer using the [Jackson](https://github.com/FasterXML/jackson-databind) data binding library.

#### Abstract Options Provider Servlet

`com.avionos.aem.foundation.core.servlets.optionsprovider.AbstractOptionsProviderServlet`

Base class for providing a list of "options" to a component dialog widget.  An option is simply a text/value pair to be rendered in a selection box.  The implementing class determines how these options are retrieved from the repository (or external provider, such as a web service).

#### Abstract Options Data Source Servlet

`com.avionos.aem.foundation.core.servlets.datasource.AbstractOptionsDataSourceServlet`

Base class for supplying a data source to component dialogs using the Touch UI.  Implementing classes will provide a list of options that will be made available as text/value pairs to selection dialog elements.  Servlets must be annotated with the `@SlingServletResourceTypes(resourceTypes = "projectname/datasources/colors")` annotation.  The resource type attribute is an arbitrary relative path that can be referenced by dialog elements using the data source.  The implementing class determines how these options are retrieved from the repository (or external provider, such as a web service).

#### Abstract Tag Data Source Servlet

`com.avionos.aem.foundation.core.servlets.datasource.AbstractTagDataSourceServlet`

Extends the `AbstractOptionsDataSourceServlet` and solely focuses on building options from tags within the repository. A basic extension is to provide just the namespace of the tags and the servlet will build a list of options from all direct descendants of that namespace tag. Extending classes may optionally provide a more granular tag path and a custom filter.

### Sling Models Injectors

The Avionos AEM Foundation provides several custom Sling Models injectors to facilitate injection of Avionos AEM Foundation-specific context objects.  Injector services are registered by default when the Avionos AEM Foundation Injectors bundle is installed, so `@Inject` annotated fields in model classes will be handled by the injectors below in addition to the default set of injectors provided by Sling. 

#### Available Injectors

Title | Name | Service Ranking | Description 
:-----|:-----|:----------------|:-----------
Resource Resolver Adaptable Injector | adaptable | `Integer.MIN_VALUE` | Injects objects that can be adapted from `ResourceResolver`, e.g. `PageManagerDecorator`
Component Content Injector | component | `Integer.MAX_VALUE` | Injects objects derived from the current component context
Enum Injector | enum | 4000 | Injects an enum for the property value matching the annotated field name
Image Injector | images | 4000 |  Injects `com.day.cq.wcm.foundation.Image` from the current resource
Inherit Injector | inherit | 4000 | Injects a property that inherits from ancestor pages if it isn't found on the current resource
Link Injector | links | 4000 | Injects `com.avionos.aem.foundation.api.link.Link` derived from the property value for the current field name
Model List Injector | model-list | 999 | Injects a list of models from adapted from the child of a named child resource
Reference Injector | references | 4000 | Injects a resource or object adapted from a resource based on the value of a property
Tag Injector | tags | 800 | Resolves a `com.day.cq.tagging.Tag` object (or list of tags) for the given property
ValueMap Injector | valuemap | 2500 |  Injects a property value with the given type from a `ValueMap`

#### Injector-specific Annotations

Annotation | Supported Optional Elements | Injector
:----------|:----------------------------|:--------
`@ImageInject` | `injectionStrategy, isSelf, inherit, selectors` | images
`@InheritInject` | `injectionStrategy` | inherit
`@LinkInject` | `injectionStrategy, titleProperty, inherit` | links
`@ReferenceInject` | `injectionStrategy, inherit` | references
`@TagInject` | `injectionStrategy, inherit` | tags

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.