
package memogem.carddatabase;

import java.sql.*;

/**
 *Class creates a new empty SQL Database at the first time
 *the application is executed. 
 */
public class InitDb {
    private Connection dbConnection;
    private Statement statement;
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    
    public InitDb(String dbAddress) {
        try {
            this.dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dbAddress + "");
            this.statement = dbConnection.createStatement();
            createTables();
            closeStatementAndConnection();
        } catch (SQLException se) {
            System.out.println(se.getMessage() + " Cannot initialize SQL Connection.");
        }
    }
    
    /**
     * Method creates all the necessary SQL tables that are 
     * required for MemoGem to be able to use database effectively.
     */
    private void createTables() {
        try {
            String sqlqueryCardSet = "CREATE TABLE CardSet("
                    + "id varchar(36) NOT NULL, "
                    + "Name varchar(150) NOT NULL UNIQUE, "
                    + "LastTimeStudied datetime, "
                    + "PRIMARY KEY (id));";

            String sqlqueryTag = "CREATE TABLE Tag("
                    + "Name varchar(50) NOT NULL, "
                    + "PRIMARY KEY (Name));";

            String sqlqueryCard = "CREATE TABLE Card("
                    + "id varchar(36) NOT NULL, "
                    + "CardSetId integer NOT NULL, "
                    + "Front varchar(2000), "
                    + "Back varchar(2000), "
                    + "CardType integer NOT NULL, "
                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (CardSetId) REFERENCES CardSet(id) ON DELETE CASCADE);";

            String sqlqueryCardTag = "CREATE TABLE CardTag("
                    + "id integer, "
                    + "CardId varchar(36) NOT NULL, "
                    + "TagName varchar(50) NOT NULL, "
                    + "PRIMARY KEY (id), "
                    + "FOREIGN KEY (TagName) REFERENCES Tag(Name) ON DELETE CASCADE, "
                    + "FOREIGN KEY (CardId) REFERENCES Card(id) ON DELETE CASCADE);";

            String sqlqueryStudySpeed = "CREATE TABLE StudySpeed("
                    + "ssid integer, "
                    + "CardId varchar(36) NOT NULL, "
                    + "Speed integer NOT NULL, "
                    + "StudyDate datetime NOT NULL, "
                    + "StudyDifficulty integer NOT NULL, "
                    + "PRIMARY KEY (ssid), "
                    + "FOREIGN KEY (CardId) REFERENCES Card(id) ON DELETE CASCADE);";
            
            statement.executeUpdate("PRAGMA foreign_keys = ON;");
            statement.executeUpdate(sqlqueryCardSet);
            statement.executeUpdate(sqlqueryTag);
            statement.executeUpdate(sqlqueryCard);
            statement.executeUpdate(sqlqueryCardTag);
            statement.executeUpdate(sqlqueryStudySpeed);
            closeStatementAndConnection();
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return dbConnection;
    }

    private void closeStatementAndConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            
        } catch (SQLException e) {
            System.out.println("Couldn't close statement.");;
        }
        try {
            if (dbConnection != null) {
                dbConnection.close();
            }
            
        } catch (SQLException e) {
            System.out.println("Couldn't close connection.");;
        }
    }
    
}
