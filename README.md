pipeline-demo
==============

Demonstration of a real-time process baked by ViyaDB.

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
                                           |
                                           v
     
                                       // TODO ...


## Usage

### Prerequisites

 * [Leiningen](http://leiningen.org) >= 2.7.1

### Building the project

    lein uberjar

### Running

    java -jar target/pipeline-demo-0.1.0-standalone.jar
