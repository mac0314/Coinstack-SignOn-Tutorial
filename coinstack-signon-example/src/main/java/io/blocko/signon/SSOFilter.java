package io.blocko.signon;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SSOFilter implements Filter {
	private AuthService authService = new AuthService();

	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("Initializing filter...");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// Common Request, Response
	    HttpServletRequest req = (HttpServletRequest) request;
	    HttpServletResponse res = (HttpServletResponse) response;

	    try {
	      // 1. Get access token
	      if (authService.hasAccessToken(req)) {
	        // 2. Check validity for access token
	        if (authService.verifyAccessToken(req)) {
	          chain.doFilter(req, res);
	        } else {
	          authService.removeAccessTokenFromCookie(req, res);
	          authService.requestAuthCode(req, res);
	        }
	      } else {
	        // 3. Redirect to oauth server's login form
	        if (req.getParameter("code") != null) {
	          authService.addAccessTokenToCookie(req, res);
	          res.sendRedirect(req.getRequestURI());
	        } else {
	          authService.requestAuthCode(req, res);
	        }
	      }

	    } catch (Exception e) {
	      e.printStackTrace();
	    }

	}

	public void destroy() {
		System.err.println("Destroying filter...");
	}

}
