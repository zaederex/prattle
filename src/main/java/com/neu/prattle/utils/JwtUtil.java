package com.neu.prattle.utils;

import com.neu.prattle.model.Government;
import com.neu.prattle.model.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * A public class that generates Json Web Tokens, for secure communication with client.
 */
public final class JwtUtil {


  private JwtUtil() {
    // private constructor to hide default public constructor
  }

  private static final String PRIVATE_KEY = "jwtPrivateKey";

  /**
   * Generates a JWT token containing username as subject, and userId and role as additional claims.
   * These properties are taken from the specified User object. Tokens validity is infinite.
   *
   * @param u the user for which the token will be generated
   * @return the JWT token
   */
  public static String generateToken(User u) {
    List<GrantedAuthority> grantedAuthorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_USER");

    return Jwts
            .builder()
            .setId("jwtToken")
            .setSubject(u.getUsername())
            .claim("authorities",
                    grantedAuthorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
            .claim("username", u.getUsername() + "")
            .claim("firstName", u.getFirstName() + "")
            .claim("lastName", u.getLastName() + "")
            .claim("contactNumber", u.getContactNumber() + "")
            .claim("timezone", u.getTimezone() + "")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 600000))
            .signWith(SignatureAlgorithm.HS512,
                    PRIVATE_KEY.getBytes()).compact();
  }


  /**
   * Generates a JWT token for the government account.
   *
   * @param government the account for which the token will be generated
   * @return the JWT token
   */
  public static String generateGovToken(Government government) {
    List<GrantedAuthority> grantedAuthorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_ADMIN");

    return Jwts
            .builder()
            .setId("jwtToken")
            .setSubject(government.getGovUsername())
            .claim("authorities",
                    grantedAuthorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
            .claim("username", government.getGovUsername() + "")
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 600000))
            .signWith(SignatureAlgorithm.HS512,
                    PRIVATE_KEY.getBytes()).compact();
  }
}
