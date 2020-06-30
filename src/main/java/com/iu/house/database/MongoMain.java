package com.iu.house.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.logging.Level;

public class MongoMain {            //Mongo bağlantı yolu ve şekli belirtme sınıfı
    private MongoClient client;

    public MongoMain() {
    }

    public MongoClient mongoConnect(){

        String local = "mongodb://localhost:27017/?serverSelectionTimeoutMS=5000&connectTimeoutMS=10000";
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        if(client == null) return this.client = MongoClients.create(local);
        else return client;

    }
}
