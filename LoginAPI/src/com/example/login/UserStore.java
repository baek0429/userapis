package com.example.login;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class UserStore {
	
	public static ArrayList<String> getStoreUserQuery(String name, String email, String password){
		UUID uuid = UUID.randomUUID();
		String hash = hashSSHA(password);
		System.out.println("Creating statement...");
		
		ArrayList<String> result = new ArrayList<>();
		result.add(uuid.toString());
		result.add(name);
		result.add(email);
		result.add(hash);
		return result;
	}
	
	public static String hashSSHA(String password){
		try {
			return PasswordHash.createHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean checkHashSSHA(String hash, String password){
		try {
			return PasswordHash.validatePassword(password, hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
