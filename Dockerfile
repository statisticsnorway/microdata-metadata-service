ARG RUST_VERSION=1.80.0
ARG APP_NAME=metadata-service

FROM rust:${RUST_VERSION}-alpine AS build
ARG APP_NAME
WORKDIR /app

RUN apk add --no-cache clang lld musl-dev git binutils
COPY Cargo.toml ./
COPY src ./src
RUN cargo build --release && \
cp ./target/release/$APP_NAME /bin/server && \
strip /bin/server

# Create user
RUN groupadd --gid 180291 microdata \
    && useradd --uid 180291 --gid microdata microdata

FROM gcr.io/distroless/static-debian12
ARG COMMIT_ID
ENV COMMIT_ID=$COMMIT_ID

COPY --from=build /bin/server /bin/
COPY --from=build /etc/passwd /etc/passwd
COPY --from=build /etc/group /etc/group
USER microdata

EXPOSE 3000
CMD ["/bin/server"]
