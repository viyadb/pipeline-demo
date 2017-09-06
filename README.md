pipeline-demo
==============

Demonstration of a real-time process baked by ViyaDB.

[![Build Status](https://travis-ci.org/viyadb/pipeline-demo.png)](https://travis-ci.org/viyadb/pipeline-demo)

## Components

The process uses [TestContainers](https://www.testcontainers.org) framework for starting, and orchestrating needed Docker containers.


     +--------------------+          +-----------+              +-----------------+
     |                    |          |           |              |                 |
     |                    |   pipe   |           |              |                 |
     |  Events Generator  |--------> |   Kafka   |              | Configuration   |
     |   (Docker)         |          |  (Docker) |              | (Consul Docker) |
     |                    |          |           |              |                 |
     +--------------------+          +-----------+              +-----------------+
                                           |                       ^
                                           |                       |
                                           v                       |
                                 +--------------------+            |
                                 |                    |            |
                                 |                    |            |
                                 |   Spark Streaming  |------------+
                                 |                    |
                                 |                    |
                                 +--------------------+
                                           |
                                           |  read generated files
                                           v
                                    +--------------+
                                    |              |
                                    |    Loader    |
                                    |              |
                                    +--------------+
                                           |
                                           |  load data via REST
                                           v
                                   +----------------+
                                   |                |
                                   |    ViyaDB      |
                                   |    (Docker)    |
                                   |                |
                                   +----------------+


## Usage

### Prerequisites

 * [Leiningen](http://leiningen.org) >= 2.7.1

### Building the project

    lein uberjar

### Running

    java -jar target/pipeline-demo-0.1.0-standalone.jar

When running for the first time, it might take some time to pull all required Docker images, please be patient.

