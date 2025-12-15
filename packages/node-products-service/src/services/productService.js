const repo = require('../repositories/productRepo');

function listAll(q) {
  if (!q || q.trim() === '') return repo.findAll();
  return repo.findByName(q);
}

function get(id) {
  return repo.findById(id);
}

function create(dto) {
  return repo.save(dto);
}

function update(id, dto) {
  return repo.update(id, dto);
}

function del(id) {
  return repo.delete(id);
}

module.exports = { listAll, get, create, update, delete: del };
