package com.broad.mbta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Route {
  private String _longName;
  private Map<String, Stop> _stops;

  /**
   * Constructor for a Route
   * @param longName the name of this route as a string
   * @param stops the list of stops that this route contains
   */
  public Route(String longName, List<Stop> stops) {
    _longName = longName;
    _stops = stops.stream()
      .collect(Collectors.toMap(Stop::getId, Function.identity()));
  }

  /**
   * Getter for the name of this route
   * @return the long string corresponding to the name of this route
   */
  public String getName() {
    return _longName;
  }

  /**
   * Getter for the Map of Stop ID to stops within this route
   * @return a map of stop ID to Stop
   */
  public Map<String, Stop> getStops() {
    return _stops;
  }

  /**
   * Utility for checking if this route contains a stop by ID
   * @param id the ID of the stop to check for
   * @return true if this Route contains a stp by the given ID and false if not
   */
  public boolean containsStop(String id) {
    return _stops.containsKey(id);
  }

  /**
   * Utility for checking if this route contains a stop with the given name
   * @param name the name of the stop to look for
   * @return true if this route contains a stop by that name and false otherwise
   */
  public boolean containsStopName(String name) {
    return _stops.values().stream()
      .map(Stop::getName)
      .anyMatch(name::equals);
  }

  /**
   * Utility for counting the number of stops this route contains
   * @return an int of the number of stops
   */
  public int getStopCount() {
    return _stops.size();
  }

  /**
   * Utility for finding the stops that this route contains that connect other routes
   * @return a map of stop name to list of connecting routes
   */
  public Map<String, Collection<Route>> findConnectingStops() {
    return _stops.values().stream()
      .filter(Stop::hasConnection)
      .collect(Collectors.toMap(Stop::getName, s -> s.getConnectingRoutes()));
  }

  /**
   * Utility for finding the stops that this route contains that connect other routes,
   * except for the given routes. This should return a similar map to {@link Route#findConnectingStops}
   * however each list of connecting routes will not contain the given routes
   * @param routes the collection of routes to exclude
   * @return a map of stop name to a list of connecting routes
   */
  public Map<String, Collection<Route>> findConnectingStopsExcluding(Collection<Route> routes) {
    return _stops.values().stream()
      .filter(Stop::hasConnection)
      .collect(Collectors.toMap(Stop::getName, s -> s.getConnectingRoutesExcluding(routes)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder()
      .append(_longName);
    return builder.toString();
  }
}
