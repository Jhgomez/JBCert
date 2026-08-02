This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM), Server. Backend is built with Ktor and it integrates with NASA's Astronomy Picture of the Day API(aka, APOD)

The Apod Service errors have predictable behavior that helps users react effectively to fix invalid interactions by returning informative messages, this is accomplishes by evaluating input early, log clearly, and fail gracefully, in code this means these parameters are passed down to the services from the routes(services entry point, aka endpoints), the functions in the services processing the input have try-catch blocks in places where exceptions could occur, in the cathc block original exception is caught, then a new exception that includes an informative message on the reason for failure is thrown from the catch block where it bubbles up to the route which also has a try-catch, from there we could catch different types of exceptions, from that catch block we respond to the user with a proper http code and usually passing the received exception's message, and there is also one more component that could help us, which is "Status Pages" plugin/extension which can serve as a central component where some specific type of exceptions or responses with determined http codes are caught, this could be more important for handling any uncaught exception, this is a consistent pattern throughout all services and routes/endpoints, these validations are small, testable pieces of code which can be covered with unit test or property-based test, unit test validate only a single scenario like the output of a function for a given input, poperty-based tests on the other hand generate a wide variety of values to test your code for example we could evaluate that x + y == y + x but with a lot of different values  

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
