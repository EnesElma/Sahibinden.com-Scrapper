package com.iu.house.dataCrawl;

import com.iu.house.commons.IPGenerator;
import com.iu.house.dataClean.Cleaner;
import com.iu.house.database.MongoMain;
import com.iu.house.producer.URLProducer;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;

public class Scrapper {
    public static int sayac=0;
                               //ilan numarasını tutar
    public static void main(String[] args) throws Exception {

        MongoClient mongoClient = new MongoMain().mongoConnect();
        MongoDatabase database = mongoClient.getDatabase("sahibinden");
        final MongoCollection<org.bson.Document> sendDatabase = database.getCollection("records");  //records collectiona bağlantı


        String [] odaText ={"38473","38470","38474","38471"};       //a20 değerinde tutuluyor
        String [] binayasiText ={"40728","40602","40603","40604","40605","40606","40607","43901","43902","43903"};  //a812 değerinde tutuluyor




        for (int x=0 ; x<4 ; x++){
            for (int y=0 ; y<10 ; y++){

                int minPriceX=100000;
                int minPriceY=0;

                while(true){

                    boolean durum=true;
                    if(minPriceX!=minPriceY) {  //en düşük değer ile arama sonu değeri eşit çıkarsa sonsuz döngüye girmesin diye if kontrolü
                        minPriceY=minPriceX;
                    }
                    else {
                        minPriceX++;
                        minPriceY = minPriceX;
                    }

                    for (int sayfaNo=0 ; sayfaNo<=950 ; sayfaNo+=50){
                        String newIP= IPGenerator.getNewIP();
                        String url="https://www.sahibinden.com/satilik-daire?a24_max=300&pagingOffset="+sayfaNo+"&pagingSize=50"+
                                "&a812="+binayasiText[y]+"&sorting=price_asc&viewType=Classic&" +
                                "price_min="+minPriceX+"&a20="+odaText[x]+"&a24_min=50";


                        Document doc=new URLProducer().urlProduce(url,newIP);
                        Elements webPage=doc.select("tr.searchResultsItem:not(.nativeAd)");

                        if(webPage.isEmpty()){
                            durum=false;
                            break;
                        }

                        Cleaner cleaner=new Cleaner(webPage);    //sayfa url den alınan veriyi temizleyip cleaner nesnesine atıyoruz.
                        List<org.bson.Document> konutlar=cleaner.clean();


                        int tekrar=1;
                        for (org.bson.Document konut:konutlar) {     //cleaner nesnesindeki dataları mongoya kaydetmek için kullanılan döngü


                            int maxConnectTry = 0;
                            while (maxConnectTry != 5) {          //mongo bağlantısı 5 defa tekrarlanırsa çıkış yapılır
                                try {
                                    try {
                                        sendDatabase.insertOne(konut);
                                    } catch (MongoWriteException e) {     //tekrarlı kopya ilanları atlamak için kullanılan exception
                                        System.out.println(tekrar+". Tekrarlı veri");
                                        sayac--;
                                        tekrar++;
                                    }
                                    break;
                                } catch (Exception e) {               //mongo bağlantı ve soket hataları için exception
                                    Thread.sleep(6000);     //mongo hatası olursa 6saniye dinlen
                                    maxConnectTry++;
                                    System.err.println(e + "\n\nMongoSend Hatası");
                                    System.out.println(url);
                                    System.out.println(sayfaNo);
                                }
                            }
                        }



                        if (sayfaNo==950){                      //sayfada ki son ilanın fiyatını minPriceX değişkenine ata
                            System.out.println(url);
                            Element element=doc.select("td.searchResultsPriceValue").last();
                            String[] ucret1 = element.text().split(" ");     //"TL" yazısını silme
                            String[] ucret2 = ucret1[0].split("\\.");                                        //noktaları silme
                            String ucretSon ="";
                            for (String s : ucret2) {
                                ucretSon = ucretSon + s;                                                    //birleştirme
                            }
                            minPriceX=Integer.parseInt(ucretSon);
                            System.out.println("Sayfa no alanı içinde SayfaNo: "+sayfaNo);
                        }


                        System.out.println("SayfaNo: " +  sayfaNo);
                        Thread.sleep(1500);
                    }

                    System.out.println("minPriceX: "+ minPriceX);
                    System.out.println("minPriceY: "+ minPriceY);

                    if (!durum) break;
                    if (minPriceX>2000000) break;
                }
            }
        }












    }
}
