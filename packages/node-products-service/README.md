# Node migration demo

This minimal Node.js example mirrors the Spring app `GET /api/products` and exposes the same CRUD contract. It's intentionally simple (file-backed JSON store) to demonstrate mapping from controller → service → repository and validation.

Run locally:

```bash
cd packages/node-migration
npm install
npm start
```

API:
- `GET /api/products` - list
- `GET /api/products/:id` - get
- `POST /api/products` - create
- `PUT /api/products/:id` - update
- `DELETE /api/products/:id` - delete
