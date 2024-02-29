package edu.arep;
import edu.arep.runtime.GetMapping;
import edu.arep.runtime.PostMapping;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.nio.file.*;
public class HttpServer {
    private ServerSocket serverSocket;
    private boolean running;
    public static Map<String, Method> componentes = new HashMap<String, Method>();
    public static Map<String, Method> getMappings = new HashMap<String, Method>();
    public static Map<String, Method> postMappings = new HashMap<String, Method>();
    public HttpServer() {
        this.serverSocket = null;
        this.running = true;
    }
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, URISyntaxException {
        findAndRegisterComponentMethods("edu.arep");
        handleComponentGetMethod("/components/hello");
        findAndRegisterRequestMappingMethods("edu.arep.controller");

        //handleRequestMappingPostMethod("/movie/favorite");
        HttpServer httpServer = new HttpServer();
        httpServer.start(35000);
    }
    public void start(int port) throws URISyntaxException {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            e.printStackTrace();
            System.exit(1);
        }

        while (running) {
            handleClientRequest();
        }
    }
    /**
     * Handles the client request.
     * @throws URISyntaxException
     */
    public void handleClientRequest() throws URISyntaxException {
        try (Socket clientSocket = serverSocket.accept();
             OutputStream outputStream = clientSocket.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String inputLine;
            String uriStr = "";

            while ((inputLine = in.readLine()) != null) {
                if (inputLine.isEmpty()) {
                    break;
                }
                if (uriStr.isEmpty()) {
                    uriStr = inputLine.split(" ")[1];
                }
            }

            URI requestUri = new URI(uriStr);
            String json = null;
            if (requestUri.getPath().equals("/")) {
                requestUri = new URI("/index.html");
            }
            if (requestUri.getPath().equals("/movie/detail")) {
                String name = requestUri.getQuery().substring(6);
                json = handleRequestMappingGetMethod("/movie/detail", name);

            }
            if (requestUri.getPath().equals("/movie/favorite")) {
                String name = requestUri.getQuery().substring(6);
                json = handleRequestMappingPostMethod("/movie/favorite", name);

            }
            try {
                httpResponse(requestUri, outputStream, json);
            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = httpError();
                outputStream.write(errorResponse.getBytes());
                outputStream.flush();
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        }
    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
    }

    /**
     * Generates HTML content for the HTTP response.
     *
     * @param uriRequest the requested URI
     * @param outputStream the output stream to write the response to
     * @throws IOException if an I/O error occurs
     */
    public static void httpResponse(URI uriRequest, OutputStream outputStream, String json) throws IOException {
        Path filePath = Paths.get("src/main/resources" + uriRequest.getPath());
        byte[] fileBytes;
        if (json != null) {
            fileBytes = json.getBytes(); // Si tenemos JSON, lo convertimos a bytes
        } else {
            fileBytes = Files.readAllBytes(filePath); // Si no, leemos el archivo como antes
        }

        String contentType;
        if (uriRequest.getPath().contains("html")) {
            contentType = "text/html";
        } else if (uriRequest.getPath().contains("css")) {
            contentType = "text/css";
        } else if (uriRequest.getPath().endsWith("js")) {
            contentType = "application/javascript";
        } else {
            contentType = "text/plain";
        }

        String responseHeader = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + fileBytes.length + "\r\n" +
                "\r\n";

        outputStream.write(responseHeader.getBytes());
        outputStream.write(fileBytes);
        outputStream.flush();
    }

    private static String httpError() {
        String outputLine = "HTTP/1.1 400 Not Found\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n"
                + "<html>\n"
                + "    <head>\n"
                + "        <title>Error Not found</title>\n"
                + "        <meta charset=\"UTF-8\">\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <h1>Error</h1>\n"
                + "    </body>\n";
        return outputLine;

    }
    public static void findAndRegisterComponentMethods(String packageName) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.getFile());
                    if (directory.exists()) {
                        File[] files = directory.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.getName().endsWith(".class")) {
                                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                                    Class<?> clazz = Class.forName(className);
                                    registerComponentMethods(clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void findAndRegisterRequestMappingMethods(String packageName) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File directory = new File(resource.getFile());
                    if (directory.exists()) {
                        File[] files = directory.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.getName().endsWith(".class")) {
                                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                                    Class<?> clazz = Class.forName(className);
                                    registerRequestMappingMethods(clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void registerComponentMethods(Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                componentes.put(m.getAnnotation(GetMapping.class).value(), m);
            }
        }
    }

    public static void registerRequestMappingMethods(Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                getMappings.put(m.getAnnotation(GetMapping.class).value(), m);
            }
            if(m.isAnnotationPresent(PostMapping.class)){
                postMappings.put(m.getAnnotation(PostMapping.class).value(), m);
            }
        }
    }

    // Maneja la lógica para los métodos GET en componentes
    public static void handleComponentGetMethod(String path) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = componentes.get(path.substring(11));
        if (m != null) {
            System.out.println("Salida: " + m.invoke(null, "jaider"));
        }
    }

    // Maneja la lógica para los métodos GET en RequestMapping
    public static String handleRequestMappingGetMethod(String path, String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = getMappings.get(path.substring(6));
        if (m != null) {
            return m.invoke(null, name).toString();
        }
        return null;
    }
    public static String handleRequestMappingPostMethod(String path, String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method m = postMappings.get(path.substring(6));
        if (m != null) {
            return m.invoke(null, name).toString();
        }
        return null;
    }
}