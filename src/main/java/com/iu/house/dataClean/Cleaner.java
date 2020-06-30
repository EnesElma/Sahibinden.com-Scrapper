package com.iu.house.dataClean;

import com.iu.house.dataCrawl.Scrapper;
import org.bson.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class Cleaner {
    private Elements pageData;              //Bir sayfadaki tüm veriler


    public Cleaner(Elements pageData) {
        this.pageData = pageData;
    }

    public List<Document> clean() {         //çektiğimiz sayfalardaki dataları temizleyen metot

        List<Document> konutlar= new ArrayList<Document>();

        for (Element singleAdvert : pageData) {

            String[] fiyat1 = singleAdvert.select(".searchResultsPriceValue").text().split(" ");     //"TL" yazısını silme
            String[] fiyat2=fiyat1[0].split(",");
            String[] fiyat3 = fiyat2[0].split("\\.");                                        //noktaları silme
            String fiyat ="";
            for (String s : fiyat3) {
                fiyat = fiyat + s;                                                    //birleştirme
            }


            String[] results = singleAdvert.select(".searchResultsAttributeValue").text().split(" ");
            String m2=results[0].trim();
            String oda=results[1].trim();

            String[] location = singleAdvert.select(".searchResultsLocationValue").text().split(" ");
            String il = location[0].trim();
            String ilce = location[1].trim();

            String url=singleAdvert.select(".classifiedTitle").attr("abs:href");

            System.out.println(Scrapper.sayac+".            "
                    +m2 + " " + fiyat +" " +oda + " " + il + " " + ilce);
            System.out.println(url + "\n");

            Document doc=new Document("no",Scrapper.sayac)
                    .append("url",url)
                    .append("m2",Integer.parseInt(m2))
                    .append("fiyat",Integer.parseInt(fiyat))
                    .append("oda",oda)
                    .append("il",il)
                    .append("ilce",ilce);

            konutlar.add(doc);
            Scrapper.sayac++;


        }




        return konutlar;
    }
}
