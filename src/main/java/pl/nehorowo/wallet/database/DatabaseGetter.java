package pl.nehorowo.wallet.database;

import lombok.SneakyThrows;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DatabaseGetter {

    private DatabaseConnector databaseConnector;

    public DatabaseGetter(DatabaseConnector connector) {
        this.databaseConnector = connector;
    }

    public void setDatabaseConnector(DatabaseConnector connector){
        this.databaseConnector = connector;
    }

    @SneakyThrows
    public void createTable(String name, String primaryKey, List<String> arguments) {
        databaseConnector.getConnection().prepareStatement(String.format("CREATE TABLE IF NOT EXISTS `%s` (%s, %s)", name, String.join(", ", arguments), primaryKey)).execute();
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    @SneakyThrows
    public void createOrUpdate(String name, Map<String, Object> values) {
        String s = "insert into {table} ({values}) values ({?}) on duplicate key update {update}";
        StringBuilder a = new StringBuilder();
        StringBuilder b = new StringBuilder();
        StringBuilder c = new StringBuilder();

        int[] id = {1};

        for (String s1 : values.keySet()) {
            a.append(",`").append(s1).append("`");
            b.append(",?");
            c.append(", ").append(s1).append("=values(").append(s1).append(")");
        }

        a = new StringBuilder(a.toString().replaceFirst(",", ""));
        b = new StringBuilder(b.toString().replaceFirst(",", ""));
        c = new StringBuilder(c.toString().replaceFirst(", ", ""));

        s = s.replace("{table}", name)
                .replace("{values}", a.toString())
                .replace("{?}", b.toString())
                .replace("{update}", c.toString());

        PreparedStatement statement = databaseConnector.getConnection().prepareStatement(s);

        for (Object value : values.values()) {
            ObjectParser parser = ObjectParser.getParser(value).orElse(null);
            if(parser == null) {
                try {
                    if(value instanceof Collection) statement.setString(id[0]++, String.join("..//", (Collection)value));
                    else statement.setObject(Math.min(id[0]++, values.size()), value);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    statement.setString(Math.min(id[0]++, values.size()), parser.serialize(parser.getValue()));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        statement.execute();
    }
}