const request = require('supertest');
const app = require('../src/index');
const fs = require('fs');
const path = require('path');

const dataPath = path.join(__dirname, '..', 'node-data', 'products.json');
const backupPath = path.join(__dirname, '..', 'node-data', 'products.backup.json');

describe('Product API Tests', () => {
  let originalData;

  beforeAll(() => {
    // Backup original data
    originalData = fs.readFileSync(dataPath, 'utf8');
    fs.writeFileSync(backupPath, originalData);
  });

  beforeEach(() => {
    // Reset data before each test
    const testData = [
      { id: 1, name: 'Laptop', description: 'Gaming laptop', price: 1200.00 },
      { id: 2, name: 'Mouse', description: 'Wireless mouse', price: 25.50 },
      { id: 3, name: 'Keyboard', description: 'Mechanical keyboard', price: 89.99 }
    ];
    fs.writeFileSync(dataPath, JSON.stringify(testData, null, 2));
  });

  afterAll(() => {
    // Restore original data
    fs.writeFileSync(dataPath, originalData);
    if (fs.existsSync(backupPath)) {
      fs.unlinkSync(backupPath);
    }
  });

  describe('GET /api/products', () => {
    it('should return all products', async () => {
      const res = await request(app).get('/api/products');
      
      expect(res.status).toBe(200);
      expect(res.body).toHaveLength(3);
      expect(res.body[0]).toHaveProperty('id');
      expect(res.body[0]).toHaveProperty('name');
      expect(res.body[0]).toHaveProperty('price');
    });

    it('should filter products by name query', async () => {
      const res = await request(app).get('/api/products?q=Mouse');
      
      expect(res.status).toBe(200);
      expect(res.body).toHaveLength(1);
      expect(res.body[0].name).toBe('Mouse');
    });

    it('should filter products case-insensitively', async () => {
      const res = await request(app).get('/api/products?q=laptop');
      
      expect(res.status).toBe(200);
      expect(res.body).toHaveLength(1);
      expect(res.body[0].name).toBe('Laptop');
    });

    it('should return empty array when no products match', async () => {
      const res = await request(app).get('/api/products?q=NonExistent');
      
      expect(res.status).toBe(200);
      expect(res.body).toHaveLength(0);
    });
  });

  describe('GET /api/products/:id', () => {
    it('should return a product by id', async () => {
      const res = await request(app).get('/api/products/1');
      
      expect(res.status).toBe(200);
      expect(res.body.id).toBe(1);
      expect(res.body.name).toBe('Laptop');
      expect(res.body.price).toBe(1200.00);
    });

    it('should return 404 for non-existent product', async () => {
      const res = await request(app).get('/api/products/999');
      
      expect(res.status).toBe(404);
      expect(res.body).toHaveProperty('message');
      expect(res.body.message).toBe('Product not found');
    });
  });

  describe('POST /api/products', () => {
    it('should create a new product', async () => {
      const newProduct = {
        name: 'Monitor',
        description: '4K Monitor',
        price: 399.99
      };

      const res = await request(app)
        .post('/api/products')
        .send(newProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(201);
      expect(res.body).toHaveProperty('id');
      expect(res.body.name).toBe('Monitor');
      expect(res.body.price).toBe(399.99);
      expect(res.body.description).toBe('4K Monitor');
    });

    it('should return 400 for missing name', async () => {
      const invalidProduct = {
        price: 100.00
      };

      const res = await request(app)
        .post('/api/products')
        .send(invalidProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(400);
      expect(res.body).toHaveProperty('errors');
    });

    it('should return 400 for missing price', async () => {
      const invalidProduct = {
        name: 'Test Product'
      };

      const res = await request(app)
        .post('/api/products')
        .send(invalidProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(400);
      expect(res.body).toHaveProperty('errors');
    });

    it('should return 400 for negative price', async () => {
      const invalidProduct = {
        name: 'Test Product',
        price: -10
      };

      const res = await request(app)
        .post('/api/products')
        .send(invalidProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(400);
      expect(res.body).toHaveProperty('errors');
    });

    it('should return 400 for empty name', async () => {
      const invalidProduct = {
        name: '',
        price: 100
      };

      const res = await request(app)
        .post('/api/products')
        .send(invalidProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(400);
      expect(res.body).toHaveProperty('errors');
    });
  });

  describe('PUT /api/products/:id', () => {
    it('should update an existing product', async () => {
      const updatedProduct = {
        name: 'Updated Laptop',
        description: 'Updated description',
        price: 1500.00
      };

      const res = await request(app)
        .put('/api/products/1')
        .send(updatedProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(200);
      expect(res.body.id).toBe(1);
      expect(res.body.name).toBe('Updated Laptop');
      expect(res.body.price).toBe(1500.00);
    });

    it('should return 404 for non-existent product', async () => {
      const updatedProduct = {
        name: 'Updated Product',
        price: 100.00
      };

      const res = await request(app)
        .put('/api/products/999')
        .send(updatedProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(404);
      expect(res.body.message).toBe('Product not found');
    });

    it('should return 400 for invalid data', async () => {
      const invalidProduct = {
        name: '',
        price: -50
      };

      const res = await request(app)
        .put('/api/products/1')
        .send(invalidProduct)
        .set('Content-Type', 'application/json');
      
      expect(res.status).toBe(400);
      expect(res.body).toHaveProperty('errors');
    });
  });

  describe('DELETE /api/products/:id', () => {
    it('should delete an existing product', async () => {
      const res = await request(app).delete('/api/products/1');
      
      expect(res.status).toBe(204);
      
      // Verify product is deleted
      const getRes = await request(app).get('/api/products/1');
      expect(getRes.status).toBe(404);
    });

    it('should return 404 for non-existent product', async () => {
      const res = await request(app).delete('/api/products/999');
      
      expect(res.status).toBe(404);
      expect(res.body.message).toBe('Product not found');
    });
  });

  describe('Integration Tests', () => {
    it('should create, update, and delete a product', async () => {
      // Create
      const createRes = await request(app)
        .post('/api/products')
        .send({ name: 'Test Product', description: 'Test', price: 50.00 })
        .set('Content-Type', 'application/json');
      
      expect(createRes.status).toBe(201);
      const productId = createRes.body.id;

      // Update
      const updateRes = await request(app)
        .put(`/api/products/${productId}`)
        .send({ name: 'Updated Test', description: 'Updated', price: 75.00 })
        .set('Content-Type', 'application/json');
      
      expect(updateRes.status).toBe(200);
      expect(updateRes.body.name).toBe('Updated Test');

      // Delete
      const deleteRes = await request(app).delete(`/api/products/${productId}`);
      expect(deleteRes.status).toBe(204);

      // Verify deletion
      const getRes = await request(app).get(`/api/products/${productId}`);
      expect(getRes.status).toBe(404);
    });

    it('should handle multiple product creations correctly', async () => {
      const products = [
        { name: 'Product A', price: 10.00 },
        { name: 'Product B', price: 20.00 },
        { name: 'Product C', price: 30.00 }
      ];

      for (const product of products) {
        const res = await request(app)
          .post('/api/products')
          .send(product)
          .set('Content-Type', 'application/json');
        
        expect(res.status).toBe(201);
        expect(res.body.name).toBe(product.name);
      }

      const listRes = await request(app).get('/api/products');
      expect(listRes.status).toBe(200);
      expect(listRes.body.length).toBeGreaterThanOrEqual(6);
    });
  });
});
