#!/usr/bin/env bash

KCBASE=$KCBASE

if [ "$KCBASE" == "" ]; then
  echo "Environment variable KCBASE is not. Set it to proceed."
  exit
fi

echo "Keycloak homepath is : ${KCBASE}"
echo "Building the hypersign plugin.."
./build.sh 

echo "Cleaning the hypersign plugin.."
rm -rf ${KCBASE}/hs-plugin-keycloak-ejb-0.2-SNAPSHOT.jar
rm -rf ${KCBASE}/modules/hs-plugin-keycloak-ejb/

echo "Coping the plugin.."
cp ./dist/hs-plugin-keycloak-ejb-0.2-SNAPSHOT.jar ${KCBASE}

echo "Dploying the hypersign theme.."
cp hs-themes/hypersign-config.ftl ${KCBASE}/themes/base/login
cp hs-themes/hypersign.ftl ${KCBASE}/themes/base/login
cp hs-themes/hypersign-new.ftl ${KCBASE}/themes/base/login

echo "Deploying the hypersign plugin.."
cd ${KCBASE}
./bin/jboss-cli.sh --command="module add --name=hs-plugin-keycloak-ejb --resources=./hs-plugin-keycloak-ejb-0.2-SNAPSHOT.jar --dependencies=org.keycloak.keycloak-common,org.keycloak.keycloak-core,org.keycloak.keycloak-services,org.keycloak.keycloak-model-jpa,org.keycloak.keycloak-server-spi,org.keycloak.keycloak-server-spi-private,javax.ws.rs.api,javax.persistence.api,org.hibernate,org.javassist,org.liquibase,com.fasterxml.jackson.core.jackson-core,com.fasterxml.jackson.core.jackson-databind,com.fasterxml.jackson.core.jackson-annotations,org.jboss.resteasy.resteasy-jaxrs,org.jboss.logging,org.apache.httpcomponents,org.apache.commons.codec,org.keycloak.keycloak-wildfly-adduser"

echo "Running keycloak.."
kill -9 $(lsof -t -i:8080)
./bin/standalone.sh

echo "Finish!"
