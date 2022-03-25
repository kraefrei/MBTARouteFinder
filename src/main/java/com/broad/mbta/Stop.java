package com.broad.mbta;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A stop on a route of a transit map.
 */
public class Stop {
  private String _id;
  private String _name;
  private Set<Route> _connectingRoutes = new HashSet<>();

  /**
   * Constructor for a stop
   * @param id the unique id for this stop
   * @param name the human readable name of this stop
   */
  public Stop(String id, String name) {
    _id = id;
    _name = name;
  }

  /**
   * Getter for the ID of this stop
   * @return the string ID
   */
  public String getId() {
    return _id;
  }

  /**
   * Getter for the name of this stop
   * @return the string name
   */
  public String getName() {
    return _name;
  }

  /**
   * Getter for the routes this stop connects
   * @return the set of connecting routes
   */
  public Set<Route> getConnectingRoutes() {
    return new HashSet<>(_connectingRoutes);
  }

  /**
   * Helper for getting the set of route names, for the purpose of logging.
   * @return the set of route names
   */
  public Set<String> getConnectingRouteNames() {
    return _connectingRoutes.stream()
      .map(Route::toString)
      .collect(Collectors.toSet());
  }

  /**
   * Get the connecting routes this stop has, without the routes that are provided as arguments.
   * The given routes may or may not be amongst the set of connecting routes
   * @param routes the routes to exclude from the set of connecting routes.
   * @return a copy of the set of route that this stop connects, with the given routes missing
   */
  public Set<Route> getConnectingRoutesExcluding(Collection<Route> routes) {
    Set<Route> connectingRoutes = new HashSet<>(_connectingRoutes);
    connectingRoutes.removeAll(routes);
    return connectingRoutes;
  }

  /**
   * Utility for checking if this stop connects another route
   * @return true if the stp connects 2 or more routes, and false if the stop belongs only to one route
   */
  public boolean hasConnection() {
    return _connectingRoutes.size() > 1;
  }

  /**
   * Add a connecting route to this stop
   * @param route the route to add to this stop
   */
  public void appendConnectingRoutes(Route route) {
    _connectingRoutes.add(route);
  }

}
