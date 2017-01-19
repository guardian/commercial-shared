# Commercial shared library

This codebase is going to hold the business logic for the display of commercial components, so that it can be shared across all Guardian platforms: web and app.

## Initial assumptions
0. It will be included in all frontend deployments that show commercial components
0. It will be included in mapi deployments 
0. It won't make any calls to external services
0. Its only dependency is the capi model

## To deploy
Run the sbt `release` task.  
This will generate artefacts and make them available from bintray.
