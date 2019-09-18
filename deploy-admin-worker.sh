#!/bin/sh
{
  echo try remove old images...
  docker rmi -f admin-api:latest
}
{
  echo try system prune...
  docker system prune -f
}

docker load < admin-api.tar