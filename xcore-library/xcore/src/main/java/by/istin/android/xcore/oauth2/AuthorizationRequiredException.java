package by.istin.android.xcore.oauth2;

import java.io.IOException;

public class AuthorizationRequiredException extends IOException {


    public AuthorizationRequiredException(String detailMessage) {
        super(detailMessage);
    }
}
