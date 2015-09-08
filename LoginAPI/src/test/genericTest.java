package test;

import static org.junit.Assert.*;

import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.example.login.PasswordHash;
import com.example.login.UserStore;
import com.google.api.server.spi.config.ResourceSchema.Builder;

public class genericTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		String hash = null;
		try {
			hash = PasswordHash.createHash("helloworld");
			System.out.println(hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			assertEquals(true,
					PasswordHash.validatePassword("helloworld", hash));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Date date = new Date();
		out(date.toString());

		out(UUID.randomUUID().toString());

	}

	private void out(String string) {
		// TODO Auto-generated method stub

	}
	
	@Test
	public void test2(){
		Function f = new Function();
		System.out.println(f.PatternChaser("abcdef12kkk12"));
	}

	class Function {
		private String str;
		int k = 0;
		int pos = 0;
		int max = 0;
		int maxPos = 0;
		boolean memory = false;
		ArrayList<Character> list1;
		ArrayList<Character> list2;

		char mFrequent = ' ';
		char lastLetter = ' ';

		String PatternChaser(String str1) {

			// code goes here
			/*
			 * Note: In Java the return type of a function and the parameter
			 * types being passed are defined, so this return call must match
			 * the return type of the function. You are free to modify the
			 * return type.
			 */

			this.str = str1;
			
			list1  = new ArrayList<Character>(); 
			list2  = new ArrayList<Character>();
			
			for(int i = 0 ; i< str.length(); i++){
				char ch = str.charAt(i);
				list1.add(ch);
				list2.add(ch);
			}
			
			function(list1,list2);
			
			String complete = "";
			String result= "";
			
			if(max != 0){
				for(int l = maxPos; l<=maxPos+max; l++){
					complete += list1.get(l);
				}
				if (lastCheck(list1,complete)){
					result += "yes ";
					result += complete;
				}else{
					result += "no null";
				}
			}else{
				result += "no null";
			}
			return result;
		}
		
		void function(ArrayList<Character> list1, ArrayList<Character> list2){
			k++;
			pos = 0;
			char ch = list2.get(0);
			list2.remove(0);
			list2.add(ch);
			
			function1(list1,list2);
			
			if(k<list1.size()-1){
				function(list1,list2);
			}
			
		}
		
		void function1(ArrayList<Character> list1, ArrayList<Character> list2){
			int j = 0;
			int m = -1;
			while(j<list1.size()-k){
				if(list1.get(j) == list2.get(j)){
					m++;
					if(!memory){
						pos = j;
					}
					
					if(m>max){
						max = m;
						maxPos = pos;
					}
					
					memory = true;
					
				}else{
					m=-1;
					memory = false;
				}
				j++;
			}
		}
		
		boolean lastCheck(ArrayList<Character> list, String complete){
			if(list.subList(maxPos+1,maxPos+max+1+1).equals(list.subList(maxPos, maxPos+max+1))){
				return false;
			}
			return true;
		}
	}
}