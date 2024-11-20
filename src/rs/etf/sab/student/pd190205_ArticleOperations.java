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
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ArticleOperations;

/**
 *
 * @author USER
 */
public class pd190205_ArticleOperations implements ArticleOperations {

    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        Connection conn = DB.getInstance().getConnection();
        int articleId=-1;
        String query = "insert into Artikal (IdProdavnica, Naziv, Cena, Kolicina) values(?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, shopId);
            ps.setString(2, articleName);
            ps.setBigDecimal(3, BigDecimal.valueOf(articlePrice));
            ps.setInt(4, 0);


            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                articleId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return articleId;

    }
    
}
