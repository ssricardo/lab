# Documenting REST APIs With Swagger

> The below article was based in a configuration I had to do in an application which I've worked. After some problems of a library (jackson) conflicting between the container version and Swagger dependency, a different approach was thought.

## Pre requirements

* Knoledge of REST webservices
* Basic knoledge of Swagger

## Overview

> Swagger is the world’s largest framework of API developer tools for the OpenAPI Specification(OAS), enabling development across the entire API lifecycle, from design and documentation, to test and deployment.

Swagger is a conjunct of tools used for APIs documentation, usually REST.  
Basicaly, it's based on one file, a JSON - for instance, in which the documentation is organized in a structured way. Once you have, it's possible to present this doc in a web interface with another part of these tools, Swagger-UI. 

To manage the *swagger.json* file, it's provided the tool *Swagger Editor*. However, this approach to generate and maintain this file [manually] will likely lead to problems, like the file becoming outdated.   
A better idea, instead of writing *swagger.json* [manually], would be getting it generated from the source code. This is possible through Swagger API. In Java, this is done through some annotations in the source code itself. 

> Swagger support others languages besides Java. Take a look at its doc

### Configuration overview

Focusing on Java REST (jax-rs) projects, generaly *swagger.json* si generated from source code. This is achieved [rather times] [ataching] Swagger to the application and using it in Runtime.  
In order to get this scenario working, the following steps are required:  

**JaxRS 1**

* *swagger-jaxrs_2.10* dependency in *pom.xml*
	* Versions 1.3.x
* A Servlet to configure Swagger to serve docs
	* Look for *SwaggerConfig*
* In Application class, register Swagger providers:
	* ApiDeclarationProvider.class;
    * ApiListingResourceJSON.class;
    * ResourceListingProvider.class;
* In each (rest) service, [annotate] the class with `@Api` and the methods with `@ApiOperation`

**JaxRS 2**

* *swagger-jaxrs_2.10* dependency in *pom.xml*
	* Versions 1.5.x
* The Servlet isn't necessary
* In Application class:
	* [Annotate] with `@SwaggerDefinition`, [filling out] at least *info* and *tags*
	* Configure *BeanConfig*
	* Register Swagger providers:
		* ApiListingResource.class;
	    * SwaggerSerializers.class;		
* In each service, [annotate] the classe with `@Api` and the methods with `@ApiOperation`

> There are other alternatives providers, but those mentioned above are sufficient already

In both cases, you need to copy the UI files [directaly] from [*swagger-ui*](https://github.com/swagger-api/swagger-ui) to your local project (Caution: the version of swagger-ui must be [compatible](https://github.com/swagger-api/swagger-ui#compatibility) with API version).

> This is the standard option in the company where I work

## Swagger with Maven - A different way

For the sake of getting *swagger.json* generated in runtime, Swagger [appears] as a regular library, which has rather dependencies on other libraries. 
The [danger] side of this is that it increases the risk of conflicts; besides, it restricts the project dependencies to a version compatible with Swagger.  
For instance, *Jackson* (*jackson-jaxrs-json-provider*) is one of its dependencies; the same library is required by other framework or it's is even already available in the Java EE server (like Weblogic). In our company, in a scenerio like this, it wasn't possible to use the container's version, duo to incompability. In this case it's necessary to package it and force the container to prefer the packaged version.  

Considering that API documentation will never be modified in runtime (it references the code - it won't be modified without a new version of the application), it's is not necessary having this generated in runtime.  

In such a way, the idea is:

* Generate *swagger.json* in compile time
* Remove (not include) Swagger libraries and dependencies in the build

Achieving this is possible [through] *swagger-maven-plugin* (a plugin for Maven, well... [obviously]).

### Configuring Maven

The plugin configuration for *JAX-RS 2.0* (in pom.xml): 

		<plugin>
			<groupId>com.github.kongchen</groupId>
			<artifactId>swagger-maven-plugin</artifactId>
			<version>3.1.4</version>
			...
		</plugin>

> [https://github.com/kongchen/swagger-maven-plugin](https://github.com/kongchen/swagger-maven-plugin)		

Those Swagger configurations which were done through the annotation `@SwaggerDefinition` in the application class, can be done in the plugin in *pom.xml*.  

		<configuration>
				<apiSources>
					<apiSource>
						<locations>
							<location>br.org.rss.mypackage</location>
						</locations>
						<schemes>
							<scheme>http</scheme>
						</schemes>
						<basePath>/app-context/rest-context</basePath>
						<info>
							<title>Servicos REST</title>
							<version>1.0</version>
							<description>
								Lorem...
							</description>
						</info>

						<swaggerDirectory>${project.build.directory}/${project.build.finalName}/api</swaggerDirectory>
						<attachSwaggerArtifact>true</attachSwaggerArtifact>
					</apiSource>
				</apiSources>
		</configuration>


Although there is still one dependency on swagger in project, it is a more restrict (smaller) one and only used in compile time. Thus, it can be *provided* (won't affect anything in the runtime application). This is necessary duo the annotations in the services.  

		<dependency>
			<groupId>io.swagger</groupId>
            <artifactId>swagger-models</artifactId>
            <version>${swagger.version}</version>
			<scope>provided</scope>
		</dependency>

Furthermore it isn't necessary to package and configure Jackson, Scala and other libs.

--------

---

