# PickAppBook

Frontend Android Jetpack Compose app for **The Playbook**, a service for posting and looking up creative user-generated pickup lines.

The concept for this project was a shared idea between [manuel-flamminio](https://github.com/manuel-flamminio/) and me. Check out his implementation of the backend [here](https://github.com/manuel-flamminio/playbook).  
The complete list of **API endpoints** is documented and available via [Swagger](https://manuel-flamminio.github.io/playbook-doc/).


https://github.com/user-attachments/assets/1ebc9c13-5ab7-4154-b4b8-694944b6d156



### Tech stack
* **Kotlin Coroutines** and **Flows** for asynchronous operations
* **Kotlinx Serialization** for JSON parsing and serialization
* **Ktor** for network requests
* **Jetpack Compose** for the UI
* **Room** for caching the results
* **DataStore** for storing user preferences
* **Navigation 2**
* **Coil** for images

## Project description
The app allows users to search, post, mark as favorite, and react to pickup lines.

The project follows the MVVM architecture and simulates a multi-module structure by organizing code into Kotlin packages.  
Common functionalities are grouped under the `com.munity.pickappbook.core` package while feature-specific components reside in the `com.munity.pickappbook.feature` package (which depend on the packages located in `com.munity.pickappbook.core`).  
For example, the Home screen is located in `com.munity.pickappbook.feature.home`.

This structure keeps the codebase clean, organized, and easy to navigate even as the project grows. 
 
**Ktor** is used for network requests in combination with the **Kotlinx Serialization** library for JSON parsing.  
A custom authentication provider (`JwtAuthProvider`) was added to make the `bearerAuthProvider` applicable to the JWT realm as well.

## Project status

The following tasks are planned or currently in progress:
* implement **unit** and **instrumented tests**;
* evaluate integrating the **Paging library** to optimize data loading and performance, compared to the current simple caching mechanism (which stores fetched pickup lines in the Room database and replaces them on each refresh);
* revamp the Search screen to improve user experience;
* collect values from a Flow in a **lifecycle-aware manner** to prevent memory leaks;
* determine which states should be retained across **configuration changes** to preserve user experience.

The project is still under development. To follow the progress of the app, check out the **GitHub Project** section of the repository.


