/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author USER
 */
public class pd190205_GeneralOperations implements GeneralOperations {

    public static Calendar cal = Calendar.getInstance();

    @Override
    public void setInitialTime(Calendar clndr) {
        cal = (Calendar) clndr.clone();
    }

    @Override
    public Calendar time(int i) {
        Connection conn = DB.getInstance().getConnection();
        while (i > 0) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            i--;
            String query = "select * from Porudzbina where Status='sent'";
            try ( PreparedStatement ps = conn.prepareStatement(query,
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int idPor = rs.getInt("IdPorudzbina");
                    Calendar vprimlj = Calendar.getInstance();
                    vprimlj.setTime(rs.getDate("VremePrimljeno"));

                    if (vprimlj.compareTo(getCurrentTime()) <= 0) {
                        rs.updateString("Status", "arrived");
                        pd190205_OrderOperations order = new pd190205_OrderOperations();
                        BigDecimal finalSum = order.getFinalPrice(idPor);
                        Double disc = order.DiscountDays(idPor);
                        if (disc == 1.0) {
                            rs.updateBigDecimal("Profit", finalSum.multiply(BigDecimal.valueOf(0.05)));
                        } else {
                            rs.updateBigDecimal("Profit", finalSum.multiply(BigDecimal.valueOf(0.03)));
                        }

                        List<Integer> artikli = order.getItems(idPor);
                        String insertTShop = "insert into Transakcija (IdPorudzbina, Vrednost, DatumTransakcije, IdProdavnica) values (?,?,?,?)";
                        String artik = "select A.IdProdavnica, A.Cena, la.Kolicina, p.Popust from Artikal A right join ListaArtikala la on A.IdArtikal=la.IdArtikal join Prodavnica p on p.IdProdavnica=A.IdProdavnica where A.IdArtikal=? ";
                        String prodavnica = "select * from Transakcija where IdProdavnica=? and IdPorudzbina=?";
                        for (Integer art : artikli) {
                            try ( PreparedStatement ps1 = conn.prepareStatement(artik)) {
                                ps1.setInt(1, art);
                                ResultSet rs1 = ps1.executeQuery();
                                if (rs1.next()) {
                                    BigDecimal ukupn = rs1.getBigDecimal(2).multiply(BigDecimal.valueOf(rs1.getInt(3)));
                                    double popust = 100.0 - rs1.getInt(4);
                                    popust = popust / 100;
                                    ukupn = ukupn.multiply(BigDecimal.valueOf(popust));

                                    try ( PreparedStatement ps3 = conn.prepareStatement(prodavnica,
                                            ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
                                        ps3.setInt(1, rs1.getInt(1));
                                        ps3.setInt(2, idPor);
                                        ResultSet rs3 = ps3.executeQuery();
                                        if (rs3.next()) {
                                            BigDecimal bd = rs3.getBigDecimal("Vrednost");

                                            if (disc == 1.0) {
                                                ukupn = ukupn.multiply(BigDecimal.valueOf(0.95));
                                                rs3.updateBigDecimal("Vrednost", bd.add(ukupn));
                                            } else {
                                                ukupn = ukupn.multiply(BigDecimal.valueOf(0.97));
                                                rs3.updateBigDecimal("Vrednost", bd.add(ukupn));
                                            }
                                            rs3.updateRow();
                                        } else {
                                            try ( PreparedStatement ps2 = conn.prepareStatement(insertTShop)) {
                                                ps2.setInt(1, idPor);
                                                if (disc == 1.0) {
                                                    ps2.setBigDecimal(2, ukupn.multiply(BigDecimal.valueOf(0.95)));
                                                } else {
                                                    ps2.setBigDecimal(2, ukupn.multiply(BigDecimal.valueOf(0.97)));
                                                }

                                                ps2.setDate(3, rs.getDate("VremePrimljeno"));
                                                ps2.setInt(4, rs1.getInt(1));
                                                ps2.executeUpdate();
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        rs.updateRow();
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return cal;
    }

    @Override
    public Calendar getCurrentTime() {
        return cal;
    }

    @Override
    public void eraseAll() {
        Connection conn = DB.getInstance().getConnection();

        String query = "delete from ListaArtikala";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Artikal";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Transakcija";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "delete from Porudzbina";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Kupac";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "delete from Linija";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        query = "delete from Prodavnica";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        query = "delete from Grad";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_GeneralOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
