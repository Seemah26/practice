package com.bl.app.user.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bl.app.user.Repository.UserRepository;
import com.bl.app.user.model.User;
import com.bl.app.user.service.UserService;
import com.google.common.base.Optional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
//@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRep;
	
	
	@Override
	public String login(User user) {

		System.out.println(user.getName());
		System.out.println(user.getEmail());

		String encryptedPassword = securePassword(user);
		System.out.println(encryptedPassword);

		List<User> userList = userRep.findByEmailAndPassword(user.getEmail(), encryptedPassword);
		System.out.println(userList);

		if (userList.size() != 0) {
			System.out.println(user.getEmail());

			return "Welcome  " + userList.get(0).getName() + "   Jwt Token--->" + jwtToken("secretKey", user.getName());
		} else
			return "invalid User Details";

	}

	public String securePassword(User password) {
		String orgpassword = password.getPassword();
		String generatedPassword = null;
		try {

			MessageDigest md = MessageDigest.getInstance("MD5");

			md.update(orgpassword.getBytes());

			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		System.out.println(generatedPassword);

		return generatedPassword;
	}

	public User saveUser(User user) {
		User givenUser = new User();
		givenUser.setEmail(user.getEmail());
		givenUser.setName(user.getName());
		givenUser.setId(user.getId());
		givenUser.setPhonenumber(user.getPhonenumber());
		givenUser.setPassword(securePassword(user));

		userRep.save(givenUser);
		return givenUser;
	}

	public String jwtToken(String secretKey, String subject) {

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		JwtBuilder builder = Jwts.builder().setSubject(subject).setIssuedAt(now).signWith(SignatureAlgorithm.HS256,
				secretKey);

		return builder.compact();

	}

	@Override
	public User userReg(User user) {
		return saveUser(user);

	}

	@Override
	public Optional findUserByResetToken(String resetToken) {
		return userRep.findByResetToken(resetToken);
	}

@Override
	public User findUserByEmail(String email) {
		return userRep.findByEmail(email);
	}

@Override
public User findById(int id) {
	return userRep.findById(id);
	
}

	
	

}
