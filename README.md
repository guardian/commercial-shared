# Commercial shared library [![Release](https://github.com/guardian/commercial-shared/actions/workflows/release.yml/badge.svg)](https://github.com/guardian/commercial-shared/actions/workflows/release.yml)

This codebase holds the business logic for the display of commercial components, so that it can
be shared across all Guardian platforms: web and app.

## Assumptions

1. It will be included in all frontend deployments that show commercial components
1. It will be included in mapi deployments
1. It won't make any calls to external services
1. Its only dependencies are the capi model and the facia model

## Usage

Add the library as dependency:
`libraryDependencies += "com.gu" %% "commercial-shared" % "<x.y.z>"`

### Known usages

- [Frontend](https://github.com/guardian/frontend/blob/master/project/Dependencies.scala#L70)
- [Mapi](https://github.com/guardian/mobile-apps-api/blob/master/project/Dependencies.scala#L49)
- [Facia Scala Client](https://github.com/guardian/facia-scala-client/blob/master/project/dependencies.scala#L14)

### Examples

1. Find branding for a given content item, section, tag or container:
   See [branding tests](src/test/scala/com/gu/commercial/branding).
1. Build display ad targeting for a given content item, section or tag:
   See [ad targeting tests](src/test/scala/com/gu/commercial/display).

## How to ...

### Start sbt session

Run `bin/activator`

### Test locally in a downstream project

Run the sbt `publishLocal` task.
This will generate a local snapshot artefact.
Add the snapshot version as a dependency of the downstream project.

### Release

See: https://github.com/guardian/gha-scala-library-release-workflow/blob/main/docs/making-a-release.md
