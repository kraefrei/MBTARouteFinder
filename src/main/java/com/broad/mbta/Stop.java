package com.broad.mbta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Stop {
  private String _id;
  private String _name;
  private Set<Route> _connectingRoutes = new HashSet<>();

  public Stop(String id, String name) {
    _id = id;
    _name = name;
  }

  public String getId() {
    return _id;
  }

  public String getName() {
    return _name;
  }

  public Set<Route> getConnectingRoutes() {
    return new HashSet<>(_connectingRoutes);
  }

  public Set<String> getConnectingRouteNames() {
    return _connectingRoutes.stream()
      .map(Route::toString)
      .collect(Collectors.toSet());
  }

  public Set<Route> getConnectingRoutesExcluding(Route route) {
    Set<Route> connectingRoutes = new HashSet<>(_connectingRoutes);
    connectingRoutes.remove(route);
    return connectingRoutes;
  }

  public Set<Route> getConnectingRoutesExcluding(Collection<Route> routes) {
    Set<Route> connectingRoutes = new HashSet<>(_connectingRoutes);
    connectingRoutes.removeAll(routes);
    return connectingRoutes;
  }

  public boolean hasConnection() {
    return _connectingRoutes.size() > 1;
  }

  public void appendConnectingRoutes(Collection<Route> routes) {
    _connectingRoutes.addAll(routes);
  }

}
