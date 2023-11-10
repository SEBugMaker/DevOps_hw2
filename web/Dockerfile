FROM node:19.5.0-alpine as build-stage
# node:19.5.0-alpine
# node:14.18-stretch
WORKDIR /app
COPY package*.json ./
RUN npm install --registry=https://registry.npm.taobao.org
COPY ./ .
RUN NODE_ENV=development npm run build


FROM nginx as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist /app
COPY nginx.conf /etc/nginx/nginx.conf

CMD nginx

