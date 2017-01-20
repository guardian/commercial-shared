# Commercial shared library

This codebase is going to hold the business logic for the display of commercial components, so that it can be shared across all Guardian platforms: web and app.

## Initial assumptions
0. It will be included in all frontend deployments that show commercial components
0. It will be included in mapi deployments 
0. It won't make any calls to external services
0. Its only dependency is the capi model

## Usage

### Configuration
1. Use bintray resolver:  
   `resolvers += "Guardian Frontend Bintray" at "https://dl.bintray.com/guardian/frontend"`
1. Add library as dependency:  
   `libraryDependencies += "com.gu" %% "commercial-shared" % "<x.y.z>"`

### Examples
1. Find branding for a given page, container, section or tag:  
   See [tests](src/test/scala/com/gu/commercial/branding/BrandingFinderSpec.scala).

## How to ...

### Start sbt session
Run `bin/activator`

### Deploy
Run the sbt `release` task.  
This will generate artefacts and make them available from bintray.
