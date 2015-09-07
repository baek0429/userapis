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
}