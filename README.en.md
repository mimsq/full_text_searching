

This project is a full-text search application based on Spring Boot, featuring basic user interaction and HTML page display capabilities.

## Installation Instructions

1. Ensure that the Java Development Environment (JDK 8 or later) and Maven are installed.
2. Clone the project to your local machine:
   ```
   git clone https://gitee.com/mimsq/full-text-searching
   ```
3. Navigate into the project directory and build using Maven:
   ```
   cd full-text-searching
   mvn clean install
   ```
4. Start the project:
   ```
   mvn spring-boot:run
   ```
5. Once the project starts, the default access port is 8080.

## Usage

- Access the `/hello` endpoint to retrieve a greeting message; the `name` parameter is optional.
- Access the `/user` endpoint to retrieve a user object.
- Access the `/save_user` endpoint to submit user information.
- Access the `/html` endpoint to display an HTML page.
- Access the `/user/{userId}/roles/{roleId}` endpoint via path variables to retrieve information about a specific user and role.
- Access the `/javabeat/{regexp1:[a-z-]+}` endpoint to use a regular expression path variable.

## Project Structure

- `FullTextSearchingApplication.java`: Main class for the Spring Boot application.
- `BasicController.java`: Controller containing handlers for basic requests.
- `PathVariableController.java`: Controller containing handlers for path variable requests.
- `User.java`: User entity class.
- `application.yml`: Main configuration file for the application.
- `index.html`: Static resource page.

## Testing

- `FullTextSearchingApplicationTests.java`: Provides Spring Boot application context loading tests.

## Contributor Guidelines

Code contributions are welcome. Before submitting code, ensure it complies with the coding standards and includes necessary unit tests.

## License

This project is licensed under the MIT License. For details, please refer to the license file in the repository.