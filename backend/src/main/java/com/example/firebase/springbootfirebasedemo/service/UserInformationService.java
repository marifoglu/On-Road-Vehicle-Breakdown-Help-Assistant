package com.example.firebase.springbootfirebasedemo.service;

import com.example.firebase.springbootfirebasedemo.entity.UserInformation;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class UserInformationService {

    private static final String COLLECTION_NAME ="UserInformation" ;

    public List<UserInformation> getAllUsers() throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirestoreClient.getFirestore();

        Iterable<DocumentReference> documentReference = dbFirestore.collection(COLLECTION_NAME).listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        List<UserInformation> userInformationList = new ArrayList<>();
        UserInformation userInformation = null;

        while (iterator.hasNext()) {
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            userInformation = document.toObject(UserInformation.class);
            if (userInformation != null) {
                userInformation.setDocumentId(document.getId());
                userInformationList.add(userInformation);
            }
        }
        return userInformationList;
    }

    public UserInformation getUserById(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();
        if (document.exists()) {
            UserInformation userInformation = document.toObject(UserInformation.class);
            return userInformation;
        } else {
            return null; // User information not found
        }
    }

    public String saveUser(UserInformation userInformation) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userInformation.getId());
        userData.put("email", userInformation.getEmail());
        userData.put("homeAddress", userInformation.getHomeAddress());
        userData.put("nameAndSurname", userInformation.getNameAndSurname());
        userData.put("phoneNumber", userInformation.getPhoneNumber());

        ApiFuture<DocumentReference> documentReference = dbFirestore
                .collection(COLLECTION_NAME)
                .add(userData);
        System.out.println(userData);

        return documentReference.get().getId();
    }

    public String deleteUserById(String documentId) {

        Firestore dbFirestore= FirestoreClient.getFirestore();

        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection(COLLECTION_NAME).document(documentId).delete();
        return "Document with Product ID" + documentId + " has been deleted successfully";

    }

    public String updateUser(UserInformation userInformation) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", userInformation.getId());
        userData.put("email", userInformation.getEmail());
        userData.put("homeAddress", userInformation.getHomeAddress());
        userData.put("nameAndSurname", userInformation.getNameAndSurname());
        userData.put("phoneNumber", userInformation.getPhoneNumber());

        ApiFuture<WriteResult> writeResult = dbFirestore
                .collection(COLLECTION_NAME)
                .document(userInformation.getEmail())
                .set(userData);

        return writeResult.get().getUpdateTime().toString();
    }









}
