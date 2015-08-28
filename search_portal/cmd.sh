#!/bin/bash

BASEDIR=$(dirname $0)
DEPLOY_DIR=$BASEDIR/target/search_portal-0.0.1-SNAPSHOT/

case $1 in

  clean_host)
    docker rm $(docker ps -a -q)
    docker rmi $(docker images | grep "^<none>" | awk "{print $3}")
    ;;
  check_local_port)
    lsof -i :$2
    ;;
  wipe_clean)
    # Delete all containers
    docker rm $(docker ps -a -q)
    # Delete all images
    docker rmi $(docker images -q)
    ;;
  run_stage)
     echo "Packaging the Application..."
     lein clean
     lein ring uberwar
     mkdir $DEPLOY_DIR
     pwd
     jar xf $BASEDIR/target/search_portal-0.0.1-SNAPSHOT-standalone.war
     mv WEB-INF/classes/appengine-web.xml WEB-INF/
     mv META-INF $DEPLOY_DIR/
     mv WEB-INF $DEPLOY_DIR/
     echo "Deploying Application to STAGE"
     mvn appengine:devserver
    ;;
  deploy_prod)
    echo "Packaging the Application..."
    lein clean
    lein ring uberwar
    cd $BASEDIR/target && mkdir search_portal-0.0.1-SNAPSHOT
    jar xf ../search_portal-0.0.1-SNAPSHOT-standalone.war
    mv WEB-INF/classes/appengine-web.xml WEB-INF/
    cd $BASEDIR
    echo "Deploying Application to PROD"
    mvn appengine:update
    ;;
  phx_db_migrate)
    echo "Running DB Migrations"
    cd $BASEDIR && mix ecto.migrate
    echo "Done with the DB Migrations"
    ;;
  phx_db_seed)
    echo "Seeding DB"
    cd $BASEDIR && mix run seeds.exs
    echo "Done Seeding the DB"
    ;;
  esac
exit 0
