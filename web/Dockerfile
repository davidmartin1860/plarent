# syntax=docker/dockerfile:1

FROM node:22-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

FROM node:22-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
# API_URL is only needed at runtime (Server Components/Actions resolve it per-request),
# so no build-time ARG is required here.
RUN npm run build

FROM node:22-alpine AS runtime
WORKDIR /app
ENV NODE_ENV=production
RUN addgroup -S plarent && adduser -S plarent -G plarent

COPY --from=build /app/public ./public
COPY --from=build --chown=plarent:plarent /app/.next/standalone ./
COPY --from=build --chown=plarent:plarent /app/.next/static ./.next/static

USER plarent
EXPOSE 3000
ENV PORT=3000
ENTRYPOINT ["node", "server.js"]
