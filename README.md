# point-management-practice
Users can accumulate points in various ways through shopping or participating in events. These points usually have an expiration date.

This app uses Spring Batch to accumulate scheduled points or expire points in a massive volume. 

## Build the project
```
gradlew.bat build
```

## Run the application using jar file
You have to give a name of a job to execute and today as arguments.

Three jobs are implemented in the project: expirePointJob, messageExpiredPointJob, and ExecutePointReservationJob.

- expirePointJob: set the points expired.
- messageExpiredPointJob: send a message to a user to inform them how many points will be expired.
- ExecutePointReservationJob: save the points that were scheduled.

for example,
```
java -jar point-management-practice-1.0-SNAPSHOT.jar --job.name=expirePointJob today=2022-11-03
```