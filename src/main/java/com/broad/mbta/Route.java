package com.broad.mbta;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Route {
  private String _longName;
  private Map<String, Stop> _stops;

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

  public Map<String, Stop> getStops() {
    return _stops;
  }

  public boolean containsStop(String id) {
    return _stops.keySet().contains(id);
  }

  public boolean containsStopName(String name) {
    return _stops.values().stream()
      .map(Stop::getName)
      .anyMatch(name::equals);
  }

  public int getStopCount() {
    return _stops.size();
  }

  public Map<String, Collection<Route>> findConnectingStops() {
    return _stops.values().stream()
      .filter(Stop::hasConnection)
      .collect(Collectors.toMap(Stop::getName, s -> s.getConnectingRoutes()));
  }

  public Map<String, Collection<Route>> findConnectingStopsExcluding(Collection<Route> routes) {
    return _stops.values().stream()
      .filter(Stop::hasConnection)
      .collect(Collectors.toMap(Stop::getName, s -> s.getConnectingRoutesExcluding(routes)));
  }

  public Map<String, Collection<Route>> findConnectingStopsExcluding(Route route) {
    return _stops.values().stream()
      .filter(Stop::hasConnection)
      .collect(Collectors.toMap(Stop::getName, s -> s.getConnectingRoutesExcluding(route)));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder()
      .append(_longName);
    //  .append(": ");
    // _stops.values().forEach(stop -> builder.append(stop.getName()).append(", "));
    return builder.toString();
  }
}
