# Install docker and docker-compose

* install [Docker](https://docs.docker.com/install/)
* install `docker-compose`
  * with [pip](https://packaging.python.org/tutorials/installing-packages/): `pip install docker-compose`
# Docker-compose scenarios

* start the server:
``` shell
docker-compose -f etc/docker/docker-compose-dev.yml up
```
* start the server dependencies only:
``` shell
docker-compose -f etc/docker/docker-compose.yml up
```
* clone and start the server and all its dependencies::
``` shell
docker-compose -f etc/docker/docker-compose-prod.yml up --build
```
* clone and run all tests:
``` shell
docker-compose -f etc/docker/docker-compose-test.yml up --build
```
