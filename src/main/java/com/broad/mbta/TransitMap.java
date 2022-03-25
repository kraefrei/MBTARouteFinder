package com.broad.mbta;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransitMap {
  private Set<Route> _routes = new HashSet<>();

  public TransitMap(Set<Route> routes) {
    _routes = routes;
  }

  public static TransitMap buildNewTransitMap() throws URISyntaxException, IOException, InterruptedException {
    ObjectMapper mapper = new ObjectMapper();
    URI uri_routes = new URI("https://api-v3.mbta.com/routes?filter[type]=0,1");
    HttpClient client = HttpClient.newBuilder().build();
    HttpRequest getRoutes = HttpRequest.newBuilder(uri_routes)
        .GET()
        .build();
    HttpResponse<String> respRoutes = client.send(getRoutes, HttpResponse.BodyHandlers.ofString());
    JsonNode json = mapper.readTree(respRoutes.body());
    Iterator<JsonNode> routeJsonIt = json.get("data").iterator();
    Set<Route> routes = new HashSet<>();
    while (routeJsonIt.hasNext()) {
        JsonNode routeData = routeJsonIt.next();
        String routeName = routeData.get("attributes").get("long_name").asText();
        String routeId = routeData.get("id").asText();
        URI uri_stops = new URI("https://api-v3.mbta.com/stops?include=connecting_stops,route&filter[route]=" + routeId);
        HttpRequest getStops = HttpRequest.newBuilder(uri_stops)
            .GET()
            .build();
        HttpResponse<String> respStops = client.send(getStops, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonStops = mapper.readTree(respStops.body());
        Iterator<JsonNode> stopsJsonIt = jsonStops.get("data").iterator();
        List<Stop> stops = new ArrayList<>();
        while (stopsJsonIt.hasNext()) {
            JsonNode stopData = stopsJsonIt.next();
            String id = stopData.get("id").asText();
            String name = stopData.get("attributes").get("name").asText();
            Set<Route> connectingRoutes = routes.stream()
                .filter(r -> r.containsStop(id))
                .collect(Collectors.toSet());
            if (!connectingRoutes.isEmpty()) {
                Stop connectingStop = connectingRoutes.stream().findAny().get().getStops().get(id);
                connectingStop.appendConnectingRoutes(connectingRoutes);
                stops.add(connectingStop);
            } else {
                stops.add(new Stop(id, name));
            }
        }
        Route route = new Route(routeName, stops);
        routes.add(route);
        stops.forEach(s -> s.appendConnectingRoutes(Arrays.asList(route)));
    }
    return new TransitMap(routes);
  }

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
      return findConnectingRouteToEnd(startingRoutes, endingRoutes, new ArrayList<>());
    }
  }

  public List<Route> findConnectingRouteToEnd(Set<Route> startingRoutes, Set<Route> endingRoutes, List<Route> initialPath) {
    for (Route startRoute : startingRoutes) {
      List<Route> currentPath = new ArrayList<>(initialPath);
      currentPath.add(startRoute);
      Set<Route> nextRoutes = startRoute.findConnectingStopsExcluding(startRoute).values().stream()
        .flatMap(Collection::stream).collect(Collectors.toSet());
      if (nextRoutes.stream().anyMatch(endingRoutes::contains)) {
        currentPath.add(nextRoutes.stream().filter(endingRoutes::contains).collect(Collectors.toList()).get(0));
        return currentPath;
      } else {
        return findConnectingRouteToEnd(nextRoutes, endingRoutes, currentPath);
      }
    }
    return initialPath;
  }

  private Set<Route> findRoutesContainingStop(String start) {
    return _routes.stream()
      .filter(r -> r.containsStopName(start))
      .collect(Collectors.toSet());
  }

  public void printRoutes() {
    _routes.forEach(route -> System.out.println(route));
  }

  public void printLargestRoute() {
    _routes.stream()
        .max(Comparator.comparingInt(r -> r.getStopCount()))
        .ifPresent(route -> System.out.println(route.getName() + " has the most stops with " + route.getStopCount()));
  }

  public void printSmallestRoute() {
    _routes.stream()
        .min(Comparator.comparingInt(r -> r.getStopCount()))
        .ifPresent(route -> System.out.println(route.getName() + " has the least stops with " + route.getStopCount()));
  }

  public void printConnectingStops() {
    _routes.stream()
        .flatMap(r -> r.getStops().values().stream())
        .filter(Stop::hasConnection)
        .distinct()
        .forEach(s -> System.out.println(s.getName() + " connects " + String.join(", ", s.getConnectingRouteNames())));
  }

  public Set<Route> getRoutes() {
    return _routes;
  }
}
