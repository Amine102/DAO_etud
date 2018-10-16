package dao;

/**
 * Methods to be implemented by domain class
 */
public interface DomainObject {
    /**
     * Get the object identifier (oid) (key in relational model)
     * @return object identifier
     */
    Object getId();

    /**
     * Set the object identifier
     * @param id object identifier
     */
    void setId(Object id);

}
