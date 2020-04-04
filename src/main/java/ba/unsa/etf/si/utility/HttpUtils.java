package ba.unsa.etf.si.utility;

import ba.unsa.etf.si.utility.exceptions.HttpRequestException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class HttpUtils {

    private static final Long DURATION = 20L;

    private HttpUtils() {}

    private static HttpClient client;

    static {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public static HttpRequest GET(String url, String... headers){
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(DURATION)).GET();
        if(headers != null && headers.length != 0) builder.headers(headers);
        return builder.build();
    }

    public static HttpRequest DELETE(String url, String... headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(DURATION)).DELETE();
        if(headers != null && headers.length != 0) builder.headers(headers);
        return builder.build();
    }

    public static HttpRequest POST(HttpRequest.BodyPublisher bodyPublisher, String url, String... headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(DURATION)).POST(bodyPublisher);
        if(headers != null && headers.length != 0) builder.headers(headers);
        return builder.build();
    }

    public static HttpRequest PUT(HttpRequest.BodyPublisher bodyPublisher, String url, String... headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(DURATION)).PUT(bodyPublisher);
        if(headers != null && headers.length != 0) builder.headers(headers);
        return builder.build();
    }

    public static <T> void send(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler, Consumer<? super T> callback, Runnable err) {
        CompletableFuture<HttpResponse<T>> future = client.sendAsync(request, bodyHandler);
        future.thenApply(response -> {
            if(future.isCompletedExceptionally()) throw new HttpRequestException();
            return response.body();
        }).handle((response, ex) -> {
            if(ex != null) err.run();
            else callback.accept(response);
            return response;
        });
    }


    /* važno - primjer korištenja rekurzivnog callbacka za http polling

    /*HttpUtils.RecursiveCallback<Consumer<String>> recursiveCallback = new HttpUtils.RecursiveCallback<>();
        HttpRequest GET = HttpUtils.GET("http://localhost:8080");
        recursiveCallback.callback = response -> {
            JSONObject json = new JSONObject(response);
            if(!json.get("message").equals("OK")) {
                System.out.println("Sendind request again!");
                HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), recursiveCallback.callback, () -> {System.out.println("ERROR!");});
            }
            else {
                System.out.println("OK");
            }
        };
        HttpUtils.send(GET, HttpResponse.BodyHandlers.ofString(), recursiveCallback.callback, () -> {System.out.println("ERROR!");});*/

    public static final class RecursiveCallback<T> {
        public T callback;
    }
}
