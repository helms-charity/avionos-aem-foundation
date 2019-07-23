## Servlets

### Abstract JSON Response Servlet

`com.avionos.aem.foundation.core.servlets.AbstractJsonResponseServlet`

Servlets should extend this class when writing a JSON response.  Objects passed to any of the `writeJsonResponse` methods will be serialized to the response writer using the [Jackson](https://github.com/FasterXML/jackson-databind) data binding library.

### Abstract Options Provider Servlet

`com.avionos.aem.foundation.core.servlets.optionsprovider.AbstractOptionsProviderServlet`

Base class for providing a list of "options" to a component dialog widget.  An option is simply a text/value pair to be rendered in a selection box.  The implementing class determines how these options are retrieved from the repository (or external provider, such as a web service).

### Abstract Options Data Source Servlet

`com.avionos.aem.foundation.core.servlets.datasource.AbstractOptionsDataSourceServlet`

Base class for supplying a data source to component dialogs using the Touch UI.  Implementing classes will provide a list of options that will be made available as text/value pairs to selection dialog elements.  Servlets must be annotated with the `@SlingServletResourceTypes(resourceTypes = "projectname/datasources/colors")` annotation.  The resource type attribute is an arbitrary relative path that can be referenced by dialog elements using the data source.  The implementing class determines how these options are retrieved from the repository (or external provider, such as a web service).

### Abstract Tag Data Source Servlet

`com.avionos.aem.foundation.core.servlets.datasource.AbstractTagDataSourceServlet`

Extends the `AbstractOptionsDataSourceServlet` and solely focuses on building options from tags within the repository. A basic extension is to provide just the namespace of the tags and the servlet will build a list of options from all direct descendants of that namespace tag. Extending classes may optionally provide a more granular tag path and a custom filter.
