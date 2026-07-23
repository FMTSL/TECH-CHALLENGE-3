# ---- build ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/tech-challenge-fiap-fase3-*.jar app.jar
EXPOSE 8080
# JAVA_OPTS fica vazio por padrao (Docker Compose local usa a heap default da JVM,
# adequada quando ha memoria disponivel). Em ambientes com RAM limitada (ex: Render
# free tier, 512MB), sobrescreva via variavel de ambiente - ver render.yaml.
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Xms128m -Xmx384m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dserver.port=${PORT:-8080} -jar app.jar"]
