package com.broad.mbta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Unit tests for MBTA Route Finder, verifying functionality for retrieving data from the MBTA
 * as well as building and using a TransitMap object from the collected data
 */
public class MBTARouteFinderTest
{
    /**
     * Test fetching the routes from the MBTA API
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testFetchRoutes() throws URISyntaxException, IOException, InterruptedException {
        TransitMapBuilder builder = new TransitMapBuilder();
        JsonNode routeJson = builder.fetchRoutes();
        assertEquals(8, routeJson.get("data").size());
    }

    /**
     * Test fetching the stops from the MBTA API for the Red Line,
     * and making sure the right number of stops are retrieved
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testFetchStops() throws URISyntaxException, IOException, InterruptedException {
        TransitMapBuilder builder = new TransitMapBuilder();
        JsonNode routeJson = builder.fetchStops("Red");
        assertEquals(22, routeJson.get("data").size());
    }

    /**
     * Test that the connecting stops are build correctly from the map
     * Verifying Park Street and Downtown Crossing as examples
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testTransitMapForConnections() throws URISyntaxException, IOException, InterruptedException {
        TransitMap map = new TransitMapBuilder().buildNewTransitMap();
        Set<Stop> connections = map.findConnectingStops();
        // Confirm there are 12 connecting stops total in the list
        assertEquals(12, connections.size());
        // Check for Park Street
        Optional<Stop> parkStreet = connections.stream().filter(s -> s.getName().equals("Park Street")).findFirst();
        assertTrue(parkStreet.isPresent());
        assertEquals(5, parkStreet.get().getConnectingRouteNames().size());
        // Check for Downtown Crossing
        Optional<Stop> downtownXing = connections.stream().filter(s -> s.getName().equals("Downtown Crossing")).findFirst();
        assertTrue(downtownXing.isPresent());
        assertEquals(2, downtownXing.get().getConnectingRouteNames().size());
    }

    /**
     * Test that the route find can find a route with no connections
     * @throws InterruptedException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDavisToKenallRouteFind() throws URISyntaxException, IOException, InterruptedException {
        TransitMap map = new TransitMapBuilder().buildNewTransitMap();
        List<Route> finalPath = map.findPathBetweenStops("Davis", "Kendall/MIT");
        assertEquals(1, finalPath.size());
        assertEquals("Red Line", finalPath.get(0).getName());
    }

    /**
     * Test that the route find can find a set of routes through connections
     * @throws InterruptedException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAshmontToArlingtonRouteFind() throws URISyntaxException, IOException, InterruptedException {
        TransitMap map = new TransitMapBuilder().buildNewTransitMap();
        List<Route> finalPath = map.findPathBetweenStops("Ashmont", "Arlington");
        List<String> finalPathNames = finalPath.stream().map(Route::getName).collect(Collectors.toList());
        int pathSize = finalPath.size();
        assertTrue(finalPath.size() == 2 || pathSize  == 3);
        assertTrue(finalPathNames.contains("Red Line"));
        assertTrue(finalPathNames.contains("Green Line B") || finalPathNames.contains("Green Line C") || finalPathNames.contains("Green Line D"));
        if (pathSize == 3) {
            assertTrue(finalPathNames.contains("Mattapan Trolley"));
        }
    }

    /**
     * Test that when finding a route from a stop with a connection,
     * the route found doesn't include unnecessary transfers
     * @throws InterruptedException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testParkStreetToAlewifeRouteFind() throws URISyntaxException, IOException, InterruptedException {
        TransitMap map = new TransitMapBuilder().buildNewTransitMap();
        List<Route> finalPath = map.findPathBetweenStops("Park Street", "Alewife");
        assertEquals(1, finalPath.size());
        assertEquals("Red Line", finalPath.get(0).getName());
    }
}
