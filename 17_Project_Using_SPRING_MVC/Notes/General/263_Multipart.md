### RequestBody and RequestPart

@RequestBody

Used when the entire HTTP request body contains data (usually JSON).

Client sends
POST /products
Content-Type: application/json

{
  "name": "Laptop",
  "price": 50000
}

@PostMapping("/products")
public Product createProduct(@RequestBody Product product) {
    return service.save(product);
}


@RequestPart

Used with multipart/form-data requests, especially when sending:

Files + JSON together
Multiple parts of different types
Client sends
POST /products
Content-Type: multipart/form-data

Parts:

image = laptop.jpg
product = {
    "name":"Laptop",
    "price":50000
}
Controller
@PostMapping("/products")
public ResponseEntity<?> createProduct(
        @RequestPart("product") Product product,
        @RequestPart("image") MultipartFile image) {

    return ResponseEntity.ok().build();
}