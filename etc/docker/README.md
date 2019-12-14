# Install docker and docker-compose

* install [Docker](https://docs.docker.com/install/)
* install `docker-compose`
  * with [pip](https://packaging.python.org/tutorials/installing-packages/): `pip install docker-compose`
# Docker-compose scenarios

* create and run docker images for all server dependencies (mysql, mailhog):
``` shell
docker-compose -f etc/docker/docker-compose.yml up
```
* create and run the docker image for the server (copies the local version):
``` shell
docker-compose -f etc/docker/docker-compose-dev.yml up
```
* create and run docker images for the server (clones the latest version) and all its dependencies:
``` shell
docker-compose -f etc/docker/docker-compose-prod.yml up --build
```
* create and run the docker image for the client on port 80:
``` shell
docker-compose -f etc/docker/docker-compose-frontend.yml up --build
```
# Docker-compose services


## Database

* to connect to the dockerized server with the `mysql` client run: `docker-compose exec mysql mysql -u root -p`

* in order to remove the volumes (eg. the database volume)  associated with a compose file run:

``` shell
docker-compose -f etc/docker/docker-compose.yml down -v
```

## Mailhog

* you can use [Mailhog](https://hub.docker.com/u/mailhog) to test email sending/receiving:
  * to check all sent emails from the server go to `http://localhost:8025`.
