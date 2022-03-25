package com.broad.mbta;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.NameNotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransitMapBuilder {
  private ObjectMapper _mapper = new ObjectMapper();
  public static final String _routesURI = "https://api-v3.mbta.com/routes?filter[type]=0,1&api_key=fd0b3c6f31cf449f9ea2f3b2f8cf97e6";
  public static final String _stopsURI = "https://api-v3.mbta.com/stops?api_key=fd0b3c6f31cf449f9ea2f3b2f8cf97e6&filter[route]=";
  private HttpClient _client = HttpClient.newBuilder().build();

  /**
   * Builder method for creating a new TransitMap from data retrieved from the MBTA
   * @return a new TransitMap object containing complete routes and stops
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   * @throws NameNotFoundException
   */
  public TransitMap buildNewTransitMap() throws URISyntaxException, IOException, InterruptedException {
    JsonNode routesJson = fetchRoutes();
    Iterator<JsonNode> routeJsonIt = routesJson.get("data").iterator();
    Set<Route> routes = new HashSet<>();
    // Iterate through all the found routes
    while (routeJsonIt.hasNext()) {
        JsonNode routeData = routeJsonIt.next();
        String routeName = routeData.get("attributes").get("long_name").asText();
        String routeId = routeData.get("id").asText();
        // Iterate through the stops found for this route and collect the stop objects
        List<Stop> stops = buildStopsForRoute(routeId, routes);
        Route route = new Route(routeName, stops);
        routes.add(route);
        // For completeness, add this route object to the connecting stop list for all stops
        stops.forEach(s -> s.appendConnectingRoutes(route));
    }
    return new TransitMap(routes);
  }

  /**
   * Helper build method for building the list of stops from MBTA data.
   *
   * This method specifically creates new stops only when there isn't an
   * existing stop with the same id already present in another route. In
   * the cases the stop already exists, that reference is used allowing the
   * stored routes to be updated with all the connecting routes
   * @param routeId the route ID to fetch the stops for
   * @param existingRoutes the existing routes to search for pre-existing stops
   * @return a list of stops of the route with the given ID
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  protected List<Stop> buildStopsForRoute(String routeId, Collection<Route> existingRoutes) throws URISyntaxException, IOException, InterruptedException {
    JsonNode jsonStops = fetchStops(routeId);
    Iterator<JsonNode> stopsJsonIt = jsonStops.get("data").iterator();
    List<Stop> stops = new ArrayList<>();
    while (stopsJsonIt.hasNext()) {
      JsonNode stopData = stopsJsonIt.next();
      // Pull identifying information from the response
      String id = stopData.get("id").asText();
      String name = stopData.get("attributes").get("name").asText();
      // Check if any existing routes contain this stop
      Set<Route> connectingRoutes = existingRoutes.stream()
          .filter(r -> r.containsStop(id))
          .collect(Collectors.toSet());
      // If there are any connecting routes
      if (!connectingRoutes.isEmpty()) {
          // Find the existing stop reference
          Stop connectingStop = connectingRoutes.stream().findAny().get().getStops().get(id);
          // And add the reference to the list of stops
          stops.add(connectingStop);
      } else {
          // Otherwise, build a fresh stop object
          stops.add(new Stop(id, name));
      }
    }
    return stops;
  }

  /**
   * Utility for building the web request to get routes from the MBTA API
   * @return a JsonNode containing the response body
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   * @throws NameNotFoundException
   */
  protected JsonNode fetchRoutes() throws URISyntaxException, IOException, InterruptedException {
    URI uri_routes = new URI(_routesURI);
    HttpRequest getRoutes = HttpRequest.newBuilder(uri_routes).GET().build();
    HttpResponse<String> respRoutes = _client.send(getRoutes, HttpResponse.BodyHandlers.ofString());
    if (respRoutes.statusCode() != 200) {
      System.out.println("ERROR: Could not retrieve routes from server. Error Code: " + respRoutes.statusCode());
    }
    return _mapper.readTree(respRoutes.body());
  }

  /**
   * Utility for building the web request to get stops from the MBTA API
   * @param routeId the id as a string of the route to find stops for
   * @return a JsonNode containing the response body
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  protected JsonNode fetchStops(String routeId) throws URISyntaxException, IOException, InterruptedException {
    URI uri_stops = new URI( _stopsURI + routeId);
    HttpRequest getStops = HttpRequest.newBuilder(uri_stops).GET().build();
    HttpResponse<String> respStops = _client.send(getStops, HttpResponse.BodyHandlers.ofString());
    if (respStops.statusCode() != 200) {
      System.out.println("ERROR: Could not retrieve stops from server. Error Code: " + respStops.statusCode());
    }
    return _mapper.readTree(respStops.body());
  }
}
