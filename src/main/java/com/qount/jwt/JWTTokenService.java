package com.qount.jwt;

import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.WebApplicationException;

import org.apache.log4j.Logger;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

public class JWTTokenService {

	private static final Logger LOGGER = Logger.getLogger(JWTTokenService.class);

	public String generate(String secret) {
		String token = null;
		try {
			JwtClaims claims = prepareClaims();
			token = prepareTokenFromClaims(claims, secret);
		} catch (Exception e) {
			LOGGER.error("Error generating token", e);
		}
		return token;
	}

	private JwtClaims prepareClaims() {
		JwtClaims claims = new JwtClaims();
		claims.setIssuer("qount.io"); // who creates the token and signs it
		// time when the token will expire (60 minutes from now)
		claims.setExpirationTimeMinutesInTheFuture(1);
		claims.setJwtId(UUID.randomUUID().toString()); // a unique identifier
														// for the token
		claims.setIssuedAtToNow(); // when the token was issued/created (now)
		// time before which the token is not yet valid (2 minutes ago)
		claims.setNotBeforeMinutesInThePast(2);
		// the subject/principal is whom the token is about
		return claims;
	}

	private String prepareTokenFromClaims(JwtClaims claims, String secret) {
		String token = null;
		if (claims == null) {
			throw new WebApplicationException();
		}
		try {
			JsonWebSignature jws = new JsonWebSignature();
			jws.setPayload(claims.toJson());
			jws.setHeader("typ", "JWT");
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
			jws.setKey(secretKey);
			jws.setKeyIdHeaderValue(UUID.randomUUID().toString());
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
			token = jws.getCompactSerialization();
		} catch (Exception e) {
			LOGGER.error("Error preparing token from claims", e);
		}
		return token;
	}

	public static void main(String[] args) {
		String companyID = "eca4dfd5-c6f6-411a-a3a1-fa5a134e43cd";
		JWTTokenService jwtTokenService = new JWTTokenService();
		String token = jwtTokenService.generate(companyID);
		System.out.println(token);
		System.out.println(jwtTokenService.consume(token, companyID));

	}

	public JwtClaims consume(String token, String secret) {
		JwtClaims claims = null;
		try {
			JwtConsumer jwtConsumer = new JwtConsumerBuilder().setVerificationKey(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"))
					.setJwsAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.HMAC_SHA256)).build();
			claims = jwtConsumer.processToClaims(token);
			System.out.println("JWT validation succeeded! " + claims);
		} catch (Exception e) {
			LOGGER.error("Error consuming the token", e);
		}
		return claims;
	}

}
