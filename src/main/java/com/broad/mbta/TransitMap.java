package com.broad.mbta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A data structure for storing a collection of routes {@link Route}, computing metadata on those routes
 * and computing the routes required to travel from one stop {@link Stop} to another
 */
public class TransitMap {
  private Set<Route> _routes = new HashSet<>();

  /**
   * Constructor of a TransitMap containing routes
   * @param routes the routes this map contain
   */
  public TransitMap(Set<Route> routes) {
    _routes = routes;
  }

  /**
   * Calculate the ordered list of routes taken to travel from one stop to another
   * @param start the name of the starting stop
   * @param end the name of the ending stop
   * @return A list of routes that connect the given starting and ending stop
   */
  public List<Route> findPathBetweenStops(String start, String end) {
    Set<Route> startingRoutes = findRoutesContainingStop(start);
    Set<Route> endingRoutes = findRoutesContainingStop(end);
    if (startingRoutes.isEmpty()) {
      throw new IllegalArgumentException("Starting stop is not a valid route stop, please try another pair.");
    }
    if (endingRoutes.isEmpty()) {
      throw new IllegalArgumentException("Ending stop is not a valid route stop, please try another.");
    }
    // If the starting route and the ending route share a route, no transfers are required
    if (startingRoutes.stream().anyMatch(endingRoutes::contains)) {
      // Find the set of intersecting routes, grab the first, and return as an element of a list
      return Arrays.asList(startingRoutes.stream().filter(endingRoutes::contains).collect(Collectors.toList()).get(0));
    } else {
      // Start recursively searching for a route that connects with the ending stop
      return findConnectingRouteToEnd(startingRoutes, endingRoutes, new ArrayList<>());
    }
  }

  /**
   * Recursive method for building a path between routes,
   * by checking if the set of possible starting routes contains an
   * intersection with any of the possible ending routes
   * @param startingRoutes a set of possible starting routes that  should be tested for reaching the end
   * @param endingRoutes a set of possible ending routes. This may typically contain one route, but for
   * stop with connections this could be reached from several routes, and so are included as possible intersections
   * @param initialPath A list of routes that have been traversed so far, this list will not be modified by this method
   * @return a final list of route including the initial path, and the first intersection that is found between the starting and ending routes
   */
  public List<Route> findConnectingRouteToEnd(Set<Route> startingRoutes, Set<Route> endingRoutes, List<Route> initialPath) {
    for (Route startRoute : startingRoutes) {
      List<Route> currentPath = new ArrayList<>(initialPath);
      currentPath.add(startRoute);
      // Find the connecting routes, but exclude routes we've already traveled, since they've been already tested for the end stop
      Set<Route> nextRoutes = startRoute.findConnectingStopsExcluding(currentPath).values().stream()
        .flatMap(Collection::stream).collect(Collectors.toSet());
      // Check if there are intersecting routes with the end stop
      if (nextRoutes.stream().anyMatch(endingRoutes::contains)) {
        // Success, return the first match
        currentPath.add(nextRoutes.stream().filter(endingRoutes::contains).findFirst().get());
        return currentPath;
      } else {
        // Recurse to find another connection
        return findConnectingRouteToEnd(nextRoutes, endingRoutes, currentPath);
      }
    }
    // If no starting routes are given, there is no possible path
    return new ArrayList<>();
  }

  /**
   * Search all contained routes for the set of Routes that contain a stop with the given name
   * @param stopName the stop name to search for
   * @return the set containing all routes that have the given stop by name
   */
  private Set<Route> findRoutesContainingStop(String stopName) {
    return _routes.stream()
      .filter(r -> r.containsStopName(stopName))
      .collect(Collectors.toSet());
  }

  /**
   * Print the list of routes by name that this Transit Map contains
   */
  public void printRoutes() {
    _routes.forEach(route -> System.out.println(route));
  }

  /**
   * Print a message about the route with the largest number of stops
   */
  public void printLargestRoute() {
    _routes.stream()
        .max(Comparator.comparingInt(r -> r.getStopCount()))
        .ifPresent(route -> System.out.println(route.getName() + " has the most stops with " + route.getStopCount()));
  }

  /**
   * Print a message about the route with the smallest number of stops
   */
  public void printSmallestRoute() {
    _routes.stream()
        .min(Comparator.comparingInt(r -> r.getStopCount()))
        .ifPresent(route -> System.out.println(route.getName() + " has the least stops with " + route.getStopCount()));
  }

  /**
   * Helper for finding all the stops this TransitMap contains
   * that connects 2 or more routes
   * @return
   */
  public Set<Stop> findConnectingStops() {
    return _routes.stream()
        .flatMap(r -> r.getStops().values().stream())
        .filter(Stop::hasConnection)
        .distinct()
        .collect(Collectors.toSet());
  }

  /**
   * Print a message about the stops that have connecting routes,
   * including the routes they connect
   */
  public void printConnectingStops() {
    findConnectingStops().forEach(s -> System.out.println(s.getName() + " connects " + String.join(", ", s.getConnectingRouteNames())));
  }

  /**
   * Getter for the routes this TransitMap contains
   * @return the set of routes
   */
  public Set<Route> getRoutes() {
    return _routes;
  }
}
