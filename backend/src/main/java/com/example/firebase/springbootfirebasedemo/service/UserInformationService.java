package com.example.firebase.springbootfirebasedemo.service;

import com.example.firebase.springbootfirebasedemo.entity.UserInformation;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserInformationService {

    private static final String COLLECTION_NAME ="UserInformation" ;


    public String saveUser(UserInformation userInformation) throws ExecutionException, InterruptedException {

       Firestore dbFirestore= FirestoreClient.getFirestore();

       ApiFuture<WriteResult> collectionApiFuture=dbFirestore.collection(COLLECTION_NAME).document(userInformation.getEmail()).set(userInformation);

       return collectionApiFuture.get().getUpdateTime().toString();
    }

    public UserInformation getUserByName(String name) throws ExecutionException, InterruptedException {

        Firestore dbFirestore= FirestoreClient.getFirestore();

        DocumentReference documentReference=dbFirestore.collection(COLLECTION_NAME).document(name);

        ApiFuture<DocumentSnapshot> future=documentReference.get();

        DocumentSnapshot document=future.get();

        UserInformation userInformation =null;
        if(document.exists()) {
         userInformation = document.toObject(UserInformation.class);
       return userInformation;
        }else{
            return null;
        }


    }

    public List<UserInformation> getUserDetails() throws ExecutionException, InterruptedException {

        Firestore dbFirestore= FirestoreClient.getFirestore();

        Iterable<DocumentReference> documentReference=dbFirestore.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> iterator=documentReference.iterator();

        List<UserInformation> userInformationList =new ArrayList<>();
        UserInformation userInformation = null;

        while(iterator.hasNext()){
           DocumentReference documentReference1=iterator.next();
           ApiFuture<DocumentSnapshot> future= documentReference1.get();
           DocumentSnapshot document=future.get();

           userInformation =document.toObject(UserInformation.class);
           userInformationList.add(userInformation);

        }
        return userInformationList;
    }


    public String updateUser(UserInformation userInformation) throws ExecutionException, InterruptedException {

        Firestore dbFirestore= FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionApiFuture=dbFirestore.collection(COLLECTION_NAME).document(userInformation.getEmail()).set(userInformation);

        return collectionApiFuture.get().getUpdateTime().toString();

    }

    public String deleteUser(String name) throws ExecutionException, InterruptedException {

        Firestore dbFirestore= FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionApiFuture=dbFirestore.collection(COLLECTION_NAME).document(name).delete();

        return "Document with UserInformation ID"+name+" has been deleted successfully";

    }

    public UserInformation getUserById(long id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(String.valueOf(id));
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(UserInformation.class);
        } else {
            return null;
        }
    }


    public String deleteUserById(long id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(String.valueOf(id)).delete();
        return "Document with UserInformation ID " + id + " has been deleted successfully";
    }

}
