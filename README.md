# My Web Server
## FilmFinder HTTP Server
FilmFinder HTTP Server is a simple Java-based HTTP server that allows users to search for movie details using the OMDb API.


## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

To run the FilmFinder HTTP Server, you need to have the following software installed:

- Java Development Kit (JDK) version 8 or higher

### Installing

Follow these steps to set up the development environment:

1. Clone the repository to your local machine:

    ```
    git clone https://github.com/JaiderArleyGonzalez/MyWebServer.git
    ```
2. Navigate to the project directory

3. Now you have to run the file HttpServer.java with your preferred IDE.

![](/img/Ejecucion.png)

### Running the HTTP Server

The server will start running on port 35000 by default.


You can test the server by opening a web browser and navigating to http://localhost:35000.

![](/img/Server.png)


Here you can enter the title, the type you are looking for (movie, series, or episode), year and plot extension. The only required field is the title. Once you fill in the information, you click on Submit.

![](/img/Busqueda.png)

### Testing GET and POST services using POJOs with IoC.

You can use Postman to verify the functionality of the GET request.

You just need to make the GET request to http://localhost:35000/movie/detail?title=movie.

![](/img/get.png)

You can also test the POST method by making the request to this address: http://localhost:35000/movie/favorite?title=movie

![](/img/post.png)

MovieController is responsible for managing incoming requests and directing them to the corresponding business logic using annotations.

### Unit tests


The execution of the unit tests is presented.

![](/img/test.png)

### Built With
- Java - Programming language
- Maven - Dependency Management
### Author
- Jaider Gonzalez
### Acknowledgments
- OMDb API for providing movie data.