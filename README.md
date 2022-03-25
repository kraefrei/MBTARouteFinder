# MBTA Route Finder

A simple program for retrieving route information from the MBTA API

[MBTA Swagger Documentation can be found here](https://api-v3.mbta.com/docs/swagger/index.html)

## How to Build

To build the mvn project use the command
`mvn clean install`

## How to Use

**MBTARouteFinder** is the main executable and takes one required argument of an integer between 1 and 3

1. Gives the list of names of the MBTA light and heavy rail routes

    `mvn exec:java -Dexec.mainClass=com.broad.mbta.MBTARouteFinder -Dexec.args="1"`

2. Gives some statistics about the routes, including the route with the largest number of stops, the fewest, and the set of stops that connect routes

    `mvn exec:java -Dexec.mainClass=com.broad.mbta.MBTARouteFinder -Dexec.args="2"`

3. Calculate the set of routes to travel to go between 2 different stops. This requires two additional arguments of the starting stop name and the ending stop name.

    `mvn exec:java -Dexec.mainClass=com.broad.mbta.MBTARouteFinder -Dexec.args="3 Copley Wonderland"`

## How it Works

The **TransitMap** is the primary class that is responsible for calculating how two stop connect, and calculating other metrics about the routes it contains. In addition to this, there is a **Route** class for representing a route on the MBTA, and a **Stop** class for representing a stop on a route.

A **TransitMap** contains a collection of **Route**s which in turn contain a collection of **Stop**s. Each **Stop** also has references to the **Route**s they connect, allowing for the search of routes that connect two given stops.

There is also a **TransitMapBuilder** utility that is responsible for reaching the MBTA API and populating these structures for a final data structure to work with.