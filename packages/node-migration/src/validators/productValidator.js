const Joi = require('joi');

const schema = Joi.object({
  name: Joi.string().trim().min(1).required(),
  description: Joi.string().allow('', null),
  price: Joi.number().min(0).required()
});

function validateProduct(obj) {
  return schema.validate(obj, { abortEarly: false });
}

module.exports = { validateProduct };
