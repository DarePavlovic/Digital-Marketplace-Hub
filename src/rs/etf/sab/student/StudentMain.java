
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.ArrayList;
import rs.etf.sab.operations.*;
import org.junit.Test;
import org.junit.Assert;
import rs.etf.sab.student.*;

import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;
import java.util.Calendar;
import java.util.List;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new pd190205_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new pd190205_BuyerOperations();
        CityOperations cityOperations = new pd190205_CityOperations();
        GeneralOperations generalOperations = new pd190205_GeneralOperations();
        OrderOperations orderOperations = new pd190205_OrderOperations();
        ShopOperations shopOperations = new pd190205_ShopOperations();
        TransactionOperations transactionOperations = new pd190205_TransactionOperations();

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
        
//        
//        generalOperations.eraseAll();
//        Calendar initialTime = Calendar.getInstance();
//        initialTime.clear();
//        initialTime.set(2018, 0, 1);
//        generalOperations.setInitialTime(initialTime);
//        Calendar receivedTime = Calendar.getInstance();
//        receivedTime.clear();
//        receivedTime.set(2018, 0, 22);
//        int cityB = cityOperations.createCity("B");
//        int cityC1 = cityOperations.createCity("C1");
//        int cityA = cityOperations.createCity("A");
//        int cityC2 = cityOperations.createCity("C2");
//        int cityC3 = cityOperations.createCity("C3");
//        int cityC4 = cityOperations.createCity("C4");
//        int cityC5 = cityOperations.createCity("C5");
//        cityOperations.connectCities(cityB, cityC1, 8);
//        cityOperations.connectCities(cityC1, cityA, 10);
//        cityOperations.connectCities(cityA, cityC2, 3);
//        cityOperations.connectCities(cityC2, cityC3, 2);
//        cityOperations.connectCities(cityC3, cityC4, 1);
//        cityOperations.connectCities(cityC4, cityA, 3);
//        cityOperations.connectCities(cityA, cityC5, 15);
//        cityOperations.connectCities(cityC5, cityB, 2);
//        int shopA = shopOperations.createShop("shopA", "A");
//        int shopC2 = shopOperations.createShop("shopC2", "C2");
//        int shopC3 = shopOperations.createShop("shopC3", "C3");
//        shopOperations.setDiscount(shopA, 20);
//        shopOperations.setDiscount(shopC2, 50);
//        int laptop = articleOperations.createArticle(shopA, "laptop", 1000);
//        int monitor = articleOperations.createArticle(shopC2, "monitor", 200);
//        int stolica = articleOperations.createArticle(shopC3, "stolica", 100);
//        int sto = articleOperations.createArticle(shopC3, "sto", 200);
//        shopOperations.increaseArticleCount(laptop, 10);
//        shopOperations.increaseArticleCount(monitor, 10);
//        shopOperations.increaseArticleCount(stolica, 10);
//        shopOperations.increaseArticleCount(sto, 10);
//        int buyer = buyerOperations.createBuyer("kupac", cityB);
//        buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
//        int order = buyerOperations.createOrder(buyer);
//        orderOperations.addArticle(order, laptop, 5);
//        orderOperations.addArticle(order, monitor, 4);
//        orderOperations.addArticle(order, stolica, 10);
//        orderOperations.addArticle(order, sto, 4);
//      
//        //Assert.assertNull(orderOperations.getSentTime(order));
//        System.out.println("1. null = " + orderOperations.getSentTime(order));
//      
//        //Assert.assertTrue("created".equals(orderOperations.getState(order)));
//        System.out.println("2. created = " + orderOperations.getState(order));
//      
//        orderOperations.completeOrder(order);
//        //System.out.println(orderOperations.getSentTime(order));
//      
//        //Assert.assertTrue("sent".equals(orderOperations.getState(order)));
//        System.out.println("3. sent = " + orderOperations.getState(order));
//      
//        int buyerTransactionId = (Integer)transactionOperations.getTransationsForBuyer(buyer).get(0);
//      
//        //Assert.assertEquals(initialTime, transactionOperations.getTimeOfExecution(buyerTransactionId));
//        System.out.println( "4. " + initialTime.getTime() + " = "+transactionOperations.getTimeOfExecution(buyerTransactionId).getTime());
//        //System.out.println( "test " + (initialTime .equals (transactionOperations.getTimeOfExecution(buyerTransactionId))) );
//          
//        //Assert.assertNull(transactionOperations.getTransationsForShop(shopA));
//        System.out.println("5. null = " + transactionOperations.getTransationsForShop(shopA));
//        
//        BigDecimal shopAAmount = (new BigDecimal("5")).multiply(new BigDecimal("1000")).setScale(3);
//        BigDecimal shopAAmountWithDiscount = (new BigDecimal("0.8")).multiply(shopAAmount).setScale(3);
//        BigDecimal shopC2Amount = (new BigDecimal("4")).multiply(new BigDecimal("200")).setScale(3);
//        BigDecimal shopC2AmountWithDiscount = (new BigDecimal("0.5")).multiply(shopC2Amount).setScale(3);
//        BigDecimal shopC3Amount = (new BigDecimal("10")).multiply(new BigDecimal("100")).add((new BigDecimal("4")).multiply(new BigDecimal("200"))).setScale(3);
//        BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
//        BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3Amount).setScale(3);
//        BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
//        BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
//        BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
//        BigDecimal shopC3AmountReal = shopC3Amount.multiply(new BigDecimal("0.95")).setScale(3);
//      
//        //Assert.assertEquals(amountWithDiscounts, orderOperations.getFinalPrice(order));
//        System.out.println("6. "+amountWithDiscounts + " = "+ orderOperations.getFinalPrice(order));
//
//        //Assert.assertEquals(amountWithoutDiscounts.subtract(amountWithDiscounts), orderOperations.getDiscountSum(order));
//        System.out.println("7. "+amountWithoutDiscounts.subtract(amountWithDiscounts)+ " = "+ orderOperations.getDiscountSum(order));
//
//        //Assert.assertEquals(amountWithDiscounts, transactionOperations.getBuyerTransactionsAmmount(buyer));
//        System.out.println("8. "+amountWithDiscounts + " = " + transactionOperations.getBuyerTransactionsAmmount(buyer));
//        
//        //Assert.assertEquals(transactionOperations.getShopTransactionsAmmount(shopA), (new BigDecimal("0")).setScale(3));
//        //Assert.assertEquals(transactionOperations.getShopTransactionsAmmount(shopC2), (new BigDecimal("0")).setScale(3));
//        //Assert.assertEquals(transactionOperations.getShopTransactionsAmmount(shopC3), (new BigDecimal("0")).setScale(3));
//
//        //System.out.println("treba 4 true");
//        System.out.println("9. "+transactionOperations.getShopTransactionsAmmount(shopA).equals( new BigDecimal("0").setScale(3)));
//        System.out.println("10. "+transactionOperations.getShopTransactionsAmmount(shopC2).equals( new BigDecimal("0").setScale(3)));
//        System.out.println("11. "+transactionOperations.getShopTransactionsAmmount(shopC3).equals( new BigDecimal("0").setScale(3)));
//        
//       
//        //Assert.assertEquals((new BigDecimal("0")).setScale(3), transactionOperations.getSystemProfit());
//        System.out.println( "12. "+(new BigDecimal("0").setScale(3)).equals(transactionOperations.getSystemProfit()));
//        System.out.println("13. "+"0.000 = " + transactionOperations.getSystemProfit() );
//        
//        //System.out.println("-------");
//        pd190205_OrderOperations  op = new pd190205_OrderOperations();
//
//        generalOperations.time(2);
//        //Assert.assertEquals(initialTime, orderOperations.getSentTime(order));
//        //Assert.assertNull(orderOperations.getRecievedTime(order));
//        System.out.println("14. "+initialTime.getTime() + " = "+orderOperations.getSentTime(order).getTime());
//        //System.out.println(orderOperations.getSentTime(order).getTime());
//        //System.out.println(initialTime.equals(orderOperations.getSentTime(order)));
//        System.out.println("15. "+"null = "+ orderOperations.getRecievedTime(order));
//        //Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityA);
//        System.out.println("16. "+orderOperations.getLocation(order) + " = "+ (long)cityA);
//        generalOperations.time(9);
//        //Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityA);
//        System.out.println("17. "+orderOperations.getLocation(order) + " = "+ (long)cityA);
//        generalOperations.time(8);
//        //Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityC5);
//        System.out.println("18. "+orderOperations.getLocation(order) + " = "+ (long)cityC5);
//        generalOperations.time(5); 
//        //Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityB);
//        System.out.println("19. "+orderOperations.getLocation(order) + " = "+ (long)cityB);
//      
//      
//        //Assert.assertEquals(receivedTime, orderOperations.getRecievedTime(order));
//        System.out.println("20. "+receivedTime .equals(orderOperations.getRecievedTime(order)));
//      
//        //Assert.assertEquals(shopAAmountReal, transactionOperations.getShopTransactionsAmmount(shopA));
//        //Assert.assertEquals(shopC2AmountReal, transactionOperations.getShopTransactionsAmmount(shopC2));
//        //Assert.assertEquals(shopC3AmountReal, transactionOperations.getShopTransactionsAmmount(shopC3));
//        System.out.println("21. "+shopAAmountReal + " = "+ transactionOperations.getShopTransactionsAmmount(shopA));
//        System.out.println("22. "+shopC2AmountReal + " = "+ transactionOperations.getShopTransactionsAmmount(shopC2));
//        System.out.println("23. "+shopC3AmountReal + " = "+ transactionOperations.getShopTransactionsAmmount(shopC3));
//      
//        //Assert.assertEquals(systemProfit, transactionOperations.getSystemProfit());
//        System.out.println("24. "+systemProfit + " = " + transactionOperations.getSystemProfit() );
//     
//        int shopATransactionId = transactionOperations.getTransactionForShopAndOrder(order, shopA);
//        //Assert.assertNotEquals(-1L, (long)shopATransactionId);
//        //Assert.assertEquals(receivedTime, transactionOperations.getTimeOfExecution(shopATransactionId));
//        System.out.println("25. "+receivedTime.getTime() + "  =  "+transactionOperations.getTimeOfExecution(shopATransactionId).getTime());
//        //System.out.println(receivedTime .equals(transactionOperations.getTimeOfExecution(shopATransactionId)));
//        
    }
}
