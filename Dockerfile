FROM virtuslab/scala-cli:1.5.0 as build

WORKDIR /source

ARG buildext=java

COPY WebSocketServer.java .
COPY WebSocketServer.scala .

RUN scala-cli config power true

RUN scala-cli package *.${buildext} --assembly -f -o backend --server=false

FROM eclipse-temurin:23 

COPY --from=build /source/backend /app 

EXPOSE 9191

CMD ["/app"]
