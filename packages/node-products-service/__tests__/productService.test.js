const productService = require('../src/services/productService');
const productRepo = require('../src/repositories/productRepo');

jest.mock('../src/repositories/productRepo');

describe('Product Service Tests', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('listAll', () => {
    it('should return all products when query is null', () => {
      const mockProducts = [
        { id: 1, name: 'Product 1', price: 10.00 },
        { id: 2, name: 'Product 2', price: 20.00 }
      ];
      productRepo.findAll.mockReturnValue(mockProducts);

      const result = productService.listAll(null);

      expect(result).toEqual(mockProducts);
      expect(productRepo.findAll).toHaveBeenCalledTimes(1);
      expect(productRepo.findByName).not.toHaveBeenCalled();
    });

    it('should return all products when query is empty string', () => {
      const mockProducts = [{ id: 1, name: 'Product 1', price: 10.00 }];
      productRepo.findAll.mockReturnValue(mockProducts);

      const result = productService.listAll('');

      expect(result).toEqual(mockProducts);
      expect(productRepo.findAll).toHaveBeenCalledTimes(1);
    });

    it('should return all products when query is whitespace', () => {
      const mockProducts = [{ id: 1, name: 'Product 1', price: 10.00 }];
      productRepo.findAll.mockReturnValue(mockProducts);

      const result = productService.listAll('   ');

      expect(result).toEqual(mockProducts);
      expect(productRepo.findAll).toHaveBeenCalledTimes(1);
    });

    it('should filter products when query is provided', () => {
      const mockProducts = [{ id: 1, name: 'Laptop', price: 1000.00 }];
      productRepo.findByName.mockReturnValue(mockProducts);

      const result = productService.listAll('Laptop');

      expect(result).toEqual(mockProducts);
      expect(productRepo.findByName).toHaveBeenCalledWith('Laptop');
      expect(productRepo.findAll).not.toHaveBeenCalled();
    });
  });

  describe('get', () => {
    it('should return product by id', () => {
      const mockProduct = { id: 1, name: 'Product 1', price: 10.00 };
      productRepo.findById.mockReturnValue(mockProduct);

      const result = productService.get(1);

      expect(result).toEqual(mockProduct);
      expect(productRepo.findById).toHaveBeenCalledWith(1);
    });

    it('should return undefined for non-existent id', () => {
      productRepo.findById.mockReturnValue(undefined);

      const result = productService.get(999);

      expect(result).toBeUndefined();
      expect(productRepo.findById).toHaveBeenCalledWith(999);
    });
  });

  describe('create', () => {
    it('should create a new product', () => {
      const dto = { name: 'New Product', price: 50.00 };
      const mockCreated = { id: 4, ...dto };
      productRepo.save.mockReturnValue(mockCreated);

      const result = productService.create(dto);

      expect(result).toEqual(mockCreated);
      expect(productRepo.save).toHaveBeenCalledWith(dto);
    });
  });

  describe('update', () => {
    it('should update an existing product', () => {
      const dto = { name: 'Updated Product', price: 75.00 };
      const mockUpdated = { id: 1, ...dto };
      productRepo.update.mockReturnValue(mockUpdated);

      const result = productService.update(1, dto);

      expect(result).toEqual(mockUpdated);
      expect(productRepo.update).toHaveBeenCalledWith(1, dto);
    });

    it('should return null for non-existent product', () => {
      const dto = { name: 'Updated Product', price: 75.00 };
      productRepo.update.mockReturnValue(null);

      const result = productService.update(999, dto);

      expect(result).toBeNull();
      expect(productRepo.update).toHaveBeenCalledWith(999, dto);
    });
  });

  describe('delete', () => {
    it('should delete an existing product', () => {
      productRepo.delete.mockReturnValue(true);

      const result = productService.delete(1);

      expect(result).toBe(true);
      expect(productRepo.delete).toHaveBeenCalledWith(1);
    });

    it('should return false for non-existent product', () => {
      productRepo.delete.mockReturnValue(false);

      const result = productService.delete(999);

      expect(result).toBe(false);
      expect(productRepo.delete).toHaveBeenCalledWith(999);
    });
  });
});
