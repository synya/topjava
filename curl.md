###curl commands to test rest API

#####get meal with id 100002:

* curl -X GET http://localhost:8080/topjava/rest/meals/100002

#####get all user meals:

* curl -X GET http://localhost:8080/topjava/rest/meals

#####get all user meals between date/time:

* curl -X GET "http://localhost:8080/topjava/rest/meals/between?startDate=2015-05-30&endDate=2015-05-30&startTime=07:00&endTime=13:00"

#####create new meal:

* curl -d '{"dateTime": "2019-03-29T09:30:00", "description": "Завтрак", "calories": 1500}' -H "Content-Type: application/json" -X POST http://localhost:8080/topjava/rest/meals

#####update meal with id 100002:

* curl -d '{"id": 100002, "dateTime": "2015-05-30T14:00:00", "description": "Обед", "calories": 1500 }' -H "Content-Type: application/json" -X PUT http://localhost:8080/topjava/rest/meals/100002

#####delete meal with id 100002:

* curl -X DELETE http://localhost:8080/topjava/rest/meals/100002
