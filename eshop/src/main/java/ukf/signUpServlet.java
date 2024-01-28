package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class signUpServlet
 */
@WebServlet("/signUpServlet")
public class signUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	HttpSession session;
	Guard g;
	String URL = "jdbc:mysql://localhost/eshop";
	String username = "root";
	String pwd = "";
	String my_error="";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public signUpServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (Exception e) {
			my_error = e.getMessage();
		}

	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {

			con = dajSpojenie(request);

			if (con == null) {
				out.println(my_error);
				return;
			}

		String operacia = request.getParameter("operacia");

		if (operacia == null) {
			operacia = "";
		}

		if (operacia.equals("sign_up")) {
			zaregistrujPouzivatela(out, response, request.getParameter("meno"), request.getParameter("priezvisko"),
					request.getParameter("adresa"), request.getParameter("login"), request.getParameter("heslo"));
		}
		
		zobrazBody(out);
		}catch(Exception e) {
			out.print(e);
		}
		

	}
	
	
	public void zobrazBody(PrintWriter out) {
		out.println("<link rel='stylesheet' href='styles.css'>");
		out.println(
				"<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css' integrity='sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm' crossorigin='anonymous'>");

		out.println("<body>");

		out.println("<br>");
		out.println("<div class='card' style='width: 18rem;'>");
		out.println("<div class='card-body'>");
		out.println("<div style='font-size:20px; font-weight:bold;'>Nový používateľ</div>");
		out.println("<br>");
		out.println("<form action='signUpServlet' method='post'>");
		out.println("<input type='hidden'  name='operacia' value='sign_up'>");
		out.println("<label>Meno</label><br><input autocomplete='off' type='text' name='meno' required><br>");
		out.println(
				"<label>Priezvisko</label><br><input autocomplete='off' type='text' name='priezvisko' required><br>");
		out.println("<label>Adresa</label><br><input type='text' style='height:60px;' name='adresa' required><br>");

		out.println("<label>Login</label><br><input autocomplete='off' type='text' name='login' required><br>");
		out.println("<label>Heslo</label><br><input autocomplete='off' type='password' name='heslo' required><br><br>");
		out.println("<input type='submit'  value='Zaregistrovať sa' class='dark-button'>");
		out.println("<br>");
		out.println("</form>");
		out.println(
				"<span class='card-text'>Už máte vytvorený účet?<br><a href='index.html' class='card-link'>Prihlásiť sa</a></span>");
		out.println("</div>");
		out.println("</div>");

		out.println("</body>");
	}

	public void zaregistrujPouzivatela(PrintWriter out, HttpServletResponse response, String meno, String priezvisko,
			String adresa, String login, String heslo) {
		// overime ci sa login este nepouziva

		try {
			Statement st = con.createStatement();
			String selectLogins = "SELECT login from users";
			st.executeQuery(selectLogins);

			ResultSet rs = st.executeQuery(selectLogins);
			while (rs.next()) {
				String zabrany = rs.getString("login");
				// ak je login uz zabrany, tak zobrazime alert
				if (login.equals(zabrany)) {
					out.print("<div class='alert alert-danger' role='alert' style='margin-bottom: 0px;'>");
					out.print("Daný login je obsadený. Zvoľte iný.");
					out.print("</div>");
					return;
				}

			}

			// ak sa login este nepouziva, tak ho zaregistrujeme

			Statement stmt = con.createStatement();

			String sql = "INSERT INTO users (login, passwd, adresa, meno, priezvisko) VALUES ('" + login + "', '"
					+ heslo + "','" + adresa + "','" + meno + "','" + priezvisko + "')";

			stmt.executeUpdate(sql);
			rs.close();
			stmt.close();

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Registrácia prebehla úspešne. ");
			out.print("<a href='index.html'>");

			out.print("Prihláste sa.");
			out.print("</a>");

			out.print("</div>");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			out.print(e);
		}

	}
	
	
	protected Connection dajSpojenie(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			Connection con = (Connection) session.getAttribute("spojenie");
			if (con == null) {
				con = DriverManager.getConnection(URL, username, pwd);
				session.setAttribute("spojenie", con); // zapis do session
				g = new Guard(con);
			}
			return con;
		} catch (Exception e) {
			return null;
		}
	}

}
