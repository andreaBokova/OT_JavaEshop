package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
 * Servlet implementation class cartServlet
 */
@WebServlet("/cartServlet")
public class cartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	Integer id_pouzivatela;
	Integer zakaznicka_zlava;
	String my_error = "";
	Statement stmt;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public cartServlet() {
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
		 id_pouzivatela = (Integer) (session.getAttribute("ID"));
		zakaznicka_zlava = (Integer) (session.getAttribute("zlava"));

		if (con == null)
			out.print(my_error);

		String operacia = request.getParameter("operacia");

		if (operacia == null)
			operacia = "";

		if (operacia.equals("mazanie")) {
			vymazPolozku(out, request.getParameter("id"), id_pouzivatela);
		}

		if (operacia.equals("vytvorObjednavku")) {
			vytvorNovuObjednavku(out);
		}

		out.println("<link rel='stylesheet' href='styles.css'>");
		out.println(
				"<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css' integrity='sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm' crossorigin='anonymous'>");
		out.println("<script src='https://kit.fontawesome.com/9481b93cfb.js' crossorigin='anonymous'></script>");

		out.println("<body>");
		zobrazHeader(out, request);
		out.println("<br>");

		int total = 0;
		int new_total = 0;
		try {
			 stmt = con.createStatement();

			// zistujeme si ci nie je kosik prazdny, ak je, tak nezobrazime tlacidlo
			// 'Odoslat objednavku'
			ResultSet pr = stmt.executeQuery("SELECT COUNT(ID) as poc FROM kosik WHERE ID_pouzivatela ='" + id_pouzivatela + "'");
			pr.next();
			int pocet_riadkov = pr.getInt("poc");

			String sql = "SELECT kosik.ID as ID, sklad.nazov AS tovar, kosik.ID_tovaru AS id_tovaru, kosik.cena,  kosik.ks from kosik JOIN sklad ON sklad.ID=kosik.ID_tovaru WHERE kosik.ID_pouzivatela ='"
					+ id_pouzivatela + "'";
			ResultSet rs = stmt.executeQuery(sql);

			out.print("<br><br><br>");

			out.println("<div class='row justify-content-center'><div class='col-md-4'>");

			out.print("<table class='table table-sm table-bordered table-striped '>");
			out.print("<thead class='thead-dark'>");
			out.print("<tr>");
			out.print("<th scope='col'>Tovar</th>");
			out.print("<th scope='col'>Počet ks</th>");
			out.print("<th scope='col'>Cena</th>");
			if (zakaznicka_zlava > 0) {
				out.print("<th scope='col'>Cena po zľave</th>");
			}

			out.print("<th scope='col'><i class=\"fa-regular fa-trash-can\"></i></th>");
			out.print("</tr>");
			out.print("</thead>");
			out.print("<tbody>");

			while (rs.next()) {
				String cena = rs.getString("cena");
				total += Integer.parseInt(cena);
				new_total = total * (100 - zakaznicka_zlava) / 100;

				out.println("<tr>");
				out.println("<td>");
				out.println(rs.getString("tovar"));
				out.println("</td>");
				out.println("<td>");
				out.println(rs.getString("ks"));
				out.println("</td>");
				out.println("<td>");

				out.print(cena);
				out.print("€");

				out.println("</td>");

				if (zakaznicka_zlava > 0) {
					out.println("<td>");
					out.print(Double.parseDouble(cena) * (100 - zakaznicka_zlava) / 100);
					out.print("€");

					out.println("</td>");
				}

				// zmaz
				out.println("<td>");
				out.println("<form action='cartServlet' method='post'  style='display: inline'>");
				out.println("<input type='hidden' name='operacia' value='mazanie'>");

				out.println("<input type=hidden name='id' value='" + rs.getString("ID") + "'>");
				out.println("<input type='submit' value='odstráň'>");
				out.println("</form>");
				out.print("</td>");

				out.println("</tr>");

			} // koniec while loop
			stmt.close();

			out.print("</tbody>");
			out.print("</table>");
			out.print("<b>MEDZISÚČET: </b>");

			out.print(new_total);

			out.println("€");
			out.print("<br>");
			if (zakaznicka_zlava > 0 & total > 0) {
				out.print("<div>");
				out.print("*bola uplatnená Vaša zákaznícka zľava ");
				out.print(zakaznicka_zlava);
				out.print("%");
				out.println("</div>");
				out.println();
			}
			out.println("</div>");
			out.println("</div>");
			out.println("</div></div>");

			out.println("</br></br>");

			if (pocet_riadkov > 0) {

				out.println("<form action='cartServlet' method='post'>");

				out.println(
						"<input type='hidden' name='operacia' value='vytvorObjednavku'><div style='text-align: center;'><input type='submit' class='dark-button' value='Odoslať objednávku'></div>");

				out.println("</form>");
			}

		} catch (Exception e) {

		}

	}

	// do bloku synchronized dame prikazy, co sa musia vykonat spolu
	public void vytvorNovuObjednavku(PrintWriter out) {
		try {
			synchronized (this) {
				boolean je_dost = skontrolujDostatokTovaru(out, id_pouzivatela);

				if (je_dost) {
					int CisloObj = zistiCisloNovejObjednavky();
					preklopKosik(CisloObj, zakaznicka_zlava, id_pouzivatela, out);

				}

			}
		} catch (Exception e) {
			out.print(e);
		}
	}

	private int zistiCisloNovejObjednavky() {
		int cisloNovejObjednavky = 0;
		try {
			Statement stmt = con.createStatement();
			ResultSet rsCisloPoslednejObjednavky = stmt
					.executeQuery("SELECT MAX(obj_cislo) as posledna_objednavka from obj_zoznam");
			rsCisloPoslednejObjednavky.next();
			cisloNovejObjednavky = rsCisloPoslednejObjednavky.getInt("posledna_objednavka");
			cisloNovejObjednavky += 1;

		} catch (Exception e) {
			e.getMessage();
		}
		return cisloNovejObjednavky;

	}

	private boolean skontrolujDostatokTovaru(PrintWriter out, Integer id_pouzivatela) {
		boolean dostatok=true;
		try {
			Statement stmt = con.createStatement();
			// zistime, kolko akeho tovaru je v kosiku a kolko akeho tovaru je na sklade
			String sql = "SELECT kosik.ID_tovaru, COUNT(kosik.ID_tovaru) AS ks_v_kosiku, sklad.ks as ks_na_sklade, sklad.nazov AS nazov_tovaru FROM kosik join sklad on kosik.ID_tovaru=sklad.ID where kosik.ID_pouzivatela='"
					+ id_pouzivatela + "' GROUP BY ID_tovaru";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String nazov_tovaru = rs.getString("nazov_tovaru");
				int pocet_ks_v_kosiku = rs.getInt("ks_v_kosiku");
				int pocet_ks_na_sklade = rs.getInt("ks_na_sklade");

				if (pocet_ks_v_kosiku > pocet_ks_na_sklade) {
					dostatok = false;
					out.print("<div class='alert alert-danger' role='alert' style='margin-bottom: 0px;'>");
					out.print("Zvolený počet kusov nie je možné objednať. Tovar " + nazov_tovaru
							+ " je na sklade v zostatku  " + pocet_ks_na_sklade + ".");
					out.print("</div>");
					

				}
			}
			

			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			out.print(e);
		}

		return dostatok;
	}

	private void preklopKosik(int cisloNovejObj, Integer zakaznicka_zlava, Integer id_pouzivatela, PrintWriter out) {

		try {

			 stmt = con.createStatement();
			
			// vyberieme polozky z kosika
			String sql = "SELECT kosik.ID as ID,   sklad.nazov AS tovar, kosik.ID_pouzivatela as user_id, kosik.ID_tovaru AS id_tovaru, kosik.cena,  kosik.ks from kosik JOIN sklad ON sklad.ID=kosik.ID_tovaru WHERE  kosik.ID_pouzivatela ='"
					+ id_pouzivatela + "'";
			ResultSet rs = stmt.executeQuery(sql);

			Statement stmt2=con.createStatement();
			while (rs.next()) {

				String ID_tovaru = rs.getString("id_tovaru");

				int ks = rs.getInt("kosik.ks");
				int cena = rs.getInt("kosik.cena");
				int cena_polozky_po_zlave = cena * (100 - zakaznicka_zlava) / 100;

				String nazov_tovaru = rs.getString("tovar");

				// Zistime kolko ks konkretnej polozky je na sklade, aby sme od nich mohli
				// odcitat tie, kt. prave objednal user
				String selectZoSkladu = "SELECT ks from sklad where ID = '" + ID_tovaru + "'";
				ResultSet rs2 = stmt2.executeQuery(selectZoSkladu);
				rs2.next();
				int ks_na_sklade = rs2.getInt("ks");

				int aktualizovane_kusy = 0;

				aktualizovane_kusy = ks_na_sklade - ks;

				// nastavime v sklade aktualizovane kusy
				PreparedStatement ps = con.prepareStatement("UPDATE sklad SET ks=? WHERE id=?");
				ps.setInt(1, aktualizovane_kusy);
				ps.setString(2, ID_tovaru);
				ps.executeUpdate();

				// vlozime do obj_polozky

				String sql2 = "INSERT INTO obj_polozky (ID_objednavky, ID_tovaru, cena, ks) VALUES ('" + cisloNovejObj
						+ "', '" + ID_tovaru + "','" + cena_polozky_po_zlave + "','" + ks + "')";

				stmt2.executeUpdate(sql2);

			}
			
			String sql5 = "SELECT SUM(cena) as total from(SELECT cena*ks as cena from kosik where ID_pouzivatela = '"
					+ id_pouzivatela + "'  group by ID)temp";

			ResultSet rs5 = stmt.executeQuery(sql5);
			rs5.next();
			int suma = rs5.getInt("total");
			int suma_po_zlave = suma * (100 - zakaznicka_zlava) / 100;

			// dame objednavku do obj_zoznam
			String sql4 = "INSERT INTO obj_zoznam (obj_cislo, ID_pouzivatela, suma) VALUES ('" + cisloNovejObj + "', '"
					+ id_pouzivatela + "','" + suma_po_zlave + "')";

			stmt.executeUpdate(sql4);

			// vymazeme kosik

			String sql3 = "DELETE from kosik WHERE kosik.ID_pouzivatela ='" + id_pouzivatela + "'";
			stmt.executeUpdate(sql3);

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Objednávka bola úspešne odoslaná.");
			out.print("</div>");
			
			stmt.close();
			stmt2.close();

		} catch (Exception e) {
			out.println(e);
		}

	}
	// koniec preklopKosik

	private void vymazPolozku(PrintWriter out, String id, Integer id_pouzivatela) {

		try {

			String sql = "SELECT ks from kosik  WHERE ID = '" + id + "' AND ID_pouzivatela = '" + id_pouzivatela + "' ";

			Statement stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String kusy = rs.getString("ks");

			if (kusy == "NULL")
				kusy = "1";
			


			int pocet_ks_po_mazani = Integer.parseInt(kusy) - 1;
			if (pocet_ks_po_mazani > 0) {

				PreparedStatement ps = con.prepareStatement("UPDATE kosik SET ks=? WHERE id=? AND id_pouzivatela =?");
				ps.setInt(1, pocet_ks_po_mazani);
				ps.setString(2, id);
				ps.setInt(3, id_pouzivatela);

				ps.executeUpdate();

			} else {

				String sql2 = "DELETE FROM kosik WHERE (ID =  '" + id + "') AND ID_pouzivatela = '" + id_pouzivatela
						+ "'";
				stmt.executeUpdate(sql2);
			}

			out.print("<div class='alert alert-success' role='alert' style='margin-bottom: 0px;'>");
			out.print("Bola odstránená 1 položka");
			out.print("</div>");

		} catch (Exception e) {
			out.println(e);
		}

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
				"<form action='viewServlet' method='post' style='display: inline-block;margin-top:7px;margin-right:7px;cursor:pointer;'><input type='submit' value='Domov' name='operacia' style='cursor:pointer;'></form>");

		out.println("</li>");
		out.println("<li class='nav-item'>");

		out.println("<a class='nav-link' href='cartServlet'>Košík</a>");

		out.println("</li>");
		out.println("<li class='nav-item'>");
		out.println("<a class='nav-link' href='ordersServlet'>Objednávky</a>");
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

}
