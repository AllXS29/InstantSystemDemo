package com.instantsystem.demo.parking.service;

import com.instantsystem.demo.parking.entity.*;
import com.instantsystem.demo.parking.exception.BuildParkingException;
import com.instantsystem.demo.parking.exception.InstantSystemMapperException;
import com.instantsystem.demo.parking.exception.ParkingNotFoundException;
import com.instantsystem.demo.util.DistanceCalculator;
import com.instantsystem.demo.util.JsonHelper;
import com.instantsystem.demo.util.WebClientManager;
import com.jayway.jsonpath.PathNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParkingService {

    private static final Logger LOG = LoggerFactory.getLogger(ParkingService.class.getName());
    public ParkingManagerService parkingManagerService;

    public ParkingService(ParkingManagerService parkingManagerService) {
        this.parkingManagerService = parkingManagerService;
    }

    /**
     * Retrieve all the {@link Parking} for a given city
     * @param city  The city to retrieve the {@link Parking} from
     * @return a {@link List} of all the {@link Parking}
     */
    public List<Parking> getParkings(String city) {
        LOG.info("Retrieve all parkings for city {}", city);
        return getParkings(city, null, null, null);
    }

    /**
     * Retrieve all {@link Parking} in a city that are in range of the given position (IF given)
     * @param city  The city to retrieve the {@link Parking} from
     * @param lat   The latitude of the position
     * @param lon   The longitude of the position
     * @param range The range around the position in KM (can be in Miles but not implemented yet)
     * @return A {@link List} of {@link Parking} that are in range of the given position if given
     */
    public List<Parking> getParkings(String city, Double lat, Double lon, Double range) {
        LOG.info("Retrieve parkings in city {}, filter by range ({} kilometers) around the user position lat : {}, lon : {}", city, range, lat, lon);
        Position userPosition = null;
        if (lat != null && lon != null) {
            userPosition = new Position(lat, lon);
        }
        // Retrieve associated parking manager
        ParkingManager parkingManager = parkingManagerService.getByCity(city);
        // Retrieve all parkings
        List<Parking> allParkings = getAndBuildAllParkings(parkingManager);
        // Filter those in range if needed
        if (userPosition != null) {
            Position finalUserPosition = userPosition;
            allParkings = allParkings.stream().filter(parking -> isInRange(parking, finalUserPosition, range)).collect(Collectors.toList());
        }
        // Return list
        return allParkings;
    }

    /**
     * Retrieve a specific parking by its name
     * @param city  The city we are looking into
     * @param name  The name of the {@link Parking} we are fetching
     * @return  The {@link Parking} object
     * @throws  ParkingNotFoundException The parking is not found will return a 404 response
     */
    public Parking getParking(String city, String name) {
        LOG.info("Retrieve parking {} in city {}", name, city);
        List<Parking> parkings = getParkings(city);
        Optional<Parking> parking = parkings.stream().filter(p -> p.getName().equals(name)).findFirst();
        if (!parking.isPresent()) {
            String message = new StringBuilder("Unable to find parking : ")
                    .append(name)
                    .append("for city ")
                    .append(city).toString();
            LOG.warn(message);
            throw new ParkingNotFoundException(message);
        }
        return parking.get();
    }

    /**
     * Retrieve all the parking and build the parking lists. We call each endpoint given in the {@link ParkingManager}
     * and call buildParkingMap method to build the list of parkings
     * @param parkingManager    The {@link ParkingManager} with the list of URLs to call and the mapper associated to
     *                          convert the response into our {@link Parking} object
     * @return  The List of {@link Parking} retrieved
     * @throws BuildParkingException if we failed to build the list from a parsing error
     */
    private List<Parking> getAndBuildAllParkings(ParkingManager parkingManager) {
        LOG.info("Retrieve and build parking list for city {} using parkingManager {}", parkingManager.getCity(), parkingManager.getId());
        Map<String, Parking> parkingMap = new HashMap<>();
        // Loop through all the endpoint dedicated to build the parking list
        for (RequestData requestData : parkingManager.getRequestsData()) {
            WebClientManager manager = new WebClientManager();
            // Call URL to retrieve the data to build
            String body = manager.makeHttpCall(requestData.getUrl(), requestData.getMethod(), parkingManager.getCity());
            try {
                //Convert String response to JSONObject
                JSONObject jsonObject = new JSONObject(body);
                parkingMap = buildParkingMap(parkingManager.getCity(), parkingMap, jsonObject, requestData.getiSMapper());
            } catch (JSONException e) {
                LOG.error(String.format("Failed to parse response from %s with city %s", requestData.getUrl(), parkingManager.getCity()), e);
                String message = String.format("Failed to retrieve parkings for city %s", parkingManager.getCity());
                throw new BuildParkingException(message);
            } catch (PathNotFoundException e) {
                LOG.error(String.format("Failed to parse response from endpoint %s with city %s", requestData.getUrl(), parkingManager.getCity()), e);
                String message = String.format("Failed to retrieve parking for city %s\n We were not able to map the data, please contact your administrator.", parkingManager.getCity());
                throw new InstantSystemMapperException(message);
            }
        }
        return new ArrayList<>(parkingMap.values());
    }

    /**
     * Build the parking map, we extract each data if they are defined in the current {@link ISMapper}
     * We populate the parkingMap given and return it, if parking are already defined they will be updated, previous
     * values will not be erased
     * @param city          The city we populate the parking list for
     * @param parkingMap    The map of parking in the city, key is the parking id (from endpoint) value is the parking object
     * @param json          The response body as {@link JSONObject} we received from the endpoint
     * @param mapper        The mapper define in {@link ParkingManager} to convert the json body values to our {@link Parking}
     * @return              The parkingMap filled/updated
     */
    private Map<String, Parking> buildParkingMap(String city, Map<String, Parking> parkingMap, JSONObject json, ISMapper mapper) {
        LOG.info("Building city {} parking list from json response body", city);
        ISFields iSFields = mapper.getiSFields();

        // Retrieve the list of fields we got from the endpoint
        JSONArray elements = json.getJSONArray(mapper.getListFieldPath());

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            Parking parking = null;
            // Parking not in map (or map empty) create a new parking entry
            if (parkingMap.isEmpty() || !parkingMap.containsKey(JsonHelper.extractStringValueFromJson(element, iSFields.getId()))) {
                parking = new Parking();
                parking.setId(JsonHelper.extractStringValueFromJson(element, iSFields.getId()));
            } else {    // Parking already exists, retrieve it to populate it
                parking = parkingMap.get(JsonHelper.extractStringValueFromJson(element, iSFields.getId()));
            }

            // Setting the city from param
            parking.setCity(city);

            // Set only the fields that are defined in the mapper ISFields
            if (iSFields.getName() != null)
                parking.setName(JsonHelper.extractStringValueFromJson(element, iSFields.getName()));
            if (iSFields.getNbPlaces() != null)
                parking.setNbPlaces(JsonHelper.extractIntegerValueFromJson(element, iSFields.getNbPlaces()));
            if (iSFields.getNbPlacesRemaining() != null)
                parking.setNbPlacesRemaining(JsonHelper.extractIntegerValueFromJson(element, iSFields.getNbPlacesRemaining()));
            if (iSFields.getDescription() != null)
                parking.setDescription(JsonHelper.extractStringValueFromJson(element, iSFields.getDescription()));
            if (iSFields.getLongitude() != null && iSFields.getLatitude() != null) {
                double latitude = JsonHelper.extractDoubleValueFromJson(element, iSFields.getLatitude());
                double longitude = JsonHelper.extractDoubleValueFromJson(element, iSFields.getLongitude());
                parking.setPosition(new Position(latitude, longitude));
            }

            // Add the parking to the map, id as key
            parkingMap.put(parking.getId(), parking);
        }
        return parkingMap;
    }

    /**
     * Check the distance between two points, if they are under a given range return true, false otherwise.
     * Check user and parking position, if not set return false
     * @param parking   The parking to check
     * @param position  The user's position
     * @param range     The maximal range acceptable between the two object
     * @return boolean true if in range false otherwise
     */
    private boolean isInRange(Parking parking, Position position, Double range) {
        LOG.info("Check if parking {} is under {} kilometers from user's position lat: {}, lon : {}", parking.getId(), range, position.getLatitude(), position.getLongitude());
        if (parking.getPosition() == null || position == null) {
            // Cannot be position was checked above
            LOG.warn("Missing position for parking {} or user, excluded from result", parking.getId());
            return false;
        } else if (parking.getPosition().getLatitude() == null || parking.getPosition().getLongitude() == null) {
            LOG.warn("Missing lat and/or lon for parking {}, excluded from result", parking.getId());
            return false;
        } else if (position.getLatitude() == null || position.getLongitude() == null) { // The position is checked before should not happen
            LOG.warn("Missing lat and/or lon for user, parking {} excluded from result ", parking.getId());
            return false;
        }
        Position parkingPosition = parking.getPosition();
        double distance = DistanceCalculator.distance(position.getLatitude(), position.getLongitude(), parkingPosition.getLatitude(), parkingPosition.getLongitude(), "K");
        return distance <= range;
    }

}
