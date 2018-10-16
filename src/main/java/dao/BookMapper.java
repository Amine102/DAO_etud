/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 *
 * @author BLHA
 */
public class BookMapper extends AbstractMapper {

    public static BookMapper getMapper() {
       
        return new BookMapper("BOOK");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BookMapper(String dbName) {
        super(dbName);
    }

    @Override
    protected String insertStatement() {

        String statement = "INSERT INTO BOOK (ISBN,TITLE,AUTHOR,PRICE) VALUES (?,?,?,?)";
        return statement;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String findManyStatement() {
        String stmt = "SELECT * FROM BOOK WHERE AUTHOR = (?)";
        return stmt;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String findStatement() {
        String stmt = "SELECT * FROM BOOK WHERE ISBN = (?)";
        return stmt;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected Set<Book> findManyByAuthor(String aut) throws AppException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (aut == null) {
            throw new AppException("AbstractMapper: findManyByAuthor failed because author is null...");
        }
        try {
            stmt = this.db.prepare(findManyStatement());
            stmt.setString(1, aut);
            rs = stmt.executeQuery();
            //Set<Book> setbook = (Set<Book>) this.; //A faire demain vite!
            return (Set<Book>) (Book) this.loadAll(rs);
        } catch (SQLException e) {
            throw new AppException(e.getMessage());
        }
    }

    @Override
    protected String updateStatement() {
        String stmt = "UPDATE BOOK SET ISBN = (?), TITLE = (?), AUTHOR = (?), PRICE = (?) WHERE ISBN = (?)";
        return stmt;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String deleteStatement() {
        String stmt = "DELETE FROM BOOK WHERE ISBN = (?)";
        return stmt;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String deleteAllStatement() {
        String stmt = "DELETE FROM BOOK";
        return stmt;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doInsert(DomainObject subject, PreparedStatement ps) throws SQLException {
        Book sub = (Book) subject;
        ps.setString(2, sub.getTitle());
        ps.setString(3, sub.getAuthor());
        ps.setDouble(4, sub.getPrice());
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected DomainObject doLoad(ResultSet rs) throws SQLException, AppException {
        String isbn = new String();
        String title = new String();
        String author = new String();
        float price = 0;
        while (rs.next()) {
            isbn = rs.getString(1);
            title = rs.getString(2);
            author = rs.getString(3);
            price = rs.getFloat(4);
        }
        DomainObject domain = new Book(isbn, title, author, price);
        return domain;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void doUpdate(DomainObject subject, PreparedStatement ps) throws SQLException {
        Book sub = (Book) subject;
        ps.setString(2, sub.getTitle());
        ps.setString(3, sub.getAuthor());
        ps.setDouble(4, sub.getPrice());
        ps.setObject(5, sub.getId());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
