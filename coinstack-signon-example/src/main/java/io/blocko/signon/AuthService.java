package io.blocko.signon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class AuthService {
	private static final String CLIENT_ID = "trusted"; // 클라이언트 ID
	private static final String SECRET = "secret"; // 클라이언트 비밀번호
	private static final String GRANT_TYPE = "authorization_code"; // 권한 취득 방식
	private static final String SCOPE = "read"; // 접근 제어 범위
	private static final String RESPONSE_TYPE = "code"; // 인증서버 응답 방식
	private static final String SERVER_DOMAIN = "http://localhost:8888"; // coinstack-signon-example
	private static final String ENDPOINT = "http://localhost:12000";// coinstack-signon-1.1 server
	
	// =============== SSO methods ================ //
	// 1. hasAccessToken
	// 2. findAccessTokenFromCookie
	// 3. addAccessTokenToCookie
	// 4. requestAuthCode
	// 5. verifyAccessToken
	// 6. getAccessTokenInfo
	// 7. removeAccessTokenFromCookie
	// ============================================ //
	/**
	   * AccessToken 유무 확인.
	   * 
	   * @param req HttpServletRequest
	   * @return
	   */
	  public boolean hasAccessToken(HttpServletRequest req) {
	    boolean result = false;
	    if (findAccessTokenFromCookie(req) != null) {
	      // accessToken이 있는 경우
	      result = true;
	    }
	    return result;
	  }
	  
	  /**
	   * Cookie 에서 accessToken값을 찾는 함수.
	   * 
	   * @param req HttpServletRequest
	   * @return
	   */
	  private String findAccessTokenFromCookie(HttpServletRequest req) {
	    String result = null;
	    Cookie[] cookies = req.getCookies();
	    if( null != cookies ) {
	      for (int i = 0; i < cookies.length; i++) {
	        if ("accessToken".equals(cookies[i].getName())) {
	          result = cookies[i].getValue();
	        }
	      }
	    }
	    return result;
	  }
	  
	  /**
	   * Access token을 가져와 Cookie에 추가하는 메서드.
	   * @param authCode String
	   * @param req HttpServletRequest
	   * @param res HttpServletResponse
	   */
	  public void addAccessTokenToCookie(HttpServletRequest req, HttpServletResponse res)
	      throws Exception {
	    String getToken = ENDPOINT + "/oauth/token";
	    String data = "grant_type=" + GRANT_TYPE + "&redirect_uri=" + SERVER_DOMAIN
	        + req.getRequestURI() + "&code=" + req.getParameter("code");
	    String clientInfo = CLIENT_ID + ":" + SECRET;
	    String accessInfo = sendPost(getToken, data, clientInfo);
	    if (accessInfo != null) {
	      JSONObject accessInfoJson = new JSONObject(accessInfo);

	      Cookie cookie = new Cookie("accessToken", accessInfoJson.getString("access_token"));
	      res.addCookie(cookie);
	    }
	  }
	  
	  /**
	   * authCode 요청 (loginPage 리다이렉션).
	   * 
	   * @param req HttpServletRequest
	   * @param res HttpServletResponse
	   * @throws IOException IOException
	   */
	  public void requestAuthCode(HttpServletRequest req, HttpServletResponse res) throws IOException {
	    res.sendRedirect(ENDPOINT + "/oauth/authorize?response_type=" + RESPONSE_TYPE + "&grant_type="
	        + GRANT_TYPE + "&scope=" + SCOPE + "&client_id=" + CLIENT_ID + "&secret=" + SECRET
	        + "&redirect_uri=" + SERVER_DOMAIN + req.getRequestURI().toString());
	  }
	  
	  /**
	   * accessToken 증명.
	   * 
	   * @param req HttpServletRequest
	   * @throws Exception Exception
	   */
	  public boolean verifyAccessToken(HttpServletRequest req) throws Exception {
	    boolean result = false;
	    if (getAccessTokenInfo(req) != null) {
	      // accessToken이 유효한 경우
	      result = true;
	    }
	    return result;
	  }
	  
	  /**
	   * accessToken 정보를 읽어오는 메서드.
	   * 
	   * @param req HttpServletRequest
	   * @throws Exception Exception
	   */
	  private JSONObject getAccessTokenInfo(HttpServletRequest req) throws Exception {
	    String checkAt = "token=" + findAccessTokenFromCookie(req);
	    String auth = CLIENT_ID + ":" + SECRET;
	    String checkToken = ENDPOINT + "/oauth/check_token";
	    JSONObject result = null;

	    String response = sendPost(checkToken, checkAt, auth);
	    if (response != null) {
	      result = new JSONObject(response);
	    }
	    return result;
	  }
	  
	  /**
	   * AccessToken이 유효하지 않은 경우 Cookie값을 삭제하는 메서.
	   * @param req HttpServletRequest
	   * @param res HttpServletResponse
	   */
	  public void removeAccessTokenFromCookie(HttpServletRequest req, HttpServletResponse res) 
	      throws Exception {
	    for (Cookie cookie : req.getCookies()) {
	      cookie.setMaxAge(0);
	      res.addCookie(cookie);
	    }
	  }
	  
	  /**
	   * HTTP POST 요청/응답 메서드.
	   * @param uri String
	   * @param params String
	   * @param clientInfo String
	   * @return data String
	   */
	private String sendPost(String uri, String params, String clientInfo) throws Exception {

	  URL url = new URL(uri);
	  HttpURLConnection con = (HttpURLConnection) url.openConnection();
	  if (clientInfo != null) {
	    String userpass = clientInfo;
	    String basicAuth =
	        "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
	    con.setRequestProperty("Authorization", basicAuth);
	  }
	  con.setRequestMethod("POST");
	  con.setDoOutput(true);

	  PrintWriter pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
	  pw.write(params);
	  pw.flush();

	  String line;
	  String data = "";
	  String result = null;

	  if (con.getResponseCode() != 400) {
	    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
	    while ((line = br.readLine()) != null) {
	      data += line + "\n";
	    }

	    pw.close();
	    br.close();
	    result = data;
	  }
	  return result;
	}
}
