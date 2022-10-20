# NS Call Monitor

An application that will monitor your calls in the background. It'll monitor incoming, outgoing and
missed calls and display them within the application UI.

## Root

`curl http://<SERVER_HOST>:<PORT>`

###### (200) Success Response

```json
{
  "startTime": "2022-10-20T12:27:35.483",
  "services": [
    {
      "name": "ongoing",
      "uri": "http://192.168.0.146:12345/ongoing"
    },
    {
      "name": "logs",
      "uri": "http://192.168.0.146:12345/logs"
    }
  ]
}          
```

## Logs

`curl http://<SERVER_HOST>:<PORT>/logs`

###### (200) Success Response

```json
[
  {
    "name": null,
    "number": "+49 30 351288585",
    "startTime": "2022-10-18T01:04:22.34Z",
    "endTime": "2022-10-18T01:04:29.232Z",
    "duration": 6892,
    "status": "Complete",
    "type": "Outgoing",
    "numberOfQueries": 2
  },
  {
    "name": "Jane Doe",
    "number": "+49 50 234593848",
    "startTime": null,
    "endTime": null,
    "duration": 6892,
    "status": "Ringing",
    "type": "Incoming",
    "numberOfQueries": 5
  }
]
```

###### (404) Error Response

```
No monitored calls available.
```

## Ongoing Call

`curl http://<SERVER_HOST>:<PORT>/ongoing`

###### (200) Success Response

```json
{
  "name": "Jane Doe",
  "number": "+49 50 234593848",
  "startTime": "2022-10-18T01:04:22.34Z",
  "endTime": null,
  "duration": null,
  "status": "Ongoing",
  "type": "Outgoing",
  "numberOfQueries": 5
}
```

###### (404) Error Response

```
There are no ongoing calls happening.
```