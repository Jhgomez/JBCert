This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM), Server. Backend is built with Ktor and it integrates with NASA's Astronomy Picture of the Day API(aka, APOD)

# Backend

The Apod Service errors have predictable behavior that helps users react effectively to fix invalid interactions by returning informative messages, this is accomplishes by evaluating input early, log clearly, and fail gracefully, in code this means these parameters are passed down to the services from the routes(services entry point, aka endpoints), the functions in the services processing the input have try-catch blocks in places where exceptions could occur, in the cathc block original exception is caught, then a new exception that includes an informative message on the reason for failure is thrown from the catch block where it bubbles up to the route which also has a try-catch, from there we could catch different types of exceptions, from that catch block we respond to the user with a proper http code and usually passing the received exception's message, and there is also one more component that could help us, which is "Status Pages" plugin/extension which can serve as a central component where some specific type of exceptions or responses with determined http codes are caught, this could be more important for handling any uncaught exception, this is a consistent pattern throughout all services and routes/endpoints, these validations are small, testable pieces of code which can be covered with unit test or property-based test, unit test validate only a single scenario like the output of a function for a given input, poperty-based tests on the other hand generate a wide variety of values to test your code for example we could evaluate that x + y == y + x but with a lot of different values
.
The server creates a local portable database using Kotlin's Exposed SQL library, a Sqlite database is created and managed by a Sqlite JDBC driver using the library "Sqlite-jdbc" which bundles sqlite and provides a driver, the driver is an implementation of JDBC tha can interact with the underneath sqlite library. if necessary we can connect to a remote db later. The DB is used to provide caching, our services are smart enough to get info from our db or from NASA's servers, the service doesn't care where the data comes from, this is possible due to the caching mechanism, so our service is more resilient to external rate limits, or service outages. Our service respects NASA's rate limits by delegating validations to the rest client which checks hourly and daily request counters against well defined limits

We have an autmated background job that helps keeping cache up to date. these jobs run on a schedule, they are in charge of fetching new data, and cleaning old records, this enforces our cache policies on our data, this logic lives in `ApodService` which is part of a background job configured when server starts up, the logic the job runs lives in the `` function, but this is not a full-blown task scheduler, this is only kotlin code inside server's built in lifecycle hooks, so if you need a more advance behaviour like kron expressions, retries, and distributed execution, you can swap this out for something like "Quartz" or a cloud task runner, however this approach works great for medium and small apps

OpenApi is a lenguage agnostic format to describe HTTP APIs, you could also use Ktor's native
compile-time OpenAPI generator which generates a yaml describing your API endpoints automatically,
there is also community library you could use instead, either way, you should get the yaml generated,
which can then be used with tools like Imsomnia(alternative to Postman), or swagger or scalar(more modern UI)
to help you test your APIs, pretty much all of them can use the description generated yaml file to allow you
to test your APIs happy path or simulate edge cases, making sure your API works as expected as well 
as errors being communicated/formatted correctly. You can alos write integration test using Ktor's 
test application engine which lets you simulate http requests inside a test environment, check response 
status code, and make assertions on the response's body. Clear routes, consistent error
and easy testing tools contribute to developers experience. As of today ktor seems to support OpenApi 
and Swagger with some official dependencies, however they don't support Scalar yet, if you want to use
it then you need to create an end endpoint that exposes the generated yaml file

Useful Gradle commands, list 
all tasks available `./gradlew tasks --all` this will list all task in all modules, you can get them 
by module(use the same module names registered in the project's settings) 
`./gradlew :androidApp:tasks --all`. You can check what task where executed to execute a task,
for example in android `installDebug` and `installRelease`, NOTE: the latter is only visible from 
gradle tasks if you have created a signing config for release explicitly or assign the debug one to 
the release build type, debug signing config is automatically created
`signingConfig = signingConfigs.getByName("release")`, those are just examples,
once you have those names you can check the Directed Acyclic Graph(DAG) created by gradle which is 
a graph of task that your project has, but you'd want to check the path the task you're debugging is
taking, you do that with `/gradlew <task_name> --dry-run`, it prints the full ordered task graph 
without executing anything. `./gradlew build --scan`, uploads to a hosted report (free tier) with a 
browsable timeline, per-task duration, up-to-date/cached status, and dependency relationships. When 
you're trying to understand why something ran or why a build is slow, this is the most informative 
option by a wide margin. You can use scans reports for sharing console logs with peers, viewing test
execution results(if your task ran test, `build` runs tests), analyze build performance, it helps you
visualize build dependencies.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :app:androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :app:desktopApp:hotRun --auto`
  - Standard run: `./gradlew :app:desktopApp:run`
- Server: `./gradlew :server:run`
- Web app:
  - Wasm target (faster, modern browsers): `./gradlew :app:webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :app:webApp:jsBrowserDevelopmentRun`
- iOS app: open the [/app/iosApp](./app/iosApp) directory in Xcode and run it from there.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :app:shared:testAndroidHostTest`
- Desktop tests: `./gradlew :app:shared:jvmTest`
- Server tests: `./gradlew :server:test`
- Web tests:
  - Wasm target: `./gradlew :app:shared:wasmJsTest`
  - JS target: `./gradlew :app:shared:jsTest`
- iOS tests: `./gradlew :app:shared:iosSimulatorArm64Test`

---
