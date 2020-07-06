./mvnw -f order/pom.xml spring-boot:build-image
docker push docker.io/enricocostanzi/orderservice:1.0.0

./mvnw -f product/pom.xml spring-boot:build-image
docker push docker.io/enricocostanzi/productservice:1.0.0



