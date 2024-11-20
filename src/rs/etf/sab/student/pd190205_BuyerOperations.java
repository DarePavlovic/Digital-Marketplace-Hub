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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.BuyerOperations;

/**
 *
 * @author USER
 */
public class pd190205_BuyerOperations implements BuyerOperations {

    @Override
    public int createBuyer(String name, int cityId) {
        Connection conn = DB.getInstance().getConnection();
        int buyerId = -1;
        String query1 = "select * from Grad where IdGrad = ? ";
        try ( PreparedStatement prepstmt = conn.prepareStatement(query1)) {
            prepstmt.setInt(1, cityId);
            ResultSet resSet = prepstmt.executeQuery();
            if (!resSet.next()) {
                System.out.println("Ne postoji grad sa ovim id-em!!!");
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "insert into Kupac (StanjeRacuna, ImeKupca, IdGrad) values(?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, BigDecimal.ZERO);
            ps.setString(2, name);
            ps.setInt(3, cityId);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                buyerId = rs.getInt(1);
                System.out.println("Kreirana je nov Kupac kome je automatski dodeljen IdKupac " + rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buyerId;

    }

    @Override
    public int setCity(int buyerId, int cityId) {
        Connection conn = DB.getInstance().getConnection();
        int uspesnost = -1;
        //provera da li postoji kupac
        String query1 = "select * from Kupac where IdKupac = ? ";
        try ( PreparedStatement prepstmt = conn.prepareStatement(query1,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
            prepstmt.setInt(1, buyerId);
            ResultSet resSet = prepstmt.executeQuery();
            if (resSet.next()) {
                resSet.updateInt("IdGrad", cityId);
                resSet.updateRow();
            }
            System.out.println("Izmenjeno je grad kupca");
            uspesnost = 1;
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return uspesnost;
    }

    @Override
    public int getCity(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        int cityId = -1;
        //provera da li postoji kupac
        String query1 = "select * from Kupac where IdKupac = ? ";
        try ( PreparedStatement prepstmt = conn.prepareStatement(query1)) {
            prepstmt.setInt(1, buyerId);
            ResultSet resSet = prepstmt.executeQuery();
            if (!resSet.next()) {
                //System.out.println("Ne postoji kupac sa ovim id-em!!!");
                return -1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        //vraca id grada od kupca
        String query = "select IdGrad from Kupac where IdKupac = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, buyerId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    cityId = rs.getInt("IdGrad");
                    //System.out.println("IdGrada za " + buyerId + " je : " + rs.getInt("IdGrad"));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cityId;
    }

    @Override
    public BigDecimal increaseCredit(int buyerId, BigDecimal credit) {
        Connection conn = DB.getInstance().getConnection();
        BigDecimal totalCredit = BigDecimal.ZERO;

        //provera da li postoji kupac
        String query1 = "select * from Kupac where IdKupac = ? ";
        try ( PreparedStatement prepstmt = conn.prepareStatement(query1,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
            prepstmt.setInt(1, buyerId);
            ResultSet resSet = prepstmt.executeQuery();
            if (resSet.next()) {
                totalCredit = resSet.getBigDecimal("StanjeRacuna");
                //System.out.println("Stanje Racuna je " + totalCredit);

                totalCredit = totalCredit.add(credit);
                resSet.updateBigDecimal("StanjeRacuna", totalCredit);
                resSet.updateRow();
                //System.out.println("Izmenjeno je stanje racuna kupca, cerdit je " + credit + " a ukupno je " + totalCredit);

            }
            //System.out.println("Izmenjeno je stanje racuna kupca, sada je " + totalCredit);
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return totalCredit;
    }

    @Override
    public int createOrder(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        int orderId = -1;

        String query = "insert into Porudzbina (Status, IdKupac, UkupnaCena, VremePoslato, VremePrimljeno) values(?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "created");
            ps.setInt(2, buyerId);
            ps.setBigDecimal(3, BigDecimal.ZERO);
            ps.setDate(4, null);
            ps.setDate(5, null);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                orderId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orderId;
    }

    @Override
    public List<Integer> getOrders(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> porudzbine = new ArrayList<>();
        String query = "select * from Porudzbina where IdKupac = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
            ps.setInt(1, buyerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                porudzbine.add(rs.getInt("IdPorudzbina"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return porudzbine;
    }

    @Override
    public BigDecimal getCredit(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        BigDecimal credit = BigDecimal.valueOf(-1);

        //provera da li postoji kupac
        String query1 = "select * from Kupac where IdKupac = ? ";
        try ( PreparedStatement prepstmt = conn.prepareStatement(query1)) {
            prepstmt.setInt(1, buyerId);
            ResultSet resSet = prepstmt.executeQuery();
            if (!resSet.next()) {
                //System.out.println("Ne postoji kupac sa ovim id-em!!!");
                return credit;
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        String query = "select StanjeRacuna from Kupac where IdKupac = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, buyerId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    credit = rs.getBigDecimal("StanjeRacuna");
                    //System.out.println("Stanje racuna za "+ buyerId + " je : " + rs.getBigDecimal("StanjeRacuna"));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_BuyerOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return credit;
    }

}
