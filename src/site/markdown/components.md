## Components

### Overview

The introduction of the HTL templating language has eliminated the need for custom JSP tags, scriptlets, and other unpleasantries when separating a component's view from it's supporting business logic.  The [Sling Models](https://sling.apache.org/documentation/bundles/models.html) framework offers a robust, POJO-based development pattern that the Avionos AEM Foundation augments to greatly simplify AEM component development.

### Abstract Component Class

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

### Injectable Component Resource

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

### Sling Models Injectors

In addition to the Avionos AEM Foundation's component API, the library also supplies a set of custom Sling Models injectors to support injection of common Sling and AEM objects for the current component.  See the [Injectors](/avionos-aem-foundation/injectors.html) page for additional information.

### HTL Integration

Sling Models-based components (i.e. POJOs with the `@org.apache.sling.models.annotations.Model` annotation) can be instantiated in HTL templates with a [data-sly-use](https://github.com/Adobe-Marketing-Cloud/sightly-spec/blob/master/SPECIFICATION.md#221-use) block statement.  Since the Avionos AEM Foundation components are just "decorated" Sling Models, nothing additional is required.

The HTL template for the preceding `Navigation` component would be implemented as follows:

    <sly data-sly-use.navigation="com.projectname.components.content.Navigation">
        <h1>${navigation.title}</h1>
    
        <ul data-sly-list.page="${navigation.pages}">
            <li><a href="${page.href}">${page.title}</a></li>
        </ul>
    </sly>

### Component Development Guidelines

* Component beans should be **read-only** since requests in publish mode are generally bound to an anonymous user without write access.  Repository write operations should be performed only in author mode (and replicated only when a page is activated by a content author).  Since component classes are executed in both author and publish modes, ideally one should consider alternative approaches to performing write operations in a component bean:
    * Delegate write operations to an OSGi service containing a service-appropriate Sling Resource Resolver.
    * Refactor the component to perform dialog-based content modifications by attaching a listener to a [dialog event](https://helpx.adobe.com/experience-manager/using/creating-touchui-events.html).
    * Register a [JCR](https://docs.adobe.com/docs/en/spec/javax.jcr/javadocs/jcr-2.0/javax/jcr/observation/ObservationManager.html) or an OSGi Event Handler to trigger event-based repository updates.
* Classes should remain stateless and contain no setter methods.  Since the lifecycle of a component/model is bound to a request, state should be maintained client-side using cookies, local storage, or HTML data attributes.