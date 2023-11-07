package client;

import exception.RequestFailedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String url;
    private String apiToken;
    private HttpClient client;

    public KVTaskClient(int port) {
        url = "http://localhost:" + port + "/";
        client = HttpClient.newHttpClient();
        apiToken = register(url);

    }

    private String register(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RequestFailedException("Не возможно сделать запрос на регистрацию, status code ="
                        + response.statusCode());
            }
        } catch (InterruptedException | IOException exception) {
            throw new RequestFailedException("Невозможно сделать  register запрос");
        }
    }

    public void save(String key, String value) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RequestFailedException("Не возможно сделать запрос на сохранение, status code ="
                        + response.statusCode());
            }
        } catch (InterruptedException | IOException exception) {
            throw new RequestFailedException("Невозможно сделать save запрос");
        }
    }

    public String load(String key) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RequestFailedException("Не возможно сделать запрос на загрузку, status code ="
                        + response.statusCode());
            }
            return response.body();
        } catch (InterruptedException | IOException exception) {
            throw new RequestFailedException("Невозможно сделать load запрос");
        }
    }

    public String getApiToken() {
        return apiToken;
    }
}
