FROM node:20-bullseye-slim
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci --only=production || npm install --only=production
COPY . .
EXPOSE 4000
CMD ["node","src/index.js"]
