# Adobe AEM / CQ Integration Tests Example

This project demonstrates how to use the [Sling Testing Tools](http://sling.apache.org/documentation/development/sling-testing-tools.html) to test components in a running AEM instance.
The default behaviour is to start up a new AEM instance as part of the maven build, but an already running instance can be used by the use of extra parameters.

## Setting up

Before you use this project you must do the following

1. Enter your AEM license details in the `it.launcher/src/test/resources/license.properties`
2. Install the AEM quickstart JAR in a maven repository with the following coordinates

        <dependency>
            <groupId>com.adobe.aem</groupId>
            <artifactId>cq-quickstart</artifactId>
            <version>6.0.0</version>
            <classifier>standalone</classifier>
        </dependency>

3. Modify your maven settings.xml or the top level pom in this project with the maven repository details you used in step 2.
4. Install maven 3.2 or later and place on your path
5. Install Java 6 or 7 and place on your path

## Running the tests in a new AEM instance

This mode of use is ideal for a CI setup. The AEM 6 quickstart will be started up using a free HTTP port, the bundles from this project installed and then the integration tests run.
To do this, run the following from the top level directory of this project

    mvn clean verify -P integrationTests

Note that this takes some time - on a 16Gb MacBook Pro Retina with SSD this takes around 4 minutes

## Running the tests in an already running instance

This mode is useful for developers who need to run the tests rapidly during day to day development. With an already running AEM instance
using defaults, run the following from the top level directory of this project

    mvn clean verify -P integrationTests -Dtest.server.url=http://localhost:4502

The test setup will be left in place after the tests have run. This allows you to debug any failing tests.

## Deploying the project

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage -PautoInstallBundle
    
    
## Modules and interesting bits
    
The main parts of the project are:

* models: contains a [Sling Model] bean (http://sling.apache.org/documentation/bundles/models.html) to support the Title component
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs as well as Hobbes-tests
* ui.content: contains sample content using the components from the ui.apps
* it.launcher: contains glue code that deploys the project bundles to the AEM server and triggers the integration tests

The `com.ninedemons.aemtesting.models.title.TitleModel` class is a Sling Model that is used by the Title component at `ui.apps/src/main/content/jcr_root/apps/aemtesting/components/content/title`. This 
java class is a drop in replacement for the `title.js` file that was used originally in the Sightly `title.html` markup.

The most interesting parts are `it.launcher/pom.xml` which bootstraps the AEM instance and `it.launcher/src/test/java/com/ninedemons/aemtesting/models/title/TitleModelTest.java` which uploads a test JSP, creates content and then checks the result is correct.
Only one scenario is in the test at the moment, but is shows how the approach works.    

## Project generation

This project was originally generated from the Adobe Marketing Cloud [AEM maven archetype](https://github.com/Adobe-Marketing-Cloud/aem-project-archetype).