const fs = require('fs');
const path = require('path');

const dataPath = path.join(__dirname, '..', '..', 'node-data', 'products.json');

function load() {
  const s = fs.readFileSync(dataPath, 'utf8');
  return JSON.parse(s);
}

function save(all) {
  fs.writeFileSync(dataPath, JSON.stringify(all, null, 2));
}

function findAll() {
  return load();
}

function findByName(q) {
  return load().filter(p => p.name.toLowerCase().includes(q.toLowerCase()));
}

function findById(id) {
  return load().find(p => p.id === id);
}

function saveNew(dto) {
  const all = load();
  const next = all.length ? Math.max(...all.map(p => p.id)) + 1 : 1;
  const item = { id: next, ...dto };
  all.push(item);
  save(all);
  return item;
}

function update(id, dto) {
  const all = load();
  const idx = all.findIndex(p => p.id === id);
  if (idx < 0) return null;
  all[idx] = { ...all[idx], ...dto };
  save(all);
  return all[idx];
}

function del(id) {
  const all = load();
  const idx = all.findIndex(p => p.id === id);
  if (idx < 0) return false;
  all.splice(idx, 1);
  save(all);
  return true;
}

module.exports = {
  findAll,
  findByName,
  findById,
  save: saveNew,
  update,
  delete: del,
};
