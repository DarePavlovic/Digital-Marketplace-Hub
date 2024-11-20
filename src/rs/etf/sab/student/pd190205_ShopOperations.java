/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ShopOperations;

/**
 *
 * @author USER
 */
public class pd190205_ShopOperations implements ShopOperations {

    @Override
    public int createShop(String shopName, String cityName) {
        Connection conn = DB.getInstance().getConnection();
        String query = "insert into Prodavnica (Popust, Naziv, IdGrad) values(?, ?, ?)";
        String query1 = "select IdGrad from Grad where Naziv = ?";
        int shopId = -1;

        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int idGrad = -1;
            try ( PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, cityName);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    idGrad = rs1.getInt("IdGrad");
                }
            } catch (SQLException ex) {
                Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
            ps.setInt(1, 0);
            ps.setString(2, shopName);
            ps.setInt(3, idGrad);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                shopId = rs.getInt(1);
                //System.out.println("Kreirana je nova prodavnica kome je automatski dodeljena IdProdavnica " + rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return shopId;
    }

    @Override
    public int setCity(int shopId, String cityName) {
        Connection conn = DB.getInstance().getConnection();
        String query = "update Prodavnica set IdGrad = ? where IdProdavnica = ?";
        String query1 = "select IdGrad from Grad where Naziv = ?";
        int success = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            int idGrad = -1;
            try ( PreparedStatement ps1 = conn.prepareStatement(query1)) {
                ps1.setString(1, cityName);
                ResultSet rs1 = ps1.executeQuery();
                if (rs1.next()) {
                    idGrad = rs1.getInt("IdGrad");
                }
                //System.out.println(idGrad);
            } catch (SQLException ex) {
                Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

            ps.setInt(1, idGrad);
            ps.setInt(2, shopId);

            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    @Override
    public int getCity(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "Select IdGrad from Prodavnica where IdProdavnica = ?";
        int cityId = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cityId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return cityId;
    }

    @Override
    public int setDiscount(int shopId, int discountPercentage) {
        Connection conn = DB.getInstance().getConnection();
        String query = "update Prodavnica set Popust=? where IdProdavnica = ?";
        int success = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(2, shopId);
            ps.setInt(1, discountPercentage);

            success = ps.executeUpdate();
            if (success == 0) {
                success = -1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return success;
    }

    @Override
    public int increaseArticleCount(int articleId, int increment) {
        Connection conn = DB.getInstance().getConnection();
        String query = "select Kolicina from Artikal where IdArtikal=?";
        int articleCount = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                articleCount = rs.getInt("Kolicina");
            }
            articleCount = articleCount + increment;
            rs.updateInt("Kolicina", articleCount);
            rs.updateRow();
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return articleCount;
    }

    @Override
    public int getArticleCount(int articleId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "Select Kolicina from Artikal where IdArtikal=?";
        int articleCount = 0;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                articleCount = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return articleCount;
    }

    @Override
    public List<Integer> getArticles(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> artikli = new ArrayList<>();
        String query = "select * from Artikal where IdProdavnica = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                artikli.add(rs.getInt("IdArtikal"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return artikli;
    }

    @Override
    public int getDiscount(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "Select Popust from Prodavnica where IdProdavnica = ?";
        int discount = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, shopId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                discount = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        return discount;
    }

}
