package com.broad.mbta;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class MBTARouteFinderTest
{
    /**
     * Rigorous Test :-)
     * @throws InterruptedException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testDavisToKenallRouteFind() throws URISyntaxException, IOException, InterruptedException {
        TransitMap map = TransitMap.buildNewTransitMap();
        List<Route> finalPath = map.findPathBetweenStops("Davis", "Kendall/MIT");
        assertEquals(1, finalPath.size());
        assertEquals("Red Line", finalPath.get(0).getName());
    }

    /**
     * Rigorous Test :-)
     * @throws InterruptedException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test
    public void testAshmontToArlingtonRouteFind() throws URISyntaxException, IOException, InterruptedException {
        TransitMap map = TransitMap.buildNewTransitMap();
        List<Route> finalPath = map.findPathBetweenStops("Ashmont", "Arlington");
        assertEquals(2, finalPath.size());
        assertEquals("Red Line", finalPath.get(0).getName());
        assertEquals("Green Line B", finalPath.get(1).getName());
    }
}
