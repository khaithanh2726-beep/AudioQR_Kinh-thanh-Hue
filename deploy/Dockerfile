FROM node:24.22.2-bookworm-slim AS frontend
WORKDIR /source/frontend
COPY frontend/package*.json ./
RUN npm ci --no-audit --no-fund
COPY frontend/ ./
ARG VITE_PUBLIC_START_URL=
ARG VITE_NGROK_SKIP_BROWSER_WARNING=false
ARG VITE_MAP_PREVIEW=false
ARG VITE_MAP_BASEMAP=osm
ARG VITE_OSM_TILE_URL=https://tile.openstreetmap.org/{z}/{x}/{y}.png
ENV VITE_PUBLIC_START_URL=$VITE_PUBLIC_START_URL
ENV VITE_NGROK_SKIP_BROWSER_WARNING=$VITE_NGROK_SKIP_BROWSER_WARNING
ENV VITE_MAP_PREVIEW=$VITE_MAP_PREVIEW
ENV VITE_MAP_BASEMAP=$VITE_MAP_BASEMAP
ENV VITE_OSM_TILE_URL=$VITE_OSM_TILE_URL
RUN npm test && npm run build

FROM maven:3.9.12-eclipse-temurin-21 AS backend
WORKDIR /source/backend
COPY backend/pom.xml ./
RUN mvn -B dependency:go-offline
COPY backend/src ./src
COPY --from=frontend /source/frontend/dist ./src/main/resources/static
RUN mvn -B -DskipTests package

FROM ubuntu:24.04 AS audio
RUN apt-get update && apt-get install -y --no-install-recommends build-essential nasm pkg-config libmp3lame-dev curl ca-certificates xz-utils && rm -rf /var/lib/apt/lists/*
WORKDIR /source
RUN curl --fail --location --retry 3 https://ffmpeg.org/releases/ffmpeg-9.0.2.tar.xz -o ffmpeg.tar.xz \
    && tar -xf ffmpeg.tar.xz \
    && cd ffmpeg-9.0.2 \
    && ./configure --prefix=/opt/ffmpeg --disable-debug --disable-doc --disable-ffplay --disable-autodetect --disable-everything \
        --enable-ffmpeg --enable-ffprobe --enable-libmp3lame --enable-protocol=file,pipe \
        --enable-demuxer=wav,mp3 --enable-decoder=pcm_s16le,mp3float --enable-parser=mpegaudio \
        --enable-encoder=libmp3lame,pcm_s16le --enable-muxer=mp3,wav,null \
        --enable-filter=aresample,aformat,anull --enable-swresample \
    && make -j2 && make install

FROM eclipse-temurin:21-jre-noble
RUN apt-get update && apt-get install -y --no-install-recommends libmp3lame0 && rm -rf /var/lib/apt/lists/* \
    && groupadd --gid 10001 hueqr && useradd --uid 10001 --gid hueqr --no-create-home hueqr \
    && mkdir -p /data/audio /app && chown -R hueqr:hueqr /data /app
COPY --from=audio /opt/ffmpeg/bin/ffmpeg /opt/ffmpeg/bin/ffprobe /usr/local/bin/
COPY --from=backend /source/backend/target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar
USER hueqr
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-XX:MaxRAMPercentage=60","-jar","/app/backend.jar"]
