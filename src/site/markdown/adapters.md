## Adapters

Sling `Resource` and `ResourceResolver` instances are adaptable to Avionos AEM Foundation types as outlined below.

### Resource

Adapter | Details
:-------|:-----
FoundationPage | Only applies when the `Resource` path is a valid page path, returns `null` otherwise.
ComponentResource | Applies to all `Resource` instances.

### ResourceResolver

Adapter | Details
:-------|:-----
FoundationPageManager | Applies to all `ResourceResolver` instances.