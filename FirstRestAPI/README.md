# Repository methods — Task 2.G

`ProductRepository` extends `JpaRepository<Product, Long>`. It therefore inherits method declarations such as `save`, `findById`, `findAll` and `deleteById`.

Spring Data JPA creates a repository proxy at runtime and delegates these operations to its repository implementation. That implementation uses the JPA entity manager and Hibernate to execute database queries.

The interface can remain empty because Spring Data supplies the implementation of the inherited standard methods. `Product` is the entity type and `Long` is the primary key type.
