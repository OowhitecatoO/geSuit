package net.cubespace.geSuit.database.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.cubespace.geSuit.core.GlobalPlayer;
import net.cubespace.geSuit.core.objects.WarnInfo;
import net.cubespace.geSuit.core.util.Utilities;
import net.cubespace.geSuit.database.BaseRepository;
import net.cubespace.geSuit.database.ConnectionHandler;
import net.cubespace.geSuit.database.ConnectionPool;
import net.cubespace.geSuit.database.StatementKey;

public class WarnHistory extends BaseRepository {
    private StatementKey insertWarn;
    private StatementKey history;
    private StatementKey activeHistory;
    
    public WarnHistory(String name, ConnectionPool pool) {
        super(name, pool);
    }
    
    @Override
    protected String getTableDeclaration() {
        return "`id` INTEGER AUTO_INCREMENT PRIMARY KEY, `who` VARCHAR(32) NOT NULL, `reason` VARCHAR(255), `by_name` VARCHAR(20) NOT NULL, `by_uuid` CHAR(32), `date` DATETIME NOT NULL, `expire_date` DATETIME, INDEX (`who`)";
    }

    @Override
    public void registerStatements() {
        insertWarn = registerStatement("insertWarn", "INSERT INTO `" + getName() + "` VALUES (DEFAULT,?,?,?,?,?,?);");
        history = registerStatement("getHistory", "SELECT * FROM `" + getName() + "` WHERE `who`=? ORDER BY `date` ASC;");
        activeHistory = registerStatement("getActiveHistory", "SELECT * FROM `" + getName() + "` WHERE `who`=? AND `expire_date` > NOW() ORDER BY `date` ASC;");
    }

    public void recordWarn(WarnInfo warn) throws SQLException {
        ConnectionHandler con = getConnection();
        
        try {
            con.executeUpdate(insertWarn, 
                    Utilities.toString(warn.getWho().getUniqueId()),
                    warn.getReason(),
                    warn.getBy(),
                    (warn.getById() != null ? Utilities.toString(warn.getById()) : null),
                    new Timestamp(warn.getDate()),
                    new Timestamp(warn.getExpireDate())
                    );
        } finally {
            con.release();
        }
    }
    
    public List<WarnInfo> getWarnHistory(GlobalPlayer player) throws SQLException {
        return getHistory(player, history);
    }
    
    public List<WarnInfo> getActiveWarnings(GlobalPlayer player) throws SQLException {
        return getHistory(player, activeHistory);
    }
    
    private List<WarnInfo> getHistory(GlobalPlayer player, StatementKey statement) throws SQLException {
        ConnectionHandler con = getConnection();
        
        try {
            ResultSet results = con.executeQuery(statement, 
                    Utilities.toString(player.getUniqueId())
                    );
            
            List<WarnInfo> warnings = Lists.newArrayList();
            
            while(results.next()) {
                String reason = results.getString("reason");
                String byName = results.getString("by_name");
                UUID byId = null;
                if (results.getString("by_uuid") != null) {
                    byId = Utilities.makeUUID(results.getString("by_uuid"));
                }
                
                long date = results.getTimestamp("date").getTime();
                long expireDate = results.getTimestamp("expire_date").getTime();
                
                warnings.add(new WarnInfo(player, reason, byName, byId, date, expireDate));
            }
            
            results.close();
            return warnings;
        } finally {
            con.release();
        }
    }
}