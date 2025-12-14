const express = require('express');
const router = express.Router();
const productService = require('../services/productService');
const { validateProduct } = require('../validators/productValidator');

router.get('/', (req, res) => {
  const q = req.query.q;
  res.json(productService.listAll(q));
});

router.get('/:id', (req, res) => {
  const p = productService.get(Number(req.params.id));
  if (!p) return res.status(404).json({ message: 'Product not found' });
  res.json(p);
});

router.post('/', (req, res, next) => {
  const { error, value } = validateProduct(req.body);
  if (error) return res.status(400).json({ errors: error.details.map(d => d.message) });
  const created = productService.create(value);
  res.status(201).json(created);
});

router.put('/:id', (req, res) => {
  const { error, value } = validateProduct(req.body);
  if (error) return res.status(400).json({ errors: error.details.map(d => d.message) });
  const updated = productService.update(Number(req.params.id), value);
  if (!updated) return res.status(404).json({ message: 'Product not found' });
  res.json(updated);
});

router.delete('/:id', (req, res) => {
  const ok = productService.delete(Number(req.params.id));
  if (!ok) return res.status(404).json({ message: 'Product not found' });
  res.status(204).end();
});

module.exports = router;
