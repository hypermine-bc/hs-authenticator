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

package io.hypermine.hypersign.api;

import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.ServicesLogger;
import org.keycloak.models.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.keycloak.wildfly.adduser.*;
import org.codehaus.jackson.map.ObjectMapper;
import io.hypermine.hypersign.service.AuthServerCaller;
import org.keycloak.models.utils.RepresentationToModel;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class HSResourceProvider implements RealmResourceProvider {

    class HSUserModel {
        String challange;
        String userId;
        boolean hasLoggedIn;

        public HSUserModel(String challange, String userId, boolean hasLoggedIn) {
            this.challange = challange;
            this.userId = userId;
            this.hasLoggedIn = hasLoggedIn;
        }
    }

    class FResponse {
        public String status;
        public String data;

        public FResponse() {
        }

        public FResponse(String status, String data) {
            this.status = status;
            this.data = data;
        }
    }

    enum Status {
        SUCCESS,
        FAIL
    }

    private static ServicesLogger logger = ServicesLogger.LOGGER;
    private static HashMap<String, HSUserModel> userSessionMap = new HashMap<>();// this is temporary
    private KeycloakSession session;
    private static String hsAuthServerEp = "";

    public HSResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    private String getHsEP() throws IOException {
        String hsAuthServerEp = "";
        FileInputStream fis = null;
        try {
            logger.info("Inside the getHsEP constructor");
            String fileName = System.getProperty("jboss.server.config.dir") + "/hypersign.properties";
            logger.info(fileName);
            Properties properties = new Properties();
            fis = new FileInputStream(fileName);
            properties.load(fis);
            hsAuthServerEp = properties.getProperty("auth-server-endpoint");
            if (!hsAuthServerEp.isEmpty()) {
                //add / if not there
                if (hsAuthServerEp.charAt(hsAuthServerEp.length() - 1) != '/') {
                    hsAuthServerEp = hsAuthServerEp + "/";
                }
                logger.info("HS auth server endpoint configured.");
                logger.info(hsAuthServerEp);
            } else {
                logger.info("HS auth server endpoint not configured. Setting it to default");
                hsAuthServerEp = "http://localhost:3000/";
                logger.info(hsAuthServerEp);
            }
        } catch (IOException e) {
            logger.info(e.toString());
        } finally {
            fis.close();
        }
        logger.info("Inside the getHsEP constructor ends");
        return hsAuthServerEp;
    }

    @Override
    public Object getResource() {
        return this;
    }

    @OPTIONS
    @Path("{path : .*}")
    public Response options() {
        return Response.ok("")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Max-Age", "1209600")
                .build();
    }

    @GET
    @Produces("text/plain; charset=utf-8")
    public Response get() {
        logger.info("This is hypersign authenticator api call");
        String name = session.getContext().getRealm().getDisplayName();
        if (name == null) {
            name = session.getContext().getRealm().getName();
        }
        return this.formattedReponse(Status.SUCCESS, "Hi, there!  " + name);
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String body) {
        logger.info("Register api called!");
        JSONObject json = new JSONObject();
        String userDID = "";
        String emaiid = "";
        String username = "";
        String companyid = "";
        UserModel newuser = null;
        try {
            if (!body.isEmpty()) {
                json = new JSONObject(body);
                if (json != null) {
                    userDID = json.getString("did");
                    emaiid = json.getString("email");
                    username = json.getString("username");
                    companyid = json.getString("companyid");
                    if (!userDID.isEmpty() || !emaiid.isEmpty()) {
                        // saving the user in keycloak
                        UserRepresentation userRep = new UserRepresentation();
                        userRep.setUsername(username);
                        userRep.setId(userDID);
                        userRep.setEmail(emaiid);
                        userRep.setEnabled(true);
                        userRep.setEmailVerified(false);
                        newuser = this.session != null && this.session.getContext() != null
                                ? RepresentationToModel.createUser(this.session, this.session.getContext().getRealm(), userRep)
                                : null;
                        if (newuser != null) {
                            return this.formattedReponse(Status.SUCCESS, json.getString("message"));
                        } else {
                            throw new Exception("Could not create the user");
                        }

                    } else {
                        throw new Exception("Publickey or emailId is null");
                    }
                } else {
                    throw new Exception("Could not parse the body");
                }
            } else {
                throw new Exception("Request body is null");
            }
        } catch (Exception e) {
            return this.formattedReponse(Status.FAIL, e.toString());
        }
    }

    @POST
    @Path("sign")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sign(String body) {
        logger.info("sign api called!");
        JSONObject bodyObj = null;
        String sessionId = "";
        String challange = "";
        String publickey = "";
        String signature = "";
        String companyId = "";
        String rawMessage = "";
        HSUserModel user = null;
        try {
            if (!body.isEmpty()) {
                bodyObj = new JSONObject(body);
                if (bodyObj != null) {
                    publickey = bodyObj.getString("publicKey");
                    sessionId = bodyObj.getString("ksSessionId");
                    challange = bodyObj.getString("challenge");
                    signature = bodyObj.getString("signedRsv");
                    companyId = bodyObj.getString("companyId");
                    rawMessage = bodyObj.getString("rawMsg");
                    if (!publickey.isEmpty() ||
                            !sessionId.isEmpty() ||
                            !signature.isEmpty() ||
                            !challange.isEmpty() ||
                            !companyId.isEmpty()) {
                        if (isSignatureValid(body)) {
                            if (userSessionMap.containsKey(sessionId)) {
                                user = new HSUserModel(challange, publickey, true);
                                userSessionMap.put(sessionId, user);
                                return this.formattedReponse(Status.SUCCESS, "User authenticated");
                            } else {
                                throw new Exception("Invalid session");
                            }
                        } else {
                            throw new Exception("Invalid signature");
                        }
                    } else {
                        throw new Exception("Publickey, sessionId or signature is null");
                    }
                } else {
                    throw new Exception("Could not parse the body");
                }
            } else {
                throw new Exception("Request body is null");
            }
        } catch (Exception e) {
            return this.formattedReponse(Status.FAIL, e.toString());
        }
    }

    @POST
    @Path("session")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNewSession(String body) {
        logger.info("session api called!");
        JSONObject json = null;
        try {
            if (!body.isEmpty()) {
                if (userSessionMap != null) {
                    json = new JSONObject(body);
                    String ksSessionId = json.getString("kcSessionId");
                    String companyId = json.getString("companyId");
                    if (json != null && !ksSessionId.isEmpty()) {
                        // call auth-server for session/challenge
                        String url = this.getHsEP() + "challenge";
                        String reqstBody = "{\"kcSessionId\" : \"" + ksSessionId + "\", \"companyId\":\"" + companyId + "\"}";
                        String response = AuthServerCaller.postApiCall(url, reqstBody);
                        json = new JSONObject(response);
                        if (json != null && json.getInt("status") == 1) {
                            // json = new JSONObject(json.get("data"));
                            userSessionMap.put(ksSessionId, null); // this is kc session                    
                            return json.getString("data"); //this.formattedReponse(Status.SUCCESS, sessionId);    
                        } else {
                            throw new Exception(json.getString("message"));
                        }
                    } else {
                        throw new Exception("Keycloak sessionid can not be null");
                    }

                } else {
                    throw new Exception("userSessionMap is null");
                }
            } else {
                throw new Exception("Request body can not be null");
            }

        } catch (Exception e) {
            return e.toString();//this.formattedReponse(Status.FAIL,e.toString());            
        }
    }

    @GET
    @Path("listen/success/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listenSuccess(@PathParam("sessionId") String sessionId) {
        logger.info("listen/success api called!");
        try {
            if (userSessionMap != null) {
                if (userSessionMap.containsKey(sessionId) && userSessionMap.get(sessionId) != null) {
                    HSUserModel user = userSessionMap.get(sessionId);
                    if (user != null && user.hasLoggedIn) {
                        return this.formattedReponse(Status.SUCCESS, user.userId);
                    } else {
                        throw new Exception("User not found or not validated");
                    }
                } else {
                    throw new Exception("Invalid session or user has not yet validated");
                }
            } else {
                throw new Exception("userSessionMap is null");
            }
        } catch (Exception e) {
            return this.formattedReponse(Status.FAIL, e.toString());
        }
    }

    @GET
    @Path("listen/fail/{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listenFail(@PathParam("sessionId") String sessionId) {
        logger.info("listen/fail api called!");
        try {
            if (userSessionMap != null) {
                if (userSessionMap.containsKey(sessionId)) {
                    userSessionMap.remove(sessionId);
                    return this.formattedReponse(Status.SUCCESS, "Session deleted");
                } else {
                    throw new Exception("Invalid session");
                }
            } else {
                throw new Exception("userSessionMap is null");
            }
        } catch (Exception e) {
            return this.formattedReponse(Status.FAIL, e.toString());
        }
    }

    private Response formattedReponse(Status status, String data) {
        String respStr = "";
        try {
            FResponse response = new FResponse();
            response.status = status.name();
            response.data = data;
            ObjectMapper Obj = new ObjectMapper();
            // JSONObject bodyObj = new JSONObject(response);
            respStr = Obj.writeValueAsString(response);
            return Response
                    .status(Response.Status.OK)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .entity(respStr)
                    .build();
        } catch (Exception e) {
            respStr = e.toString();
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Max-Age", "1209600")
                    .entity(respStr)
                    .build();
        }
        // return respStr;
    }

    private Boolean isSignatureValid(String body) {
        try {
            // call auth-server to validate this user.
            String url = this.getHsEP() + "verify";
            String responseFromAuthServer = AuthServerCaller.postApiCall(url, body);
            JSONObject json = new JSONObject(responseFromAuthServer);
            if (json != null && json.getInt("status") == 1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
    }

}
