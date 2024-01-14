
//if u dont know just do the sandbox code on test after that integrate into main fikee

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import fr.epita.dataModel.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class HttpServerTest {

    public static void main(String[] args) throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080) , 0);

        HttpContext context = server.createContext("/");
        context.setHandler(
                ex -> {
                    String requestMethod = ex.getRequestMethod();
                    switch (requestMethod) {
                        case "GET":
                            System.out.println("test the get");
                            User user = new User("test", "test", "test");
                            List<User> users = Arrays.asList(user);
                            ObjectMapper mapper = new ObjectMapper();
                            String jsonString = mapper.writeValueAsString(users);
                            byte[] bytes = jsonString.getBytes();
                            ex.sendResponseHeaders(200, bytes.length);
                            ex.getResponseBody().write(bytes);
                            ex.close();
                            break;
                        case "POST":
                            System.out.println("testing the post connection");
                            ObjectMapper m = new ObjectMapper();
                            User userToBeCreated = m.readValue(ex.getRequestBody(), User.class);
                            System.out.println(userToBeCreated);
                            byte[] response = "user created".getBytes();
                            ex.sendResponseHeaders(201, response.length);
                            ex.getResponseBody().write(response);
                            break;


                        default:
                            System.out.println("hey");

                    }
                });

        server.start();
        getandreq();
        getpostreq();
        server.stop(1000);

    }
  private static void getandreq() throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/")
                .openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        connection.getResponseCode();
        InputStream inputStream = connection.getInputStream();
        byte[] bytes = inputStream.readAllBytes();
        String responseText = new String(bytes);
        System.out.println(responseText);

        ObjectMapper mapper = new ObjectMapper();
        List<User>users = mapper.readValue(responseText, new TypeReference<List<User>>() {}
        );
        System.out.println("the users list is "+users.size());

    }

    private static void getpostreq() throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/")
                .openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        User user = new User("post" , "post" , "post");
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(user);
        connection.getOutputStream().write(jsonString.getBytes());
        connection.connect();
        int responseCode = connection.getResponseCode();
        System.out.println("r"+responseCode);


    }


}
