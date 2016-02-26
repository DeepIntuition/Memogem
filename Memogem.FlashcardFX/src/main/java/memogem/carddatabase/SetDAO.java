
package memogem.carddatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import memogem.coreapplication.Set;

/**
 *Is responsible for the importing and exporting of Set-objects to and from
 *SQL-database.
 */
public class SetDAO implements Dao<Set> {
    private Connection dbConnection;
    private Statement statement;
    private String dbAddress;

    public SetDAO(String dbAddress) {
        try {
            this.dbAddress = dbAddress;
            this.dbConnection = DriverManager.getConnection("jcdb:sqlite:" + dbAddress + ".db");
            statement = dbConnection.createStatement();
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }
    
    @Override
    public void delete(Set set) throws SQLException {
        String name = set.getId();
        CardDAO cardDao = new CardDAO(dbAddress);
        cardDao.deleteCardsBySetId(name);
        //needs more refinement, how to delete all the cards that have particular SetId
        statement.executeQuery("DELETE FROM Set WHERE Name = '" + name + "';");
        closeConnection();
    }

    @Override
    public void add(Set set) throws SQLException {
        if (set.getLastTimeStudied() != null) {
            statement.executeQuery("INSERT INTO CardSet(Id, Name, LastTimeStudied) VALUES "
                    + "('" + set.getId() + "', "
                    + "'" + set.getName() + "', "
                    + "'" + set.getLastTimeStudied() + "');");
        } else {
            statement.executeQuery("INSERT INTO CardSet(Id, Name) VALUES "
                    + "('" + set.getId() + "', "
                    + "'" + set.getName() + "');");
        }
        closeConnection();
    }

    @Override
    public void update(Set key) throws SQLException {
        //not supported
    }

    @Override
    public List<Set> getAll() throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM Set;");
        List<Set> sets = new ArrayList<>();
        
        while(resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("Name");            
            String dateString = resultSet.getString("LastTimeStudied");
            
            //formatting and extracting date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime locDT = (LocalDateTime) formatter.parse(dateString);
            
            Set queriedSet = new Set(id, name, locDT);
            sets.add(queriedSet);
        }
        return sets;
    }
    
    private void closeConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (SQLException se) {
            System.out.println(se.getMessage());
        }
    }


    
}
