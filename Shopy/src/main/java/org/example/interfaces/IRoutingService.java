package org.example.interfaces;

import org.example.model.Point;

import java.util.List;

public interface IRoutingService {
    List<Point> calculateOptimalRoute(List<Point> pickupLocations);
}
