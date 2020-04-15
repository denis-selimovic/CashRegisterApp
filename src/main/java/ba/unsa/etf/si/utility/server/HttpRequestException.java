package ba.unsa.etf.si.utility.server;


public class HttpRequestException extends RuntimeException {

    public HttpRequestException() {
        super();
    }

    public HttpRequestException(String msg) {
        super(msg);
    }

    public HttpRequestException(Throwable ex) {
        super(ex);
    }

    public HttpRequestException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
