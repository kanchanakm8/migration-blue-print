const express = require('express');
const productsRouter = require('./routes/products');
const errorHandler = require('./middleware/errorHandler');

const app = express();
app.use(express.json());

app.use('/api/products', productsRouter);

app.use(errorHandler);

const port = process.env.PORT || 4000;
if (require.main === module) {
  app.listen(port, () => console.log(`Node migration app listening on ${port}`));
}

module.exports = app;
