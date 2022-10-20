# NS Call Monitor

An application that will allow you to monitor incoming and outgoing calls.

<p align="center">
<img src="https://user-images.githubusercontent.com/57259490/196995443-57966090-0541-4dca-99ef-e0b40568b214.gif" height="500"/>
&nbsp; &nbsp; &nbsp; &nbsp;
<img src="https://user-images.githubusercontent.com/57259490/196995461-37d37b51-9950-463e-bdb1-924e80f9dde1.gif" height="500"/>
</p>

## Tech stack

- Minimum SDK level 23
- [Kotlin](https://kotlinlang.org/)
  based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
    + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
      for call logs.
- Jetpack
    - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes.
    - ViewModel: Manages UI-related data holder and lifecycle aware. Allows data to survive
      configuration changes such as screen rotations.
    - DataBinding: Used to update the Server Details View on the dashboard based on observations
      from the DashboardViewModel.
    - Room: A database consisting of two tables serving the Call Logs (call_log) + Query Count (
      call_log_query) shared between client and server.
    - [Hilt](https://dagger.dev/hilt/): for dependency injection.
- Architecture
    - Clean MVVM Architecture (View - DataBinding - ViewModel - UseCase - Model)
- [KTOR](https://ktor.io/) for the HTTP Server monitoring the call logs
- [Material-Components](https://github.com/material-components/material-components-android):
  Material design to help with the the dark and light theming + text styling.
- [Mockk](https://mockk.io/)
  with [Junit5-Jupiter](https://junit.org/junit5/docs/current/user-guide/) for Unit Testing.
  
## API

### Root

`curl http://<SERVER_HOST>:<PORT>`

###### # (200) Success Response

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

### Logs

`curl http://<SERVER_HOST>:<PORT>/logs`

###### # (200) Success Response

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
    "numberOfQueries": 5
  },
  {
    "name": "Jane Doe",
    "number": "+49 50 234593848",
    "startTime": null,
    "endTime": null,
    "duration": 0,
    "status": "Ringing",
    "type": "Incoming",
    "numberOfQueries": 2
  }
]
```

###### # (404) Error Response

```
No monitored calls available.
```

### Ongoing Call

`curl http://<SERVER_HOST>:<PORT>/ongoing`

###### # (200) Success Response

```json
{
  "name": "Jane Doe",
  "number": "+49 50 234593848",
  "startTime": "2022-10-18T01:04:22.34Z",
  "endTime": null,
  "duration": 0,
  "status": "Ongoing",
  "type": "Outgoing",
  "numberOfQueries": 5
}
```

###### # (404) Error Response

```
There are no ongoing calls happening.
```
