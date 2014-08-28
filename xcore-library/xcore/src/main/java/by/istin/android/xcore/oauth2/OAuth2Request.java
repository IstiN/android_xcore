package by.istin.android.xcore.oauth2;

public abstract class OAuth2Request<Request> {

    private Request request;

    public OAuth2Request(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public abstract void sign(Request request, String header, String value);

}
