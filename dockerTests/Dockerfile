FROM node
WORKDIR /usr/local/node
ADD testing tests
RUN npm install -g artillery
RUN npm install --save-dev faker@5.5.3
RUN npm install -g node-fetch -save
RUN npm install -g https://github.com/preguica/artillery-plugin-metrics-by-endpoint.git