package com.macro.mall.security.component.wx;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WxOneKeyAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
    public static final String SPRING_SECURITY_FORM_ENCRYPTED_DATA_KEY = "encryptedData";
    public static final String SPRING_SECURITY_FORM_IV_KEY = "iv";

    private final boolean postOnly = true;

    public WxOneKeyAuthenticationFilter() {
        super(new AntPathRequestMatcher("/loginByCode", "POST"));
    }

    protected WxOneKeyAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    protected WxOneKeyAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        String code = obtainCode(request);
        String encryptedData = obtainEncryptedData(request);
        String iv = obtainIv(request);

        WxOneKeyAuthenticationToken authRequest = new WxOneKeyAuthenticationToken(
                code, encryptedData);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
                              WxOneKeyAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }


    @Nullable
    protected String obtainCode(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_CODE_KEY);
    }

    @Nullable
    protected String obtainEncryptedData(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_ENCRYPTED_DATA_KEY);
    }

    @Nullable
    protected String obtainIv(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_IV_KEY);
    }
}
