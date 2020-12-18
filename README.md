# PROJ731

# How to run the docker images ?

Go to src directory 
```
cd src
```

First start the switcher image

## Switcher image 
```
 docker build --rm -f  Switcher/Dockerfile -t noahrz/rmi-switcher .
 docker run --network host --name switcher noahrz/rmi-switcher
```
Then start the machine image
## Machine image 
```
docker build --rm -f  Machine/Dockerfile -t noahrz/rmi-machine .
docker run --network host  --name machine  noahrz/rmi-machine
```
To go inside the machine container
```
docker exec -it <container_ID> sh
```

Finally start the client image

## Client image
```
docker build --rm -f  Client/Dockerfile -t noahrz/rmi-client .
docker run -it --network host --name client noahrz/rmi-client sh
```
notice the "sh" ending, this allows you to go inside the client container you have just created

Once inside the client container you can :
* Write a new file or an existing file : ```java Client <port_number> write <filename>```
* Read an existing file : ```java Client <port_number> read <filename> ```
  
