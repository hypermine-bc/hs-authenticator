package io.hypermine.hypersign.authenticator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationProcessor;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.forms.login.freemarker.FreeMarkerLoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class HyperSignAuthenticatorTest {

    @Mock
    private AuthenticationFlowContext authenticationFlowContext;
    @Test
    public void getChallenge() {
        PowerMockito.mock(AuthenticationFlowContext.class);
        when(authenticationFlowContext.form()).thenReturn(new MockLoginFormProvider());
        HyperSignAuthenticator hyperSignAuthenticator = new HyperSignAuthenticator();
        assertNotNull (hyperSignAuthenticator.getChallenge(authenticationFlowContext));

    }
}