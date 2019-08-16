/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.hanko.plugin.keycloak.authentication;

import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.credential.*;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;

import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class HankoCredentialProvider implements CredentialProvider, CredentialInputValidator, CredentialInputUpdater, OnUserCache {

    private static final Logger logger = Logger.getLogger(HankoCredentialProvider.class);

    public static final String TYPE = "hanko";

    private KeycloakSession session;

    public HankoCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return false;
        }

        if (!TYPE.equals(credentialType)) {
            return false;
        }

        return !session.userCredentialManager().getStoredCredentialsByType(realm, user, TYPE).isEmpty();
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        throw new UnsupportedOperationException("Authenticator should validate credential");
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType())) return false;

        CredentialModel model = new CredentialModel();
        model.setType(TYPE);
        model.setCreatedDate(Time.currentTimeMillis());
        model.setValue(((UserCredentialModel) input).getValue());

        session.userCredentialManager().createCredential(realm, user, model);

        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) {
            return;
        }

        for (CredentialModel credential : session.userCredentialManager().getStoredCredentialsByType(realm, user, TYPE)) {
            session.userCredentialManager().removeStoredCredential(realm, user, credential.getId());
        }
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return isConfiguredFor(realm, user, TYPE) ? Collections.singleton(TYPE) : Collections.emptySet();
    }

    @Override
    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
    }

}
