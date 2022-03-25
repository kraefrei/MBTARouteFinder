package com.broad.mbta;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MBTA Route Finder
 * This program hits the MBTA API <a href="https://api-v3.mbta.com/docs/swagger/index.html"</a>
 * and finds information about the set of light and heavy rail routes.
 * See the README for information on the arguments
 */
public class MBTARouteFinder {
    public static void main( String[] args ) throws IOException, InterruptedException, URISyntaxException {
        if (args.length > 0) {
            try {
                int firstArg = Integer.parseInt(args[0]);

                TransitMap map = new TransitMapBuilder().buildNewTransitMap();
                if (firstArg == 1) { // Question 1: List all the light and heavy rail routes
                    map.printRoutes();
                } else if (firstArg == 2) { // Question 2: print the largest and smallest number of stops and the route they belong to
                    map.printLargestRoute();
                    map.printSmallestRoute();
                    map.printConnectingStops();
                } else if (firstArg == 3) { // Question 3: Route finding
                    String start = args[1];
                    String end = args[2];
                    List<Route> finalRoute = map.findPathBetweenStops(start, end);
                    System.out.println("Get between " + start + " and " + end + " via "
                        + String.join(", ", finalRoute.stream().map(Route::getName).collect(Collectors.toList())));

                }
                System.exit(0);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        } else {
            System.out.println("No arguments given, please give a number 1 to get the list of possible lines, "
                + " 2 to calculate some stats about the lines, or 3 followed by two stop names to find a route..");
            System.exit(1);
        }
    }
}