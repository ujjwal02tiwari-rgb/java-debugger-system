# ---------- Frontend build ----------
FROM node:18 AS fe-build
WORKDIR /app
# Only copy pkg manifests first to leverage caching
COPY frontend/package.json frontend/package-lock.json* frontend/yarn.lock* ./
RUN npm ci || npm install
# now copy the rest of the frontend
COPY frontend/ .
RUN npm run build

# ---------- Backend build ----------
FROM eclipse-temurin:17-jdk AS builder
# Gradle wrapper needs unzip for first download of Gradle dist
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace
# Copy backend first (so changes in frontend don't always bust Gradle cache)
COPY build.gradle settings.gradle gradlew gradlew.bat ./
COPY gradle ./gradle
# Warm gradle deps cache (optional but speeds up repeated builds)
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

# Copy the rest of the backend sources
COPY src ./src

# Copy the built frontend into Spring Boot's static resources
# so it’s packaged inside the jar at classpath:/static/**
RUN mkdir -p src/main/resources/static && rm -rf src/main/resources/static/*

COPY --from=fe-build /app/build/ ./src/main/resources/static/

# Build the Spring Boot fat jar
RUN chmod +x ./gradlew && ./gradlew --no-daemon clean bootJar

# ---------- Runtime ----------
FROM eclipse-temurin:17-jre
WORKDIR /workspace
COPY --from=builder /workspace/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
