package banking;

import java.sql.*;

public class SBSDBOperate {
    private static String url;

    public static void setUrl(String url) {
        SBSDBOperate.url = url;
    }

    public static void createNewDatabase() {

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable(String sql) {
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Integer queryMaxId() {
        String sql = "SELECT max(id) as mid FROM card";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            if (rs.next()) {
                return rs.getInt("mid");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static String[] getCardInfo(String number){
        String sql = "SELECT number, pin, balance "
                + "FROM card WHERE number = ?";
        String[] retMas = new String[3];

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1,number);
            ResultSet rs  = pstmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                retMas[0] = rs.getString("number");
                retMas[1] = rs.getString("pin");
                retMas[2] = rs.getString("balance"); // ??? Integer
                count++;
            }
            if (count > 1) {
                throw new DuplicatesException("Duplicate card numbers found!");
            }
        } catch (SQLException | DuplicatesException e) {
            System.out.println(e.getMessage());
        }
        return retMas;
    }

    public static void insertCard(Integer id, String number, String pin, Integer balance) {
        String sql = "INSERT INTO card(id, number, pin, balance) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, number);
            pstmt.setString(3, pin);
            pstmt.setInt(4, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
