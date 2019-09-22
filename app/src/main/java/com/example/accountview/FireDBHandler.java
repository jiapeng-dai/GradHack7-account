package com.example.accountview;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.collection.ArraySortedMap;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.google.android.gms.tasks.Tasks.await;

public class FireDBHandler {
    //information of database
    //private static final int DATABASE_VERSION = 1;
    //private static final String DATABASE_NAME = "clientDB.db";
    public static final String CTABLE_NAME = "Clients";
    public static final String COLUMN_ID = "ClientId";
    public static final String COLUMN_NAME = "ClientName";
    public static final String COLUMN_BALANCE = "Balance";
    public static final String TRAN_TABLE_NAME = "Transac";
    //public static final String TRAN_C_ID = "ClientID";
    public static final String TRAN_T_ID = "TranId";
    public static final String COLUMN_ACTION = "Act";
    public static final String COLUMN_AMOUNT = "Amount";
    public static final int ROW_LIMIT = 10;

    public static void createtable() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        Map<String, Object> client = new HashMap<>();
        //client.put("id", 11);
        client.put(COLUMN_ID,"11");
        client.put(COLUMN_NAME, "Bob");
        client.put(COLUMN_BALANCE, "100");

        // Add a new document with a given ID
        db.collection(CTABLE_NAME).document("11")
                .set(client)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        Map<String, Object> transac = new HashMap<>();
        //dummy row
        //transac.put("tid", 0);
        transac.put(TRAN_T_ID, "0");
        transac.put(COLUMN_ACTION, "deposit");
        transac.put(COLUMN_AMOUNT, "0.0");

        // Add a new document with a given ID
        db.collection(TRAN_TABLE_NAME).document("0")
                .set(client)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public static ArrayList<String> loadTHandler() {
        final ArrayList<String> result = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> task = db.collection(TRAN_TABLE_NAME)
                .get();
        while(task.isComplete()==false) {
            try {
                Thread.sleep(200);
            } catch(InterruptedException e) {
                Log.w(TAG, "Error getting documents: Being interrupted!"+e.getMessage());
                return result;
            }
        }
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Map<String, Object> datamap = document.getData();
                String id = (String)datamap.get(TRAN_T_ID);
                String act = (String)datamap.get(COLUMN_ACTION);
                String amount = (String)datamap.get(COLUMN_AMOUNT);
                result.add(id + " " + act + " " + amount);
            }
        } else {
            Log.d(TAG, "Error getting documents: ", task.getException());
        }
        /*
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> datamap = document.getData();
                                String id = (String)datamap.get(TRAN_T_ID);
                                String act = (String)datamap.get(COLUMN_ACTION);
                                String amount = (String)datamap.get(COLUMN_AMOUNT);
                                result.add(id + " " + act + " " + amount);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

         */
        return result;
    }
    public static void addCHandler(Clients client) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> clientmap = new HashMap<>();
        clientmap.put(COLUMN_ID, Integer.toString(client.getID()));
        clientmap.put(COLUMN_NAME, client.getClientName());
        clientmap.put(COLUMN_BALANCE, Float.toString(client.getBalance()));
        db.collection(CTABLE_NAME).document(Integer.toString(client.getID())).set(clientmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "client data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding client document", e);
                    }
                });
        //long result = db.insert(TABLE_NAME, null, values);
        //db.close();
        //return (int)result;
    }
    public static void addTHandler(int id, String act, float amount){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //If more than 10 rows, delete the first one
        ArrayList<String> rows = loadTHandler();
        if (rows.size() >= ROW_LIMIT) {
            String sid = rows.get(0).substring(0,2).trim();
            deleteTHandler(sid);
            //db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{sid});
        }
        Map<String, Object> tranmap = new HashMap<>();
        tranmap.put(TRAN_T_ID,Integer.toString(id));
        tranmap.put(COLUMN_ACTION,act);
        tranmap.put(COLUMN_AMOUNT,Float.toString(amount));
        db.collection(TRAN_TABLE_NAME).document(Integer.toString(id)).set(tranmap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Transaction data successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding transaction document", e);
                    }
                });
    }

    public static ArrayList<String> findCTaskHandler(String clientname) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<String> cliparalist = new ArrayList<>();
        Task<QuerySnapshot> task =
        db.collection(CTABLE_NAME)
                .whereEqualTo(COLUMN_NAME, clientname)
                .get();
        /*
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //only return the first result
                            DocumentSnapshot docfound = task.getResult().getDocuments().get(0);
                            //ArraySortedMap<String, Object> cmap = (ArraySortedMap)docfound.get("doc");
                            //List<DocumentSnapshot> my_collections = queryDocumentSnapshots.getDocuments();
                            Map<String,Object> cmap = docfound.getData();
                            cliparalist.add((String)cmap.get(COLUMN_ID));
                            cliparalist.add((String)cmap.get(COLUMN_NAME));
                            cliparalist.add((String)cmap.get(COLUMN_BALANCE));
                            Log.d(TAG, "Read data success" + cliparalist.toString());
                        } else {
                            cliparalist.add("-1");
                            Log.w(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
         */
        /*
        synchronized (cliparalist) {
            try {
                while(cliparalist.size() == 0) {
                    cliparalist.wait();
                }
                cliparalist.notify();
                return cliparalist;
            } catch(InterruptedException e) {
                Log.w(TAG, "Error getting documents: Being interrupted!"+e.getMessage());
                return cliparalist;
            }
        }
         */
        while(task.isComplete()==false) {
            try {
                Thread.sleep(200);
            } catch(InterruptedException e) {
                Log.w(TAG, "Error getting documents: Being interrupted!"+e.getMessage());
                return cliparalist;
            }
        }
        if (task.isSuccessful()) {
            //only return the first result
            DocumentSnapshot docfound = task.getResult().getDocuments().get(0);
            //ArraySortedMap<String, Object> cmap = (ArraySortedMap)docfound.get("doc");
            //List<DocumentSnapshot> my_collections = queryDocumentSnapshots.getDocuments();
            Map<String,Object> cmap = docfound.getData();
            cliparalist.add((String)cmap.get(COLUMN_ID));
            cliparalist.add((String)cmap.get(COLUMN_NAME));
            cliparalist.add((String)cmap.get(COLUMN_BALANCE));
            Log.d(TAG, "Read data success" + cliparalist.toString());
        } else {
            cliparalist.add("-1");
            Log.w(TAG, "Error getting documents: ", task.getException());
        }
        return cliparalist;
    }
    public static Clients findCHandler(String clientname) {
        Clients client = new Clients();
        ArrayList<String> clist = findCTaskHandler((clientname));
        if (clist.size()>0) {
            Log.d(TAG, "Read data success2" + clist.toString());
            if (clist.size() == 1) {
                return null;
            }
            client.setID(Integer.parseInt(clist.get(0)));
            client.setClientName(clist.get(1));
            client.setBalance(Float.parseFloat(clist.get(2)));
        } else {
            Log.w(TAG, "Error getting client lists ");
        }
        return client;
    }

    public static boolean deleteCHandler(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boolean result = false;
        try {
            db.collection(CTABLE_NAME).document(id).delete();
            result = true;
        } catch(Exception e) {
            Log.w(TAG, "Error deleting document", e);
        }
        return result;
    }

    public static boolean deleteTHandler(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        boolean result = false;
        try {
            db.collection(TRAN_TABLE_NAME).document(id).delete();
            result = true;
        } catch(Exception e) {
            Log.w(TAG, "Error deleting document", e);
        }
        return result;
    }

    public static boolean updateCHandler(int id, String name, Float balance) {
        //boolean result = false;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> map = new HashMap<>();
        map.put(COLUMN_ID,Integer.toString(id));
        map.put(COLUMN_NAME, name);
        map.put(COLUMN_BALANCE, Float.toString(balance));
        try {
            db.collection(CTABLE_NAME).document(Integer.toString(id)).set(map);
            Log.d(TAG, "DocumentSnapshot successfully updated!");
        } catch(Exception e) {
            Log.w(TAG, "Error updating document", e);
            return false;
        }
        return true;
    }
}
