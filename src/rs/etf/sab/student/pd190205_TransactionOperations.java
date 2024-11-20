/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author USER
 */
public class pd190205_TransactionOperations implements TransactionOperations {

    @Override
    public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select sum(Vrednost) from Transakcija where IdKupac = ?";
        BigDecimal vrednost = BigDecimal.valueOf(-1);
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vrednost = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return vrednost.setScale(3);
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select sum(Vrednost) as 'suma' from Transakcija where IdProdavnica = ?";
        BigDecimal vrednost = new BigDecimal("0");
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vrednost = rs.getBigDecimal(1);
            }
            if (vrednost == null) {
                return new BigDecimal("0").setScale(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return vrednost.setScale(3);
    }

    @Override
    public List<Integer> getTransationsForBuyer(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdTransakcija from Transakcija where IdKupac=?";
        List<Integer> transakcije = new ArrayList<>();
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transakcije.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (transakcije.isEmpty()) {
            return null;
        }
        return transakcije;
    }

    @Override
    public int getTransactionForBuyersOrder(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select IdTransakcija from Transakcija where IdPorudzbina = ? and IdKupac is not null";
        int trans = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                trans = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return trans;
    }

    @Override
    public int getTransactionForShopAndOrder(int orderId, int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select IdTransakcija from Transakcija where IdPorudzbina = ? and IdProdavnica=?";
        int transc = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(2, shopId);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                transc = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return transc;
    }

    @Override
    public List<Integer> getTransationsForShop(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdTransakcija from Transakcija where IdProdavnica=?";
        List<Integer> transakcije = new ArrayList<>();
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transakcije.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (transakcije.isEmpty()) {
            return null;
        }
        return transakcije;
    }

    @Override
    public Calendar getTimeOfExecution(int transactId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select DatumTransakcije from Transakcija where IdTransakcija = ?";
        //pd190205_GeneralOperations go = new pd190205_GeneralOperations();

        Calendar trans = Calendar.getInstance();
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, transactId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getDate(1) != null) {
                    trans.setTime(rs.getDate(1));
                } else {
                    return null;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return trans;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select Vrednost from Transakcija where IdPorudzbina = ?";
        BigDecimal vrednost = BigDecimal.valueOf(-1);
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vrednost = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        if (vrednost == BigDecimal.ZERO) {
            return null;
        }
        return vrednost.setScale(3);
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select Vrednost from Transakcija where IdPorudzbina = ? and IdProdavnica=?";
        BigDecimal vrednost = BigDecimal.valueOf(-1);
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(2, shopId);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vrednost = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        if (vrednost == BigDecimal.ZERO) {
            return null;
        }
        return vrednost.setScale(3);
    }

    @Override
    public BigDecimal getTransactionAmount(int i) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select Vrednost from Transakcija where IdTransakcija = ?";
        BigDecimal vrednost = BigDecimal.valueOf(-1);
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, i);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vrednost = rs.getBigDecimal(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        if (vrednost.equals(BigDecimal.valueOf(-1))) {
            return null;
        }
        return vrednost.setScale(3);
    }

    @Override
    public BigDecimal getSystemProfit() {
        Connection conn = DB.getInstance().getConnection();
        String query = "select sum(Profit) from Porudzbina where Status = 'arrived'";
        BigDecimal vrednost = null;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vrednost = rs.getBigDecimal(1);
            }
            if (vrednost == null) {
                return new BigDecimal("0").setScale(3);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_TransactionOperations.class.getName()).log(Level.SEVERE, null, ex);

        }

        return vrednost.setScale(3);
    }

}
