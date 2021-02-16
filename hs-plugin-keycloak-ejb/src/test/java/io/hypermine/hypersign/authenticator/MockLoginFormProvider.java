package io.hypermine.hypersign.authenticator;

import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.sessions.AuthenticationSessionModel;

import javax.ws.rs.core.*;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.*;

/*This is to be used while mocking the response from the getChallenge method*/
public class MockLoginFormProvider implements LoginFormsProvider {
    @Override
    public void addScript(String scriptUrl) {

    }

    @Override
    public Response createResponse(UserModel.RequiredAction action) {
        return null;
    }

    @Override
    public Response createForm(String form) {
        return new Response() {
            @Override
            public int getStatus() {
                return 0;
            }

            @Override
            public StatusType getStatusInfo() {
                return null;
            }

            @Override
            public Object getEntity() {
                return null;
            }

            @Override
            public <T> T readEntity(Class<T> entityType) {
                return null;
            }

            @Override
            public <T> T readEntity(GenericType<T> entityType) {
                return null;
            }

            @Override
            public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
                return null;
            }

            @Override
            public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
                return null;
            }

            @Override
            public boolean hasEntity() {
                return false;
            }

            @Override
            public boolean bufferEntity() {
                return false;
            }

            @Override
            public void close() {

            }

            @Override
            public MediaType getMediaType() {
                return null;
            }

            @Override
            public Locale getLanguage() {
                return null;
            }

            @Override
            public int getLength() {
                return 0;
            }

            @Override
            public Set<String> getAllowedMethods() {
                return null;
            }

            @Override
            public Map<String, NewCookie> getCookies() {
                return null;
            }

            @Override
            public EntityTag getEntityTag() {
                return null;
            }

            @Override
            public Date getDate() {
                return null;
            }

            @Override
            public Date getLastModified() {
                return null;
            }

            @Override
            public URI getLocation() {
                return null;
            }

            @Override
            public Set<Link> getLinks() {
                return null;
            }

            @Override
            public boolean hasLink(String relation) {
                return false;
            }

            @Override
            public Link getLink(String relation) {
                return null;
            }

            @Override
            public Link.Builder getLinkBuilder(String relation) {
                return null;
            }

            @Override
            public MultivaluedMap<String, Object> getMetadata() {
                return null;
            }

            @Override
            public MultivaluedMap<String, String> getStringHeaders() {
                return null;
            }

            @Override
            public String getHeaderString(String name) {
                return null;
            }
        };
    }

    @Override
    public String getMessage(String message) {
        return null;
    }

    @Override
    public String getMessage(String message, String... parameters) {
        return null;
    }

    @Override
    public Response createLogin() {
        return null;
    }

    @Override
    public Response createPasswordReset() {
        return null;
    }

    @Override
    public Response createLoginTotp() {
        return null;
    }

    @Override
    public Response createRegistration() {
        return null;
    }

    @Override
    public Response createInfoPage() {
        return null;
    }

    @Override
    public Response createUpdateProfilePage() {
        return null;
    }

    @Override
    public Response createIdpLinkConfirmLinkPage() {
        return null;
    }

    @Override
    public Response createIdpLinkEmailPage() {
        return null;
    }

    @Override
    public Response createLoginExpiredPage() {
        return null;
    }

    @Override
    public Response createErrorPage(Response.Status status) {
        return null;
    }

    @Override
    public Response createOAuthGrant() {
        return null;
    }

    @Override
    public Response createCode() {
        return null;
    }

    @Override
    public Response createX509ConfirmPage() {
        return null;
    }

    @Override
    public LoginFormsProvider setAuthenticationSession(AuthenticationSessionModel authenticationSession) {
        return null;
    }

    @Override
    public LoginFormsProvider setClientSessionCode(String accessCode) {
        return null;
    }

    @Override
    public LoginFormsProvider setAccessRequest(List<ClientScopeModel> clientScopesRequested) {
        return null;
    }

    @Override
    public LoginFormsProvider setError(String message, Object... parameters) {
        return null;
    }

    @Override
    public LoginFormsProvider setErrors(List<FormMessage> messages) {
        return null;
    }

    @Override
    public LoginFormsProvider addError(FormMessage errorMessage) {
        return null;
    }

    @Override
    public LoginFormsProvider addSuccess(FormMessage errorMessage) {
        return null;
    }

    @Override
    public LoginFormsProvider setSuccess(String message, Object... parameters) {
        return null;
    }

    @Override
    public LoginFormsProvider setInfo(String message, Object... parameters) {
        return null;
    }

    @Override
    public LoginFormsProvider setUser(UserModel user) {
        return null;
    }

    @Override
    public LoginFormsProvider setResponseHeader(String headerName, String headerValue) {
        return null;
    }

    @Override
    public LoginFormsProvider setFormData(MultivaluedMap<String, String> formData) {
        return null;
    }

    @Override
    public LoginFormsProvider setAttribute(String name, Object value) {
        return new MockLoginFormProvider();
    }

    @Override
    public LoginFormsProvider setStatus(Response.Status status) {
        return null;
    }

    @Override
    public LoginFormsProvider setMediaType(MediaType type) {
        return null;
    }

    @Override
    public LoginFormsProvider setActionUri(URI requestUri) {
        return null;
    }

    @Override
    public LoginFormsProvider setExecution(String execution) {
        return null;
    }

    @Override
    public void close() {

    }
}
