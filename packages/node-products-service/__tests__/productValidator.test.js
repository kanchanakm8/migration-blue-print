const { validateProduct } = require('../src/validators/productValidator');

describe('Product Validator Tests', () => {
  describe('validateProduct', () => {
    it('should validate a valid product', () => {
      const validProduct = {
        name: 'Test Product',
        description: 'Test Description',
        price: 99.99
      };

      const { error, value } = validateProduct(validProduct);

      expect(error).toBeUndefined();
      expect(value).toEqual(validProduct);
    });

    it('should validate product without description', () => {
      const product = {
        name: 'Test Product',
        price: 99.99
      };

      const { error, value } = validateProduct(product);

      expect(error).toBeUndefined();
      expect(value.name).toBe('Test Product');
      expect(value.price).toBe(99.99);
    });

    it('should fail when name is missing', () => {
      const invalidProduct = {
        price: 99.99
      };

      const { error } = validateProduct(invalidProduct);

      expect(error).toBeDefined();
      expect(error.details[0].message).toContain('name');
    });

    it('should fail when name is empty', () => {
      const invalidProduct = {
        name: '',
        price: 99.99
      };

      const { error } = validateProduct(invalidProduct);

      expect(error).toBeDefined();
    });

    it('should fail when price is missing', () => {
      const invalidProduct = {
        name: 'Test Product'
      };

      const { error } = validateProduct(invalidProduct);

      expect(error).toBeDefined();
      expect(error.details[0].message).toContain('price');
    });

    it('should fail when price is negative', () => {
      const invalidProduct = {
        name: 'Test Product',
        price: -10
      };

      const { error } = validateProduct(invalidProduct);

      expect(error).toBeDefined();
      expect(error.details[0].message).toContain('must be greater than or equal to 0');
    });

    it('should accept price as zero', () => {
      const product = {
        name: 'Test Product',
        price: 0
      };

      const { error, value } = validateProduct(product);

      expect(error).toBeUndefined();
      expect(value.price).toBe(0);
    });

    it('should fail when price is not a number', () => {
      const invalidProduct = {
        name: 'Test Product',
        price: 'not a number'
      };

      const { error } = validateProduct(invalidProduct);

      expect(error).toBeDefined();
    });

    it('should trim whitespace from name', () => {
      const product = {
        name: '  Test Product  ',
        price: 99.99
      };

      const { error, value } = validateProduct(product);

      expect(error).toBeUndefined();
      expect(value.name).toBe('Test Product');
    });
  });
});
