# Instant System Demo for Alexis Hellouin Interview

## Project design

For the given assignment, I decided to use a mapper stored in a mongo DB collection.

The choice of the database to mongoDB was driven by a will to have more freedom in the architecture of the data,
since I wanted to create a mapper that will contain sub-object, which would be more complex using a SQL database (I 
would have to create 4 tables).
In the early draft, I wanted to store the mapper as a file and link it to a SQL database. But finally I went with a 
MongoDB collection containing all the necessary data. In the collection a unique index is set of the city field of the 
ParkingManager entity to avoid duplicates on the same city.

During the development, I decided that the city will be central to the architecture (hence the uniqueness on the ParkingManager entity)
Also for most log and exception thrown, the city is used to help keep track of the issue. 

### Architecture

For the architecture, I went as follows : 
- Create a Parking entity that will change the least, and will be used by the front-end, mobile and client., this object 
is not stored in the database
- Create a ParkingManager entity that will contain the list of RequestData to retrieve the data for a specific city and an 
associated mapper to convert the response data into the Parking entity describe earlier. This entity is the only stored
in the database
- Create an entity RequestData that will contain the URL to reach the data for a city, a http method and a list of 
parameters for the URL. Also, a ISMapper (IS stand for Instant System) that will help convert the response to the Parking entity
- Create an entity ISMapper that will explain if the response is a Json list or object (For this demo only the json 
object is taken in account, the json list is not developed). Also, the path to the list of record, in the early draft, 
if this value was null it meant that the data would have to be extracted from an object and not a list (for instance to
retrieve only one parking), but the case never presented. The idea was kept in case this situation might be encountered in the future
- Create an entity ISFields that will map the response to the Parking entity. Each variable of the ISFields is the same as
the parking entity (except position that is split in longitude and latitude in ISFields). The value for each variable
is the path to the data in the response, for example, in the object below, to retrieve the id of a parking, we would 
put in the ISField id variable "parking.field.id" and the code will extract the value 1234 and store it in the id variable of 
the Parking entity
```json
{
  "parking" : {
    "field" : {
      "id": 1234
    }
  }
}
```
This kind of architecture in SQL could be done using a table for each entity and a relationship between each of them : 
- 1 to n between ParkingManager and RequestData
- 1 to 1 between RequestData and ISMapper
- 1 to 1 between ISMapper and ISFields

### Methods
Finally, a controller and a service is created for both the ParkingManager and the Parking part. 

#### Parking 
Parking contains only two method 
- Get all Parkings (with a near function if the position of the user is given) 
- Get a specific Parking for a city with its given name

#### Parking Manager
ParkingManager contains five methods : 
- Get all parking managers
- Get a parking manager for a city
- Create a new parking manager
- Update an existing parking manager
- Delete an existing parking manager

## How to run

In order to run the project, you will need a mongoDB instance running locally on the port 27017, with a database 
InstantSystemDemo and a collection ParkingManager (empty). But it is possible to change this in the application.properties, 
except for the collection name that needs to be ParkingManager (case-sensitive)

## Issues encountered
### caching the data
The first issue was that in a real case, the number of request to a distant endpoint would explode depending on the 
number of request to our endpoint (eg. if a ParkingManager has 3 Requests to make to populate the parking, and 1000 
users call the ParkingManager, then we would have 3000 Http request sent). I think the best solution would be to cache 
the parking retrieve in a in-memory database with a time to live up to 15 minutes. First we would check the cache, and 
if it is empty call the endpoints to re-populate the cache. The 15 minutes time to live could be updated to 30 minutes
because the only information that change is the number of available places in the parking and they don't evolve that fast.

### Block accesses to the ParkingManager endpoints
The ParkingManager endpoint are to be used only by the developer and manager. For that we would need to create a User 
database associated with an LDAP database and each user would have rights allowing them to access the endpoints.
But for the parking endpoint, we allow all calls, even from user not registered (not in the DB and LDAP).
Associated with this the creation of user would be open to everyone, but to create a user with higher rights, only a
user with rights above can create it. 

For example to create a user with rights manager, the user making the request must be at least manager himself to add a new user.

### Internationalization
The Response from the Parking endpoint are not internationalized, the response message is in english only, which can be
an issue for French, Spanish or other user using the application.

### HttpCall error manager
During the development of the WebClientManager, I catch all the exception and return a unique Exception called 
RestCallException, but the issue can vary, and we would need better logging and exception response. For that it would 
be good to create a method that will extract the issue from the endpoint (Timeout, too many request, wrong parameter)
log it and return a dedicated error. 

### In memory database
Since this project is only a demo, I spent some time (3-4 hours) trying to have a mongoDB in-memory created at the start-up of 
the application. But I failed to do so, and as seen in the `how to run` part, a MongoDB with a ParkingManager collection
needs to be created beforehand.

### Test for POST, PUT, DELETE
In the test I only check the GET httpMethod for the WebClientManager. This needs to be implemented too, but was not 
since no other method was used in the process.

### Validator for ParkingManager
A good practice is to create a validator for the object you send as data in your endpoint. I postpone this part until
I decided not to implement it, due to a lack of time.

## Conclusion
The project is working the tests are all green. I Honestly think the project could have been done better, The choice of
MongoDB help me simplify my development, even if the mapper part where I would extract data took me some time to perfect
and also became a bit complex at some point. But in the end, if the given endpoint response evolve it is easy to update
the ParkingManager entry. Some detailed explanation is necessary for the user that will maintain/populate the
ParkingManager, which I regret, I think it could be simplified with more time to spend on the project.

I spent around 12 hours on this project.

I hope you will appreciate my work.
