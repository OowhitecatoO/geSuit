package net.cubespace.geSuit.database;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public interface IRepository {
    String[] getTable();

    void registerPreparedStatements(ConnectionHandler connection);

    void checkUpdate();
}
