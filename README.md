# IO.Swagger - ASP.NET Core 2.0 Server

This is an example of using OAuth2 Application Flow in a specification to describe security to your API.

## Run

Linux/OS X:

```
sh build.sh
```

Windows:

```
build.bat
```

## Run in Docker

```
cd src/IO.Swagger
docker build -t io.swagger .
docker run -p 5000:5000 io.swagger
```
