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
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Servlet implementation class viewServlet
 */

@WebServlet("/viewServlet")
public class viewServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	HttpSession session;
	Integer id_user;
	Guard g;
	String URL = "jdbc:mysql://localhost/eshop";
	String username = "root";
	String pwd = "";
	String my_error="";

	/**
	 * Default constructor.
	 */
	public viewServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
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
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		out.println("<link rel='stylesheet' href='styles.css'>");
		out.println(
				"<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css' integrity='sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm' crossorigin='anonymous'>");
		out.println("<script src='https://kit.fontawesome.com/9481b93cfb.js' crossorigin='anonymous'></script>");

		try {

			con = dajSpojenie(request);

			if (con == null) {
				out.println(my_error);
				return;
			}

			String operacia = request.getParameter("operacia");
			if (operacia == null) {
				zobrazNeopravnenyPristup(out);
				return;
			}

			if (operacia == "sign_up") {
				ZaregistrujUsera(out, request.getParameter("email"), request.getParameter("password"), request);
				return;
			}
			if (operacia.equals("login")) {
				OverUsera(out, request, response);

			}

			int user_id = getUserID(request);
			if (user_id == 0) {
				zobrazNeopravnenyPristup(out);
				return;
			}

			if (operacia.equals("pridatDoKosika")) {
				pridajDoKosika(out, request);

			}

			if (operacia.equals("logout")) {
				urobLogout(out, request);
				return;
			}

			zobrazHeader(out, request);
			zobrazBody(out, request);
		} catch (Exception e) {
			out.println("e");
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

	protected void pridajPrispevok(PrintWriter out, HttpServletRequest request) {

		try {

			String kontent = request.getParameter("kontent");
			int id_pouzivatela = getUserID(request);
			String datum = getDateTime();

			Statement stmt = con.createStatement();

			String sql = "INSERT INTO post (content, user_id, date) VALUES ('" + kontent + "', '" + id_pouzivatela
					+ "','" + datum + "')";
			stmt.executeUpdate(sql);

			stmt.close();

		} catch (Exception ex) {
			out.println(ex.getMessage());

		}
	}

	protected void pridajDoKosika(PrintWriter out, HttpServletRequest request) {

		try {

			int id_pouzivatela = getUserID(request);

			// String datum = getDateTime();
			String id_tovaru = request.getParameter("idTovaru");
			String cena = request.getParameter("cena");
			// String kusy = request.getParameter("pocetKs");
			String nazov = request.getParameter("nazov");

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Tovar '" + nazov + "' bol pridaný do košíka");
			out.print("</div>");

			Statement stmt = con.createStatement();

			String sql = "INSERT INTO kosik (ID_pouzivatela, ID_tovaru, cena, ks) VALUES ('" + id_pouzivatela + "', '"
					+ id_tovaru + "','" + cena + "','" + 1 + "')";

			stmt.executeUpdate(sql);

			stmt.close();

		} catch (Exception ex) {

			out.println(ex);
		}
	}

	private String zisti_ks_v_kosi(PrintWriter out, HttpServletRequest request) {

		String ks_v_kosi = "0";
		int id_pouzivatela = getUserID(request);
		try {
			Statement stmt = con.createStatement();

			String sql2 = "SELECT SUM(ks) as celkovo_ks from kosik where ID_pouzivatela = '" + id_pouzivatela + "'";
			ResultSet rs2 = stmt.executeQuery(sql2);

			while (rs2.next())
				ks_v_kosi = rs2.getString("celkovo_ks");

			stmt.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ks_v_kosi;

	}

	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	protected void zobrazNeopravnenyPristup(PrintWriter out) {
		out.println("Prístup zamietnutý.");
	}

	protected void OverUsera(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
		try {
			String email = request.getParameter("email");

			String heslo = request.getParameter("password");

			Statement stmt = con.createStatement();
			String sql = "SELECT COUNT(id) AS pocet FROM users WHERE login = '" + email + "' AND passwd= '" + heslo
					+ "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();

			HttpSession session = request.getSession();

			if (rs.getInt("pocet") == 1) { // ak je user s menom a heslo jeden - OK
				sql = "SELECT ID, meno, priezvisko, zlava, je_admin FROM users WHERE login = '" + email + "'";

				rs = stmt.executeQuery(sql);
				rs.next();

				int je_admin = rs.getInt("je_admin");

				// do session ulozim data
				session.setAttribute("ID", rs.getInt("ID"));
				session.setAttribute("meno", rs.getString("meno"));
				session.setAttribute("priezvisko", rs.getString("priezvisko"));
				session.setAttribute("zlava", rs.getInt("zlava"));
				session.setAttribute("je_admin", je_admin);

				if (je_admin == 1) {

					response.sendRedirect("adminServlet");

				}

			} else { // ak je userov 0 alebo viac – autorizacia sa nepodarila
				out.println("Chybné prihlasovacie údaje.");
				session.invalidate(); // vymazem session
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			out.println(ex.getMessage());
		}
	}

	protected void ZaregistrujUsera(PrintWriter out, String email, String password, HttpServletRequest request) {
		try {

			Statement stmt = con.createStatement();
			String sql = "SELECT COUNT(id) AS pocet FROM users WHERE login = '" + email + "' AND passwd= '" + password
					+ "'";
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();

			HttpSession session = request.getSession();

			if (rs.getInt("pocet") == 0) { // ak je user s menom a heslo jeden - OK

				try {

					String sql3 = "INSERT INTO users (login, passwd) VALUES (";
					sql3 += "'" + email + "', ";
					sql3 += "'" + password + "') ";

					stmt.executeUpdate(sql3);
					out.print("Účet bol úspešne vytvorený.");

					rs = stmt.executeQuery(sql); // prečítam jeho údaje z db
					rs.next(); // nastavím sa na prvý načítaný záznam
					session.setAttribute("ID", rs.getInt("ID")); // a do session povkladám data
					session.setAttribute("login", rs.getString("login"));
					session.setAttribute("passwd", rs.getString("passwd"));

				} catch (Exception e) {
					out.println(e);
				}

			} else { // ak je userov 0 alebo viac – autorizacia sa nepodarila
				out.println("Email sa už používa.");
				session.invalidate(); // vymazem session
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {
			out.println(ex.getMessage());
		}
	}

	protected int getUserID(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Integer id = (Integer) (session.getAttribute("ID"));
		if (id == null)
			id = 0;
		return id;
	}

	protected void vypisData(PrintWriter out, HttpServletRequest request) {
		try {
			Statement stmt = con.createStatement();

			String sql = "SELECT nazov, ks, cena, URL_obr from sklad";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {

				out.println("<form method='post' action='viewServlet'>");
				out.print(rs.getString("datum"));
				out.println("<b>" + " " + rs.getString("nick") + "</b>" + ":");
				out.println("<span style='width:500px !important; overflow-wrap: break-word;color: pink'>"
						+ rs.getString("kontent") + "</span>");

				out.println("<br />");

			}
			out.println("</div>");
			rs.close();
			stmt.close();
			out.println("<br /><br />");
			out.println("<form method='post' action='viewServlet'>");
			out.println("<input type='hidden' name='operacia' value='pridat'>");
			out.println("<textarea name='kontent' style='width:500px;height:100px'></textarea>");
			out.println("<br /><br />");
			out.println("<input type='submit' value='Pridať príspevok'>");
			out.println("</form>");

			out.println("<br /><br />");
			out.println("<form method='post' action='viewServlet'>");
			out.println("<input type='hidden' name='operacia' value='logout'>");
			out.println("<input type='submit' value='Odhlásiť sa'>");
			out.println("</form>");

			out.println("<form  method='post' action='banServlet'>");
			out.println("<input type=hidden name='operacia' value='spravovatPristup'>");
			out.println("<input type=hidden name='id' value='" + getUserID(request) + "'>");
			out.println("<input type='submit' value = 'Spravovať prístup'>");
			out.println("</form>");

		} catch (Exception ex) {
			out.println("Uz by nemuseli byt chyby");
		}
	}

	protected void urobLogout(PrintWriter out, HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();

		out.println("<meta http-equiv='Refresh' content='0.1;url=index.html'>");

	}

	private void zobrazHeader(PrintWriter out, HttpServletRequest request) {
		HttpSession ses = request.getSession();
		String vypis = (String) ses.getAttribute("meno") + " " + (String) ses.getAttribute("priezvisko");

//bootstrap navigation
		out.println("<nav class='navbar navbar-dark bg-dark navbar-expand-lg'>");
		out.println("<a class='navbar-brand' href='#'>ScentsLab</a>");
		out.println("<div id='navbarNav'>");
		out.println("<ul class='navbar-nav'>");

		out.println("<li class='nav-item'>");
		out.print(
				"<form action='viewServlet' method='post' style='display: inline-block;margin-top:7px;margin-right:7px;'>");
		out.print("<input type='submit' value='Domov' name='operacia' style='cursor:pointer;'>");
		out.println("</form>");
		out.println("</li>");

		String ks = zisti_ks_v_kosi(out, request);
		if (ks == null)
			ks = "0";

		out.println("<li class='nav-item'>");
		out.print(
				"<form action='cartServlet' method='post' style='display: inline-block;margin-top:7px;margin-right:7px;'>");
		out.println("<input type='hidden' name='operacia' value='do_kosika'>");
		out.println("<input type='hidden' name='ks' value='" + ks + "'>");
		out.println("<input type='hidden' name='current_user' value='" + getUserID(request) + "'>");
		out.println("<input type='hidden' name='zlava' value='" + ses.getAttribute("zlava") + "'>");

		out.println("<input type='hidden' value='" + vypis + "'>");
		out.print("<input type='submit' value='Košík(" + ks + ")' style='cursor:pointer;'>");
		out.println("</form>");
		out.println("</li>");

		out.println("<li class='nav-item'>");
		out.print(
				"<form action='ordersServlet' method='post' style='display: inline-block;margin-top:7px;margin-right:7px;'>");
		out.println("<input type='hidden' name='operacia' value='do_kosika'>");
		out.println("<input type='hidden' name='ks' value='" + ks + "'>");
		out.println("<input type='hidden' name='current_user' value='" + getUserID(request) + "'>");
		out.println("<input type='hidden' value='" + vypis + "'>");

		out.print("<input type='submit' value='Objednávky' style='cursor:pointer;'>");
		out.println("</form>");
		out.println("</li>");

		out.println("<li class='nav-item'>");
		out.println("<form action='viewServlet' method='post' class='nav-link'>");
		out.println("<input type='hidden' name='operacia' value='logout'>");
		out.println("<input type='submit' value='Odhlásiť(" + vypis + ")' style='margin-left:15px;cursor: pointer;'>");
		out.println("</form>");
		out.println("</li>");

		out.println("</ul>");

		out.println("</div>");

		out.println("</nav>");

	}

	private void zobrazBody(PrintWriter out, HttpServletRequest request) {
		try {
			Statement stmt = con.createStatement();

			String sql = "SELECT ID, nazov, ks, cena, URL_obr from sklad";
			ResultSet rs = stmt.executeQuery(sql);

			out.println("<br>");

			out.println("<div class='container'>");
			out.println("<div class='row justify-content-center'>");

			while (rs.next()) {

				String nazov = rs.getString("nazov");
				int cena = rs.getInt("cena");
				String URL = rs.getString("URL_obr");
				int id_parfemu = rs.getInt("ID");

				out.println("<div class='col-4'>");
				out.println("<div class='img-div'>");
				out.println("<img width='180px' height='200px' src=" + URL + "'>");
				out.println("</div>");
				out.println("<div class='perfume-name'><b>" + nazov + "</b></div>");
				out.println("<div class='perfume-price'>" + cena + "€</div>");
				out.println("<div>");
				out.println("<form method='post' action='viewServlet'>");
				out.println("<input type='hidden' name='operacia' value='pridatDoKosika'>");
				out.println("<input type='hidden' name='idTovaru' value ='" + id_parfemu + "'>");
				out.println("<input type='hidden' name='cena' value ='" + cena + "'>");
				out.println("<input type='hidden' name='nazov' value ='" + nazov + "'>");

				out.println("<input type='submit' class='dark-button' value='Pridať do košíka'>");

				out.println("</form>");

				out.println("</div>");

				out.println("</div>");

			}
			rs.close();
			stmt.close();
			out.println("</div>");
			out.println("</div>");
			out.println("<br>");
			out.println("<br>");
		} catch (Exception e) {
			out.println("hosi");
		}

	}

}