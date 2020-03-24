package ba.unsa.etf.si.utility;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

public class HttpUtils {

    private HttpUtils() {}

    private static HttpClient client;

    static {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    public static HttpRequest GET(String url, String... headers){
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(20)).GET();
        if(headers != null) builder.headers(headers);
        return builder.build();
    }

    public static HttpRequest DELETE(String url, String... headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(20)).DELETE();
        if(headers != null) builder.headers(headers);
        return builder.build();
    }

    public static HttpRequest POST(HttpRequest.BodyPublisher bodyPublisher, String url, String... headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(20)).POST(bodyPublisher);
        if(headers != null) builder.headers(headers);
        return builder.build();
    }

    public static HttpRequest PUT(HttpRequest.BodyPublisher bodyPublisher, String url, String... headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(20)).PUT(bodyPublisher);
        if(headers != null) builder.headers(headers);
        return builder.build();
    }

    public static <T> void send(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler, Consumer<? super T> callback) {
        client.sendAsync(request, bodyHandler).thenApply(HttpResponse::body).thenAccept(callback);
    }
}
