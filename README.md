# hs-authenticator
Hypersign authenticator for Keycloak

1. First, Keycloak must be running.
   
   To install using Docker Container https://hub.docker.com/r/jboss/keycloak/

2. To build the hs-authenticator jar file.
   - Run mvn clean install
   - Go to the target folder, you should see new jar created.
   - Take the jar file and put it under the keycloak/standalone/deployments folder.

3. Copy the hyerpsign-config.ftl and hyerpsign.ftl files to the themes/base/login directory.

4. Login to admin console.  Hit browser refresh if you are already logged in so that the new providers show up.

5. Go to the Authentication menu item and go to the Flow tab, you will be able to view the currently
   defined flows.  You cannot modify an built in flows, so, to add the Authenticator you
   have to copy an existing flow or create your own.  Copy the "Browser" flow.

6. In your copy, click the "Actions" menu item and "Add Execution".  Pick Secret Question

7. Next you have to register the required action that you created. Click on the Required Actions tab in the Authenticaiton menu.
   Click on the Register button and choose your new Required Action.
   Your new required action should now be displayed and enabled in the required actions list.


