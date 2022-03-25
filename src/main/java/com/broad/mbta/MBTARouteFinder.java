package com.broad.mbta;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;

/**
 *
 */
public class MBTARouteFinder {
    public static void main( String[] args ) throws IOException, InterruptedException, URISyntaxException {
        TransitMap map = TransitMap.buildNewTransitMap();
        map.getRoutes().forEach(route -> System.out.println(route));
        map.getRoutes().stream()
            .max(Comparator.comparingInt(r -> r.getStopCount()))
            .ifPresent(route -> System.out.println(route.getName() + " has the most stops with " + route.getStopCount()));
        map.getRoutes().stream()
            .min(Comparator.comparingInt(r -> r.getStopCount()))
            .ifPresent(route -> System.out.println(route.getName() + " has the least stops with " + route.getStopCount()));
        map.getRoutes().stream()
            .flatMap(r -> r.getStops().values().stream())
            .filter(Stop::hasConnection)
            .distinct()
            .forEach(s -> System.out.println(s.getName() + " connects " + String.join(", ", s.getConnectingRouteNames())));

    }
}
