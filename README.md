# soapbox-race

webserver to handle cars and pilots profiles.

to build need apache maven and jdk8

https://maven.apache.org/

http://www.oracle.com/technetwork/java/javase/downloads/

build:
mvn clean compile assembly:single

#####to run the server use:

    java -Djsse.enableCBCProtection=false -jar soapbox-race-version-jarname.jar your-ip-to-host-xmpp (optional)

example:

    java -Djsse.enableCBCProtection=false -jar soapbox-race-version-jarname.jar 127.0.0.1

---

users:

nobody@nowhere	secret

anyone@anywhere	anysecret

more@nomore	notsecret

to use the client, access browser at url:

    http://localhost:1337/soapbox/nothing/user/authenticateUser?email=SOME_EMAIL&password=SOME_PASSWORD

examples:

    user1
    http://localhost:1337/soapbox/nothing/user/authenticateUser?email=nobody@nowhere&password=secret
    
    user2
    http://localhost:1337/soapbox/nothing/user/authenticateUser?email=anyone@anywhere&password=anysecret
    
    user3
    http://localhost:1337/soapbox/nothing/user/authenticateUser?email=more@nomore&password=notsecret

your browser show a number like this:

    n.zzzzzzzzzzzzzzz

next, you call soapbox.exe with the parameters: 

    soapbox.exe US http://localhost:1337/soapbox/Engine.svc zzzzzzzzzzzzzzzz n
    
---

A launcher is on the way

https://github.com/berkay2578/soapbox-race-launcher

for more users, insert userid, email and passowrd in table user
