package io.hypermine.hypersign.api;


import org.junit.runner.RunWith;
import org.keycloak.models.KeycloakSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HSResourceProviderTest {
    @Mock
    private KeycloakSession keycloakSession;


    @org.junit.Test
    public void register() {
        String body = "{\n" +
                "  \"did\": \"12321321323d\",\n" +
                "  \"email\": \"dev@hypermine.com\",\n" +
                "  \"username\": \"dev gurung\",\n" +
                "  \"companyid\": \"hypermine\"\n" +
                "}";
        HSResourceProvider hsResourceProvider = new HSResourceProvider(keycloakSession);
        assertEquals("{\"status\":\"FAIL\",\"data\":\"java.lang.Exception: Could not create the user\"}", hsResourceProvider.register(body).getEntity());
    }

    @org.junit.Test
    public void sign() {
    }
}