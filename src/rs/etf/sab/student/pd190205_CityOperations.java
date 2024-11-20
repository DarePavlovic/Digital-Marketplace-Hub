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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author USER
 */
public class pd190205_CityOperations implements CityOperations {

    @Override
    public int createCity(String string) {
        Connection conn = DB.getInstance().getConnection();
        String query = "insert into Grad (Naziv) values(?)";
        int cityId = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, string);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                cityId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cityId;
    }

    @Override
    public List<Integer> getCities() {
        Connection conn = DB.getInstance().getConnection();
        String query = "SELECT IdGrad from Grad";
        List<Integer> gradovi = new ArrayList<>();
        try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);ResultSet rs = ps.executeQuery();) {
            while(rs.next()){
                gradovi.add(rs.getInt("IdGrad"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gradovi;
    }

    @Override
    public int connectCities(int city1, int city2, int distance) {
        Connection conn = DB.getInstance().getConnection();
        String query = "insert into Linija (Distanca, IdGradOd, IdGradDo) values(?, ?, ?)";
        String provera = "select IdGradOd, IdGradDo from Linija where IdGradOd=? and IdGradDo=? OR IdGradOd=? and IdGradDo=?";
        try (PreparedStatement ps3=conn.prepareStatement(provera)){
            ps3.setInt(1, city1);
            ps3.setInt(2, city2);
            ps3.setInt(3, city2);
            ps3.setInt(4, city1);
            
            ResultSet rs3 = ps3.executeQuery();
            if(rs3.next()){
                System.out.println("Vec postoji takva linija");
                return -1;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);

        }
        int lineId = -1;
        try ( PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, distance);
            ps.setInt(2, city1);
            ps.setInt(3, city2);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                lineId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lineId;
    }

    @Override
    public List<Integer> getConnectedCities(int city) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> gradovi = new ArrayList<>();
        String query1 = "select IdGradDo from Linija where IdGradOd = ?";
        String query2 = "select IdGradOd from Linija where IdGradDo = ?";

       try(PreparedStatement ps1 = conn.prepareStatement(query1, PreparedStatement.RETURN_GENERATED_KEYS);
               PreparedStatement ps2 = conn.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS);) {
           ps1.setInt(1, city);
           ps2.setInt(1, city);
           ResultSet rs1 = ps1.executeQuery();
           while(rs1.next())
               gradovi.add(rs1.getInt(1));
           ResultSet rs2 = ps2.executeQuery();
           while(rs2.next())
               gradovi.add(rs2.getInt(1));
            
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return gradovi;
    }

    @Override
    public List<Integer> getShops(int cityId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> prodavnice = new ArrayList<>();
        String query = "select * from Prodavnica where IdGrad = ?";
       try(PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);) {
           ps.setInt(1, cityId);
           ResultSet rs = ps.executeQuery();
           while(rs.next()){
                prodavnice.add(rs.getInt("IdProdavnica"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
       return prodavnice;
    }
    
}
