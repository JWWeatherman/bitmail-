# Bitmail
---

Prerequisites
  - NodeJS v- 7.10.1
    - Use [N](https://github.com/tj/n)
  - WebPack installed globally 
    - ```npm install webpack -g``` 
  - MongoDB install globally
    - Goto [MongoDb](https://docs.mongodb.com/manual/installation/)
  
### Getting started,

* This application is not using any of the Scala play views and all the views are served by the [Vue](https://vuejs.org/) code base which is inside the `ui` folder.

* Used any of the sbt commands listed in the below according to the requirement which are working fine with this application.(To see more details of [sbt](http://www.scala-sbt.org/))

``` 
    sbt clean               # Clear existing build files
    
    sbt stage               # Build your application from your project’s source directory 
                            (When finished restart SBT Shell)
    sbt run                 # Run both backend and frontend builds in watch mode
    
    sbt dist                # Build both backend and frontend sources into a single distribution
                            (When finished restart SBT Shell)
    !! sbt test             # Run both backend and frontend unit tests !! *coming soon!  
```





 