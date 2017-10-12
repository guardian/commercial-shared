# Commercial shared library

This codebase is going to hold the business logic for the display of commercial components, so that it can be shared across all Guardian platforms: web and app.

## Assumptions
1. It will be included in all frontend deployments that show commercial components
1. It will be included in mapi deployments 
1. It won't make any calls to external services
1. Its only dependencies are the capi model and the facia model

## Usage

### Configuration
1. Use bintray resolver:  
   `resolvers += "Guardian Frontend Bintray" at "https://dl.bintray.com/guardian/frontend"`
1. Add library as dependency:  
   `libraryDependencies += "com.gu" %% "commercial-shared" % "<x.y.z>"`

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

### Deploy
[You need to have an account on [bintray](https://bintray.com/) and be a member of the Guardian organisation there.  
You also need to have run the sbt `bintrayChangeCredentials` task to generate a credentials file.]  
Then:  
Run the sbt `release` task.  
This will generate artefacts and make them available from [bintray](https://bintray.com/guardian/frontend/commercial-shared).  
Releases follow the [semantic versioning](http://semver.org/) policy, which is roughly:

* A *major*.*minor*.*patch* format  
* Bump patch number for a bug fix or dependency bump etc.  
* Bump minor number for non-breaking new features  
* Bump major number for breaking changes  
