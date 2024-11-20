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
import java.sql.Date;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.OrderOperations;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author USER
 */
public class pd190205_OrderOperations implements OrderOperations {

    //public pd190205_GeneralOperations go;
    @Override
    public int addArticle(int orderId, int articleId, int Kolicina) {
        Connection conn = DB.getInstance().getConnection();
        int kolic = -1;
        String query1 = "Select Kolicina from Artikal where IdArtikal = ? and Kolicina >= ?";
        try ( PreparedStatement ps = conn.prepareStatement(query1)) {
            ps.setInt(1, articleId);
            ps.setInt(2, Kolicina);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                kolic = rs.getInt(1);   //ukupna kolicina artikala
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (kolic < 0) {
            return -1;
        }
        String query2 = "Select * from ListaArtikala where IdArtikal = ?";
        String query = "insert into ListaArtikala (Kolicina, IdArtikal, IdPorudzbina) values(?, ?, ?)";
        int listaId = -1;
        //provera kad ubacis artikal iz iste prodavnice ponovo da ti samo update kolicinu ukupnu
        try ( PreparedStatement ps = conn.prepareStatement((query2), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
            ps.setInt(1, articleId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int kol = rs.getInt("Kolicina");
                if (kol + Kolicina >= kolic) {
                    kol = kolic;
                } else {
                    kol = kol + Kolicina;
                }
                rs.updateInt("Kolicina", kol);  //kolicina u listi artikala
                rs.updateRow();
                listaId = rs.getInt("IdListaArtikala");
            } else {

                try ( PreparedStatement ps1 = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps1.setInt(1, Kolicina);
                    ps1.setInt(2, articleId);
                    ps1.setInt(3, orderId);
                    ps1.executeUpdate();
                    ResultSet rs1 = ps1.getGeneratedKeys();
                    if (rs1.next()) {
                        listaId = rs1.getInt(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listaId;
    }

    @Override
    public int removeArticle(int orderId, int articleId) {
        Connection conn = DB.getInstance().getConnection();
        String query = "delete from ListaArtikala where IdArtikal=? and IdPorudzbina=? ";
        int success = -1;
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, articleId);
            stmt.setInt(2, orderId);
            success = stmt.executeUpdate();
            if (success > 0) {
                success = 1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return success;
    }

    @Override
    public List<Integer> getItems(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> artikli = new ArrayList<>();
        String query = "select IdArtikal from ListaArtikala where IdPorudzbina = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                artikli.add(rs.getInt(1));
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return artikli;
    }

    @Override
    public int completeOrder(int orderId) {
        pd190205_GeneralOperations go = new pd190205_GeneralOperations();
        Connection conn = DB.getInstance().getConnection();
        Date vreme = null;
        Date v2 = null;
        int kolArtikla = -1;
        //oduzima kolicinu izabranih artikla iz Tabele Artikal
        String query = "select * from ListaArtikala where IdPorudzbina = ?";
        String queryKolicna = "select * from Artikal where IdArtikal = ?";
        try ( PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                try ( PreparedStatement ps1 = conn.prepareStatement(queryKolicna, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
                    ps1.setInt(1, rs.getInt("IdArtikal"));
                    ResultSet rs1 = ps1.executeQuery();
                    if (rs1.next()) {
                        if (rs1.getInt("Kolicina") >= rs.getInt("Kolicina")) {
                            rs1.updateInt("Kolicina", rs1.getInt("Kolicina") - rs.getInt("Kolicina"));
                            rs1.updateRow();
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            kolArtikla = 1;
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        String queryDatum = "select * from Porudzbina where IdPorudzbina = ?";
        try ( PreparedStatement ps = conn.prepareStatement(queryDatum, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.updateString("Status", "sent");
                vreme = new Date(go.getCurrentTime().getTimeInMillis());
                rs.updateDate("VremePoslato", vreme);
                Calendar curr = (Calendar) go.getCurrentTime().clone();
                curr.add(Calendar.DAY_OF_MONTH, getTransportTime(orderId));
                v2 = new Date(curr.getTimeInMillis());
                rs.updateDate("VremePrimljeno", v2);
                rs.updateBigDecimal("UkupnaCena", getFinalPrice(orderId));

                rs.updateRow();
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        //zapoceti -> Transakcija Kupac
        String queryTransakcijaKupac = "select * from Kupac where IdKupac = ? and StanjeRacuna>=?";
        try ( PreparedStatement ps10 = conn.prepareStatement(queryTransakcijaKupac, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)) {
            ps10.setInt(1, getBuyer(orderId));
            BigDecimal ukupno = getFinalPrice(orderId);
            ps10.setBigDecimal(2, ukupno);
            ResultSet rs = ps10.executeQuery();
            if (rs.next()) {
                BigDecimal suma = rs.getBigDecimal("StanjeRacuna");
                suma = suma.subtract(ukupno);
                rs.updateBigDecimal("StanjeRacuna", suma);
                rs.updateRow();
                String queryTKupac = "insert into Transakcija (IdPorudzbina, Vrednost, DatumTransakcije, IdKupac) values (?,?,?,?)";
                try ( PreparedStatement ps = conn.prepareStatement(queryTKupac)) {
                    ps.setInt(1, orderId);
                    ps.setBigDecimal(2, ukupno);
                    ps.setDate(3, vreme);
                    ps.setInt(4, getBuyer(orderId));
                    ps.executeUpdate();
                } catch (SQLException ex) {
                    Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Transakcija->Prodavnice
        //Transakcija->Sistem
        return kolArtikla;
    }

    @Override
    public BigDecimal getFinalPrice(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        BigDecimal price = BigDecimal.valueOf(-1);
        String query = "select sum(A.Cena*L.Kolicina*(100-p.Popust)/100) from Artikal A right join ListaArtikala L on A.IdArtikal=L.IdArtikal join Prodavnica p on p.IdProdavnica=a.IdProdavnica where L.IdPorudzbina=?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    price = rs.getBigDecimal(1).setScale(3);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        return price.multiply(BigDecimal.valueOf(DiscountDays(orderId))).setScale(3);
    }

    public Double DiscountDays(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        int kupac = getBuyer(orderId);
        Double discount = 1.0;
        Double iznos = 0.0;
        pd190205_GeneralOperations go = new pd190205_GeneralOperations();
        Calendar calendar = (Calendar) go.getCurrentTime().clone();
        String proveraPopust = "SELECT sum([prodajaArtikala].[dbo].[Transakcija].Vrednost) FROM [prodajaArtikala].[dbo].[Transakcija] where DatumTransakcije>? and IdKupac =?";
        try ( PreparedStatement stmt = conn.prepareStatement(proveraPopust);) {

            //System.out.println(calendar.getTime().toString());
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            //System.out.println(calendar.getTime().toString());

            stmt.setDate(1, new Date(calendar.getTimeInMillis()));
            stmt.setInt(2, kupac);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    iznos = rs.getDouble(1);
                    if (iznos > 10000.0) {
                        discount = 0.98;
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return discount;
    }

    @Override
    public BigDecimal getDiscountSum(int orderId) {
        String query = "select sum(A.Cena*L.Kolicina*(p.Popust)/100) from Artikal A right join ListaArtikala L on A.IdArtikal=L.IdArtikal join Prodavnica p on p.IdProdavnica=a.IdProdavnica where L.IdPorudzbina=?";
        Connection conn = DB.getInstance().getConnection();
        BigDecimal price = BigDecimal.valueOf(-1);
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    price = rs.getBigDecimal(1).setScale(3);;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return price;
    }

    @Override
    public String getState(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String state = "";
        String query = "select Status from Porudzbina where IdPorudzbina = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    state = rs.getString(1);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return state;
    }

    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    @Override
    public Calendar getSentTime(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        pd190205_GeneralOperations go = new pd190205_GeneralOperations();
        Calendar sent = Calendar.getInstance();
        String query = "select VremePoslato from Porudzbina where IdPorudzbina = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    Date d = rs.getDate("VremePoslato");
                    if (d != null) {
                        sent.setTime(d);
                        return sent;
                    } else {
                        return null;
                    }

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sent;
    }

    @Override
    public Calendar getRecievedTime(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        //Calendar recieved = null;
        pd190205_GeneralOperations go = new pd190205_GeneralOperations();
        Calendar recieved = Calendar.getInstance();
        //Calendar time = (Calendar)go.getCurrentTime().clone();
        //System.out.println("Time is :" +time.toString());
        String status = "";
        String query = "select VremePrimljeno, Status from Porudzbina where IdPorudzbina = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    if (rs.getDate(1) != null) {
                        recieved.setTime(rs.getDate(1));
                        status = rs.getString(2);
                        System.out.println("Status: " + status);
                    } else {
                        return null;
                    }

                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (status.equals("sent")) {
            return null;
        }
        if (status.equals("arrived")) {
            return recieved;
        }
        return null;
    }

    @Override
    public int getBuyer(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        int buyer = -1;
        String query = "select IdKupac from Porudzbina where IdPorudzbina = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    buyer = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return buyer;
    }

    @Override
    public int getLocation(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        int buyerId = (new pd190205_OrderOperations().getBuyer(orderId));
        List<Integer> gradovi = new ArrayList<>();

        String query5 = "select distinct(P.IdGrad) from Prodavnica P join Artikal A on A.IdProdavnica=P.IdProdavnica join ListaArtikala la on la.IdArtikal=A.IdArtikal where la.IdPorudzbina=?";
        try ( PreparedStatement stmt = conn.prepareStatement(query5);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    gradovi.add(rs.getInt(1));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        int cityBuyerId = -1;
        String query = "select IdGrad from Kupac where IdKupac = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, buyerId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    cityBuyerId = rs.getInt(1);
                } else {
                    return -1;
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        int cityShopId = -1;

        String query1 = "select IdGrad from Prodavnica where IdGrad = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query1);) {
            stmt.setInt(1, cityBuyerId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    cityShopId = rs.getInt(1);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        //dodavanje u graf
        List<Edge> edges = new ArrayList<>();
        String query2 = "select * from Linija";
        try ( PreparedStatement stmt = conn.prepareStatement(query2);) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Edge e = new Edge(rs.getInt("IdGradOd"), rs.getInt("IdGradDo"), rs.getInt("Distanca"));
                edges.add(e);
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        pd190205_GeneralOperations go = new pd190205_GeneralOperations();
        Calendar time = (Calendar) go.getCurrentTime().clone();
        //System.out.println(time.getTime().toString());
        Calendar cal = (Calendar) getSentTime(orderId).clone();
        //System.out.println(cal.getTime().toString());
//        cal.add(Calendar.DAY_OF_MONTH, getTransportTime(orderId));
//        System.out.println(cal.getTime().toString());
        long proslo = -1 * ChronoUnit.DAYS.between(time.toInstant(), cal.toInstant());
        //Calendar sentTime = getSentTime(orderId);
        // proslo =  ChronoUnit.DAYS.between(sentTime.toInstant(), time.toInstant());

        //System.out.println("Proslo : " + proslo);
        if (cityShopId < 0) {  //nema prodavnica u gradu kupca
            int[] a = getClosesCity(edges, cityBuyerId);
            int closestCity = a[0];
            int distance = a[1];
            int max = 0;
            Map<Integer, Integer> mapa = getDistanceFromSource(edges, closestCity);
            for (int city : mapa.keySet()) {
                if (gradovi.contains(city)) {
                    if (max < mapa.get(city)) {
                        max = mapa.get(city);
                    }
                }
            }

            if (proslo >= max + distance) {
                return cityBuyerId;
            }

            if (proslo < max) {
                return closestCity;
            }

            if (proslo < max + distance && proslo >= max) {
                int i = max;
                Map<Integer, Integer> prethodnici = getPrethodnike(edges, cityBuyerId);
                Map<Integer, Integer> path = getPath(prethodnici, mapa, closestCity);
                int ret = closestCity;
                if (path.isEmpty()) {
                    return ret;
                }

                for (int city : path.keySet()) {
                    if (proslo == i + path.get(city)) {
                        return city;
                    } else if (proslo > i + path.get(city)) {
                        ret = city;
                    } else {
                        i += path.get(city);
                    }
                }
                return ret;
            }
        } else {               //postoji prodavnica u gradu kupca
            int max = 0;
            Map<Integer, Integer> mapa = getDistanceFromSource(edges, cityBuyerId);
            for (int city : mapa.keySet()) {
                if (gradovi.contains(city)) {
                    if (max < mapa.get(city)) {
                        max = mapa.get(city);
                    }
                }
            }

            return cityBuyerId;
        }
        return -1;
    }

    //za postavljanje RecievedTime
    public int getTransportTime(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        int buyerId = (new pd190205_OrderOperations().getBuyer(orderId));
        List<Integer> gradovi = new ArrayList<>();

        String query5 = "select distinct(P.IdGrad) from Prodavnica P join Artikal A on A.IdProdavnica=P.IdProdavnica join ListaArtikala la on la.IdArtikal=A.IdArtikal where la.IdPorudzbina=?";
        try ( PreparedStatement stmt = conn.prepareStatement(query5);) {
            stmt.setInt(1, orderId);
            try ( ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    gradovi.add(rs.getInt(1));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        int cityBuyerId = -1;
        String query = "select IdGrad from Kupac where IdKupac = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query);) {
            stmt.setInt(1, buyerId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    cityBuyerId = rs.getInt(1);
                } else {
                    return -1;
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        int cityShopId = -1;

        String query1 = "select IdGrad from Prodavnica where IdGrad = ?";
        try ( PreparedStatement stmt = conn.prepareStatement(query1);) {
            stmt.setInt(1, cityBuyerId);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    cityShopId = rs.getInt(1);
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        //dodavanje u graf
        List<Edge> edges = new ArrayList<>();
        String query2 = "select * from Linija";
        try ( PreparedStatement stmt = conn.prepareStatement(query2);) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Edge e = new Edge(rs.getInt("IdGradOd"), rs.getInt("IdGradDo"), rs.getInt("Distanca"));
                edges.add(e);
            }

        } catch (SQLException ex) {
            Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (cityShopId < 0) {  //nema prodavnica u gradu kupca
            int[] a = getClosesCity(edges, cityBuyerId);
            int closestCity = a[0];
            int distance = a[1];
            int max = 0;
            Map<Integer, Integer> mapa = getDistanceFromSource(edges, closestCity);
            for (int city : mapa.keySet()) {
                if (gradovi.contains(city)) {
                    if (max < mapa.get(city)) {
                        max = mapa.get(city);
                    }
                }
            }
            return max + distance;
        } else {               //postoji prodavnica u gradu kupca
            int max = 0;
            Map<Integer, Integer> mapa = getDistanceFromSource(edges, cityBuyerId);
            for (int city : mapa.keySet()) {
                if (gradovi.contains(city)) {
                    if (max < mapa.get(city)) {
                        max = mapa.get(city);
                    }
                }
            }

            return max;
        }

    }

    static class Node {

        int city; // gradId
        int days; //  distanca

        public Node(int city, int days) {
            this.city = city;
            this.days = days;
        }
    }

    // provera distance dva noda
    static class NodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node n1, Node n2) {
            return n1.days - n2.days;
        }
    }

    class Edge {

        int city; // the city 
        int connectedCity; // the connected 
        int distance; // the distance between cities

        public Edge(int startCity, int connectedCity, int distance) {
            this.city = startCity;
            this.connectedCity = connectedCity;
            this.distance = distance;
        }
    }

    // Trazi najkracu distancu od cvora do ostalih cvorova
    public static Map<Integer, Integer> getDistanceFromSource(List<Edge> graph, int source) {
        PriorityQueue<Node> pq = new PriorityQueue<>(new NodeComparator());
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        for (Edge edge : graph) {
            distances.put(edge.city, Integer.MAX_VALUE);
            distances.put(edge.connectedCity, Integer.MAX_VALUE);
            predecessors.put(edge.city, null);
            predecessors.put(edge.connectedCity, null);
        }

        distances.put(source, 0);
        pq.add(new Node(source, 0));

        // Loop until the queue is empty or all cities are visited
        while (!pq.isEmpty()) {
            // Remove the node with the minimum distance from the queue
            Node node = pq.poll();

            // For each edge starting or ending at the current node
            for (Edge edge : graph) {
                if (edge.city == node.city || edge.connectedCity == node.city) {
                    // Get the other end of the edge
                    int otherCity = (edge.city == node.city) ? edge.connectedCity : edge.city;

                    // Calculate the new distance of the other city by adding the edge distance to the current node distance
                    int newDistance = node.days + edge.distance;

                    // If the new distance is smaller than the current distance of the other city
                    if (newDistance < distances.get(otherCity)) {
                        // Update the distance and predecessor of the other city and add it to the queue
                        distances.put(otherCity, newDistance);
                        predecessors.put(otherCity, node.city);
                        pq.add(new Node(otherCity, newDistance));
                    }
                }
            }
        }

//         System.out.println("The shortest path from city " + source + " to all other cities are:");
//        for (int city : distances.keySet()) {
//            System.out.println("City " + city + ": " + distances.get(city) + " (Path: " + getPath(predecessors, city) + ")");
//        }
        return distances;
    }

    // A method to get the predecessors of each city from a source city using a modified Dijkstra's algorithm
    public static Map<Integer, Integer> getPrethodnike(List<Edge> graph, int source) {
        PriorityQueue<Node> pq = new PriorityQueue<>(new NodeComparator());
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        for (Edge edge : graph) {
            distances.put(edge.city, Integer.MAX_VALUE);
            distances.put(edge.connectedCity, Integer.MAX_VALUE);
            predecessors.put(edge.city, null);
            predecessors.put(edge.connectedCity, null);
        }

        distances.put(source, 0);
        pq.add(new Node(source, 0));

        // Loop until the queue is empty or all cities are visited
        while (!pq.isEmpty()) {
            // Remove the node with the minimum distance from the queue
            Node node = pq.poll();

            // For each edge starting or ending at the current node
            for (Edge edge : graph) {
                if (edge.city == node.city || edge.connectedCity == node.city) {
                    // Get the other end of the edge
                    int otherCity = (edge.city == node.city) ? edge.connectedCity : edge.city;

                    // Calculate the new distance of the other city by adding the edge distance to the current node distance
                    int newDistance = node.days + edge.distance;

                    // If the new distance is smaller than the current distance of the other city
                    if (newDistance < distances.get(otherCity)) {
                        // Update the distance and predecessor of the other city and add it to the queue
                        distances.put(otherCity, newDistance);
                        predecessors.put(otherCity, node.city);
                        pq.add(new Node(otherCity, newDistance));
                    }
                }
            }
        }

        return predecessors;
    }

    // A method to get the path from a source city to a destination city using the predecessors map and the distances map
    public static Map<Integer, Integer> getPath(Map<Integer, Integer> predecessors, Map<Integer, Integer> distances, int destination) {
        // Create a map to store the cityId and distance of each city in the path
        Map<Integer, Integer> path = new HashMap<>();

        // If there is no predecessor for the destination, return an empty map
        if (predecessors.get(destination) == null) {
            return path;
        }

        // Recursively get the path from the source to the predecessor of the destination
        path = getPath(predecessors, distances, predecessors.get(destination));
        // Add the cityId and distance of the destination to the map and return it
        path.put(destination, distances.get(destination));
        return path;
    }

    public int[] getClosesCity(List<Edge> graph, int source) {
        Map<Integer, Integer> distance = getDistanceFromSource(graph, source);
        Connection conn = DB.getInstance().getConnection();
        int closestCity = -1;
        int minDistance = Integer.MAX_VALUE; // a variable to store the minimum distance
        for (int city : distance.keySet()) {
            // Ignore city 0 itself and any unreachable cities
            if (city != 0 && distance.get(city) != Integer.MAX_VALUE) {
                // If the distance of city i is smaller than the current minimum distance
                if (distance.get(city) < minDistance) {
                    // Update the closest city and the minimum distance
                    String query = "select IdProdavnica from Prodavnica where IdGrad = ?";
                    try ( PreparedStatement stmt = conn.prepareStatement(query);) {
                        stmt.setInt(1, city);
                        try ( ResultSet rs = stmt.executeQuery();) {
                            if (rs.next()) {
                                closestCity = city;
                                minDistance = distance.get(city);
                            }
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(pd190205_OrderOperations.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
        int[] a = new int[2];
        a[0] = closestCity;
        a[1] = minDistance;
        return a;
    }

}
