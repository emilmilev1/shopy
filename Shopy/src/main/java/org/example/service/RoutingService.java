package org.example.service;

import org.example.interfaces.IRoutingService;
import org.example.model.Point;

import java.util.ArrayList;
import java.util.List;

public class RoutingService implements IRoutingService {
    private final Point WAREHOUSE_START_POINT = new Point(0, 0);

    @Override
    public List<Point> calculateOptimalRoute(List<Point> pickupLocations) {
        List<Point> destinationSequence = findDestinationSequence(pickupLocations);

        return buildFullPath(destinationSequence);
    }

    // Calculates the optimal route using a Nearest Neighbor heuristic
    private List<Point> findDestinationSequence(List<Point> pickupLocations) {
        if (pickupLocations == null || pickupLocations.isEmpty()) {
            return List.of(WAREHOUSE_START_POINT, WAREHOUSE_START_POINT);
        }

        List<Point> sequence = new ArrayList<>();
        List<Point> remaining = new ArrayList<>(pickupLocations);
        Point currentLocation = WAREHOUSE_START_POINT;

        sequence.add(currentLocation);

        while (!remaining.isEmpty()) {
            Point nearestLocation = null;
            int shortestDistance = Integer.MAX_VALUE;

            for (Point location : remaining) {
                int distance = calculateManhattanDistance(currentLocation, location);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    nearestLocation = location;
                }
            }

            if (nearestLocation == null) {
                throw new IllegalStateException("Routing algorithm failed: Could not determine the next location.");
            }

            currentLocation = nearestLocation;
            sequence.add(currentLocation);
            remaining.remove(currentLocation);
        }

        sequence.add(WAREHOUSE_START_POINT);
        return sequence;
    }

    private List<Point> buildFullPath(List<Point> destinationSequence) {
        List<Point> fullPath = new ArrayList<>();
        if (destinationSequence.isEmpty()) {
            return fullPath;
        }

        for (int i = 0; i < destinationSequence.size() - 1; i++) {
            Point start = destinationSequence.get(i);
            Point end = destinationSequence.get(i + 1);

            fullPath.add(start);

            // If the bot needs to move on both axes
            // it adds the intermediate corner point.
            // It moves along the X-axis first then the Y-axis.
            if (start.x() != end.x() && start.y() != end.y()) {
                Point corner = new Point(end.x(), start.y());
                fullPath.add(corner);
            }
        }

        fullPath.add(destinationSequence.get(destinationSequence.size() - 1));

        return fullPath;
    }

    private int calculateManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x() - p2.x()) + Math.abs(p1.y() - p2.y());
    }
}
