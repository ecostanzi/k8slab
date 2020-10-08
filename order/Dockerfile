FROM adoptopenjdk:11-jre-hotspot
RUN apt-get install -y curl
RUN addgroup spring && useradd -g spring spring
USER spring:spring
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","org.ecostanzi.order.OrderApplication"]
