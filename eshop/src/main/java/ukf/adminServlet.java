package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class adminServlet
 */
@WebServlet("/adminServlet")
public class adminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	String my_error = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public adminServlet() {
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

		HttpSession session = request.getSession();
		con = (Connection) (session.getAttribute("spojenie"));

		Integer id_admina = (Integer) (session.getAttribute("ID"));

		if (con == null)
			out.print(my_error);

		String operacia = request.getParameter("operacia");

		if (operacia == null) {
			operacia = "";
		}

		if (operacia.equals("udelAdminStatus")) {
			udelAdminStatus(out, request.getParameter("id"));
		}

		if (operacia.equals("odoberAdminStatus")) {
			odoberAdminStatus(out, request.getParameter("id"));
		}

		if (operacia.equals("zmenStavSpracovana")) {
			zmenStavSpracovana(out, request.getParameter("id"));
		}

		if (operacia.equals("zmenStavOdoslana")) {
			zmenStavOdoslana(out, request.getParameter("id"));
		}

		if (operacia.equals("zmenStavZaplatena")) {
			zmenStavZaplatena(out, request.getParameter("id"));
		}

		if (operacia.equals("vymazObjednavku")) {
			vymazObjednavku(out, request.getParameter("id"));
		}

		zobrazBody(out, id_admina);

	}

	private void zobrazBody(PrintWriter out, int id_admina) {
		out.println("<link rel='stylesheet' href='styles.css'>");
		out.println(
				"<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css' integrity='sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm' crossorigin='anonymous'>");
		out.println("<script src='https://kit.fontawesome.com/9481b93cfb.js' crossorigin='anonymous'></script>");

		out.println("<br>");

		try {

			String sql = "SELECT ID, login, meno, priezvisko, adresa, je_admin FROM users WHERE ID <> '" + id_admina
					+ "'";
			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			out.print("<br>");
			out.println("<div style='text-align:center;'><img  src='imgs/icon-admin.png' style='width:60px;'></div>");

			out.println("<div class='row justify-content-center'><div class='col-md-6'>");

			out.print("<table class='table table-md table-bordered table-striped '>");
			out.print("<thead class='thead-dark'>");
			out.print("<tr>");
			out.print("<th scope='col'>ID</th>");
			out.print("<th scope='col'>Login</th>");
			out.print("<th scope='col'>Meno</th>");
			out.print("<th scope='col'>Priezvisko</th>");
			out.print("<th scope='col'>Status</th>");
			out.print("<th scope='col'>Upraviť status</th>");

			out.print("</tr>");
			out.print("</thead>");
			out.print("<tbody>");

			while (rs.next()) {
				String je_admin = rs.getString("je_admin");
				String id_user = rs.getString("ID");
				out.println("<tr>");

				out.println("<td>");
				out.println(id_user);
				out.println("</td>");
				out.println("<td>");
				out.println(rs.getString("login"));
				out.println("</td>");
				out.println("<td>");
				out.println(rs.getString("meno"));
				out.println("</td>");
				out.println("<td>");
				out.println(rs.getString("priezvisko"));
				out.println("</td>");
				out.println("<td>");
				if (je_admin.equals("1"))
					out.println("<b>admin</b>");
				else
					out.println("používateľ");
				out.println("</td>");
				out.println("<td>");
				if (je_admin.equals("1")) {

					out.println("<form action='adminServlet' method='post' style='display: inline'>");
					out.println("<input type=hidden name='id' value='" + id_user + "'>");
					out.println("<input type='hidden' name='operacia' value='odoberAdminStatus'>");
					out.println("<input type='submit' value='Odober status admina' style='margin-left:10px'>");
					out.println("</form>");
				} else {
					out.println("<form action='adminServlet' method='post' style='display: inline'>");
					out.println("<input type=hidden name='id' value='" + id_user + "'>");
					out.println("<input type='hidden' name='operacia' value='udelAdminStatus'>");
					out.println("<input type='submit' value='Udeľ status admina' style='margin-left:10px'>");
					out.println("</form>");

				}
				out.println("</td>");
				out.println("<br />");
				out.println("</tr>");

			}
			out.print("</tbody>");
			out.print("</table>");
			out.println("</div>");
			rs.close();
			stmt.close();

		} catch (Exception e) {

		}

		// tabulka objednavok
		out.print("</br>");
		out.print("</br>");
		out.print("</br>");
		out.println("<div class='row justify-content-center'><div class='col-md-9'>");

		out.print("<span style='text-decoration: underline;'>Zoznam objednávok:</span>");
		out.print("<table class='table table-md table-bordered table-striped '>");
		out.print("<thead class='thead-dark'>");
		out.print("<tr>");
		out.print("<th scope='col'>Číslo objednávky</th>");
		out.print("<th scope='col'>Dátum objednávky</th>");
		out.print("<th scope='col'>ID používateľa</th>");
		out.print("<th scope='col'>Suma</th>");
		out.print("<th scope='col'>Stav</th>");
		out.print("<th scope='col'>Upraviť stav</th>");
		out.print("<th scope='col'><i class='fa-regular fa-trash-can'></i></th>");
		out.print("</tr>");
		out.print("</thead>");
		out.print("<tbody>");

		try {

			String sql = "SELECT ID, obj_cislo, datum_objednavky, ID_pouzivatela, suma, stav FROM obj_zoznam ORDER BY obj_cislo";
			Statement stmt = con.createStatement();
			

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String id = rs.getString("ID");
				String obj_cislo = rs.getString("obj_cislo");
				String datum_objednavky = rs.getString("datum_objednavky");
				String ID_pouzivatela = rs.getString("ID_pouzivatela");
				String suma = rs.getString("suma");
				String stav = rs.getString("stav");

				out.println("<tr>");

				out.println("<td>");
				out.println(obj_cislo);
				out.println("</td>");
				out.println("<td>");
				out.println(datum_objednavky);
				out.println("</td>");
				out.println("<td>");
				out.println(ID_pouzivatela);
				out.println("</td>");
				out.println("<td>");
				out.println(suma);
				out.println("</td>");
				out.println("<td>");
				out.println("<b>");
				out.println(stav);
				out.println("</b>");
				out.println("</td>");
				out.println("<td>");

				out.println("<form action='adminServlet' method='post' style='display: inline'>");
				out.println("<input type=hidden name='id' value='" + id + "'>");
				out.println("<input type='hidden' name='operacia' value='zmenStavSpracovana'>");
				out.println("<input type='submit' value='spracovaná' style='margin-left:10px'>");
				out.println("</form>");
				out.println("<form action='adminServlet' method='post' style='display: inline'>");
				out.println("<input type=hidden name='id' value='" + id + "'>");
				out.println("<input type='hidden' name='operacia' value='zmenStavOdoslana'>");
				out.println("<input type='submit' value='odoslaná' style='margin-left:10px'>");
				out.println("</form>");
				out.println("<form action='adminServlet' method='post' style='display: inline'>");
				out.println("<input type=hidden name='id' value='" + id + "'>");
				out.println("<input type='hidden' name='operacia' value='zmenStavZaplatena'>");
				out.println("<input type='submit' value='zaplatená' style='margin-left:10px'>");
				out.println("</form>");
				out.println("</td>");

				out.print("<td>");
				out.println("<form action='adminServlet' method='post' style='display: inline'>");
				out.println("<input type=hidden name='id' value='" + id + "'>");
				out.println("<input type='hidden' name='operacia' value='vymazObjednavku'>");
				out.println("<input type='submit' value='vymaž objednávku' style='margin-left:10px'>");
				out.println("</form>");
				out.println("</td>");

				out.println("</tr>");

			}

			out.print("</tbody>");
			out.print("</table>");

			out.println("</br></br>");
			rs.close();
			stmt.close();

		} catch (Exception e) {
			out.print(e);
		}

		out.println("<form method='post' action='viewServlet'>");
		out.println("<input type='hidden' name='operacia' value='logout'>");
		out.println("<div style='text-align: center;'>");
		out.println("<input type='submit' value='Odhlásiť sa' class='dark-button'>");
		out.println("</div>");

		out.println("</form>");
		out.println("</div></div>");

	}

	private void udelAdminStatus(PrintWriter out, String id_user) {

		try {

			PreparedStatement ps = con.prepareStatement("UPDATE users SET je_admin=? WHERE id=?");
			ps.setInt(1, 1);
			ps.setString(2, id_user);
			ps.executeUpdate();
			ps.close();
			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Status admina bol udelený.");
			out.print("</div>");

		} catch (Exception e) {
			out.println(e);
		}
	}

	private void odoberAdminStatus(PrintWriter out, String id_user) {

		try {

			PreparedStatement ps = con.prepareStatement("UPDATE users SET je_admin=? WHERE id=?");
			ps.setInt(1, 0);
			ps.setString(2, id_user);
			ps.executeUpdate();
			ps.close();

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Status admina bol odobraný.");
			out.print("</div>");

		} catch (Exception e) {
			out.println(e);
		}
	}

	private void zmenStavSpracovana(PrintWriter out, String id_obj) {

		try {

			PreparedStatement ps = con.prepareStatement("UPDATE obj_zoznam SET stav=? WHERE id=?");
			ps.setString(1, "spracovaná");
			ps.setString(2, id_obj);
			ps.executeUpdate();
			ps.close();

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Stav objednávky bol zmenený.");
			out.print("</div>");

		} catch (Exception e) {
			out.println(e);
		}
	}

	private void zmenStavOdoslana(PrintWriter out, String id_obj) {

		try {

			PreparedStatement ps = con.prepareStatement("UPDATE obj_zoznam SET stav=? WHERE id=?");
			ps.setString(1, "odoslaná");
			ps.setString(2, id_obj);
			ps.executeUpdate();
			ps.close();

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Stav bol zmenený.");
			out.print("</div>");

		} catch (Exception e) {
			out.println(e);
		}
	}

	private void zmenStavZaplatena(PrintWriter out, String id_obj) {

		try {

			PreparedStatement ps = con.prepareStatement("UPDATE obj_zoznam SET stav=? WHERE id=?");
			ps.setString(1, "zaplatená");
			ps.setString(2, id_obj);
			ps.executeUpdate();
			ps.close();

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Stav bol zmenený.");
			out.print("</div>");

		} catch (Exception e) {
			out.println(e);
		}

	}

	private void vymazObjednavku(PrintWriter out, String id_obj) {
		try {

			// pozrieme sa ake polozky su v obj.
			// vratime ich do skladu
			// odstranime objednavku

			Statement stmt = con.createStatement();
			Statement stmt2 = con.createStatement();


			String selectCisloObj = "SELECT obj_cislo from obj_zoznam where ID ='" + id_obj + "'";
			ResultSet RS = stmt.executeQuery(selectCisloObj);
			RS.next();
			int cisloObj = RS.getInt("obj_cislo");
			String selectPolozkyZObjednavky = "SELECT ID_tovaru, ks from obj_polozky join obj_zoznam on obj_polozky.ID_objednavky=obj_zoznam.obj_cislo where obj_zoznam.obj_cislo='"
					+ cisloObj + "'";
			ResultSet rS = stmt.executeQuery(selectPolozkyZObjednavky);
			while (rS.next()) {
				int id_tovaru = rS.getInt("ID_tovaru");
				int vratene_kusy = rS.getInt("ks");

				// Zistime kolko ks konkretnej polozky je na sklade, aby sme k nim mohli
				// pripocitat dalsie
				String selectZoSkladu = "SELECT ks from sklad where ID = '" + id_tovaru + "'";
				ResultSet rs2 = stmt2.executeQuery(selectZoSkladu);
				rs2.next();
				int ks_na_sklade = rs2.getInt("ks");
				int aktualizovane_kusy = ks_na_sklade + vratene_kusy;

				// nastavime v sklade aktualizovane kusy
				PreparedStatement ps = con.prepareStatement("UPDATE sklad SET ks=? WHERE id=?");
				ps.setInt(1, aktualizovane_kusy);
				ps.setInt(2, id_tovaru);
				ps.executeUpdate();
				ps.close();
			}

			// vymazeme objednavku

			String sql3 = "DELETE from obj_zoznam WHERE ID ='" + id_obj + "'";
			stmt.executeUpdate(sql3);

			// oznamime uspesne vymazanie
			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Objednávka bola odstránená.");
			out.print("</div>");
			stmt.close();
			stmt2.close();


		} catch (Exception e) {
			out.println(e);
		}
	}

}
