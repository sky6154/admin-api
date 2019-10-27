#!/bin/sh
{
  echo try remove old services...
  docker service rm admin-api
}
{
  echo try remove old images...
  docker rmi -f admin-api:latest
}
{
  echo try system prune...
  docker system prune -f
}

docker load < admin-api.tar
docker service create --name admin-api --replicas 2 --publish 8888:8080 admin-api:latest