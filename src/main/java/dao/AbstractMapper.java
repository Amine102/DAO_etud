package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements common code to map an object to a row in a relational table
 */
public abstract class AbstractMapper {

    protected Registry<Object, Object> loadedMap;
    protected final DB db;

    /**
     * Init the map and the database
     *
     * @param dbName
     */
    public AbstractMapper(String dbName) {
        try {
            this.loadedMap = new Registry<>();
            this.db = DB.getDB(dbName);
        } catch (Exception e) {
            throw new RuntimeException("AbstractMapper:: Failed to initialize database access: " + e.getMessage());
        }
    }

    /**
     * Returns SQL insert string for domain object
     *
     * @return SQL insert string
     */
    protected abstract String insertStatement();
    
    /**
     * Returns SQL findMany string for domain object
     *
     * @return SQL findMany string
     */
    protected abstract String findManyStatement();

    /**
     * Returns SQL find string for domain object
     *
     * @return SQL find string
     */
    protected abstract String findStatement();

    /**
     * Returns SQL update string for domain object
     *
     * @return SQL update string
     */
    protected abstract String updateStatement();

    /**
     * Returns SQL delete string for domain object
     *
     * @return SQL delete string
     */
    protected abstract String deleteStatement();

    /**
     * Returns SQL delete string for all book objects
     *
     * @return SQL delete string
     */
    protected abstract String deleteAllStatement();

    /**
     * Configure PreparedStatement for domain object
     *
     * @param subject domain object
     * @param ps SQL prepared statement
     * @throws SQLException
     */
    abstract protected void doInsert(DomainObject subject, PreparedStatement ps) throws SQLException;

    /**
     * Load a domain object from a SQL result set
     *
     * @param rs SQL result set
     * @return domain object
     * @throws SQLException
     */
    abstract protected DomainObject doLoad(ResultSet rs) throws SQLException, AppException;

    /**
     * Update a domain object from a SQL result set
     *
     * @param subject domain object
     * @param ps SQL prepared statement
     * @throws SQLException
     */
    abstract protected void doUpdate(DomainObject subject, PreparedStatement ps) throws SQLException;

    /**
     * Insert an domain object in database
     *
     * @param subject domain object
     * @return object identifier (oid)
     * @throws AppException if the object already exists
     */
    protected Object insert(DomainObject subject) throws AppException {
        Object subjectId = subject.getId();
        try {
            PreparedStatement insertStatement = null;
            insertStatement = this.db.prepare(insertStatement());
            if (subjectId == null) {
                throw new AppException("AbstractMapper: insert failed because key is null...");
            }
            insertStatement.setObject(1, subjectId);
            doInsert(subject, insertStatement);
            insertStatement.execute();
        } catch (SQLException e) {
            throw new AppException(e.getMessage());
        }
        return subjectId;
    }

    /**
     * Find an object with its oid
     *
     * @param id object identifier
     * @return the object matching the iod
     * @throws AppException
     */
    protected DomainObject find(Object id) throws AppException {
        if (id == null) {
            throw new AppException("AbstractMapper: find failed because key is null...");
        }
        DomainObject result = (DomainObject) loadedMap.getObject(id);
        if (result != null) {
            return result;
        } else {
            PreparedStatement findStatement = null;
            try {
                findStatement = this.db.prepare(findStatement());
                findStatement.setObject(1, id);
                ResultSet rs = findStatement.executeQuery();
                if (rs.next()) {
                    result = this.load(rs);
                    return result;
                } else {
                    return null;
                }
            } catch (SQLException e) {
                throw new AppException(e.getMessage());
            }
        }
    }

    /**
     * Find the objects matching to SQL query
     *
     * @param id the criteria (key) to retrieve many objects
     * @return the domain objects corresponding to the result of the query
     * @throws AppException
     */
    protected Set<DomainObject> findMany(Object id) throws AppException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (id == null) {
            throw new AppException("AbstractMapper: findMany failed because key is null...");
        }
        try {
            stmt = this.db.prepare(findManyStatement());
            stmt.setObject(1, id);
            rs = stmt.executeQuery();
            return this.loadAll(rs);
        } catch (SQLException e) {
            throw new AppException(e.getMessage());
        }
    }

    /**
     * Update an object to the database
     *
     * @param subject the object to update
     * @throws AppException
     */
    protected void update(DomainObject subject) throws AppException {
        try {
            PreparedStatement updateStatement = null;
            updateStatement = db.prepare(updateStatement());
            updateStatement.setObject(1, subject.getId());
            doUpdate(subject, updateStatement);
            int updatedRows = updateStatement.executeUpdate();
            // if the object to update doesn't exist in database...
            if (updatedRows == 0) {
                throw new AppException("AbstractMapper:: no updated row");
            }
            // update cache (this.loadedMap)
            /* to complete... */
        } catch (SQLException e) {
            throw new AppException(e.getMessage());
        }
    }

    /**
     * Delete an object from database
     *
     * @param subject the object to delete
     * @throws AppException
     */
    protected void delete(DomainObject subject) throws AppException {
        PreparedStatement deleteStatement = null;
        try {
            deleteStatement = this.db.prepare(deleteStatement());
            deleteStatement.setObject(1, subject.getId());
            int deletedRows = deleteStatement.executeUpdate();
            // if the object to remove doesn't exist in database...            
            if (deletedRows == 0) {
                throw new AppException("AbstractMapper:: no deleted row");
            }
            // update cache (this.loadedMap)
            /* to complete... */
        } catch (SQLException e) {
            throw new AppException(e.getMessage());
        }
    }

    /**
     * Delete all objects from database
     *
     * @throws AppException
     */
    protected void deleteAll() throws AppException {
        PreparedStatement deleteStatement = null;
        try {
            deleteStatement = this.db.prepare(deleteAllStatement());
            deleteStatement.executeUpdate();
            // update cache (this.loadedMap)
            /* to complete... */
        } catch (SQLException e) {
            throw new AppException(e.getMessage());
        }
    }

    /**
     * Load all the objects from a result set of SQL query. For each object the
     * load method (below) will be called.
     *
     * @param rs result set
     * @return
     * @throws SQLException
     * @throws AppException
     */
    protected Set<DomainObject> loadAll(ResultSet rs) throws SQLException, AppException {
        Set<DomainObject> resultSet = new HashSet<>();
        while (rs.next()) {
            DomainObject result = this.load(rs);
            resultSet.add(result);
        }
        return resultSet;
    }

    /**
     * Load an object from a result set of SQL query with doLoad method
     * implemented in the subclass
     *
     * @param rs result set
     * @return
     * @throws SQLException
     * @throws AppException
     */
    protected DomainObject load(ResultSet rs) throws SQLException, AppException {
        Object key = rs.getObject(1); // key
        // search in the cache before to create an domain object
        /* remove null and complete... */
        DomainObject result = null;
        if (result != null) {
            return result;
        } else {
            // doLoad is defined in the subclass...
            result = doLoad(rs);
            // update cache (this.loadedMap)
            /* to complete... */
            return result;
        }
    }

}
