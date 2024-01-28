package ukf;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * Servlet implementation class ordersServlet
 */
@WebServlet("/ordersServlet")
public class ordersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	String my_error = "";
	Integer id_pouzivatela;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ordersServlet() {
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

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();
		con = (Connection) (session.getAttribute("spojenie"));
		id_pouzivatela = (Integer) (session.getAttribute("ID"));

		if (con == null)
			out.print(my_error);

		zobrazBody(out, request);
		
		
	}

	
	public void zobrazBody(PrintWriter out, HttpServletRequest request) {
		out.println("<link rel='stylesheet' href='styles.css'>");
		out.println(
				"<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css' integrity='sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm' crossorigin='anonymous'>");
		out.println("<script src='https://kit.fontawesome.com/9481b93cfb.js' crossorigin='anonymous'></script>");

		out.println("<body>");
		zobrazHeader(out, request);
		out.println("<br>");

		try {
			Statement stmt = con.createStatement();

			out.print("<br><br>");

			out.println("<div class='row justify-content-center'><div class='col-md-4'>");

			out.print("<table class='table table-sm table-bordered table-striped '>");
			out.print("<thead class='thead-dark'>");
			out.print("<tr>");
			
			out.print("<th scope='col'>Dátum</th>");
			out.print("<th scope='col'>ID obj.</th>");
			out.print("<th scope='col'>Položka</th>");
			out.print("<th scope='col'>Cena položky</th>");
			out.print("<th scope='col'>Celková cena obj.(*po zľave)</th>");
			out.print("<th scope='col'>Stav obj.</th>");

			out.print("</tr>");
			out.print("</thead>");
			out.print("<tbody>");

			
			
			String sql2= "SELECT obj_polozky.ID_objednavky as ID_objednavky, obj_zoznam.stav as stav, obj_zoznam.suma as celkova_suma_objednavky, obj_zoznam.datum_objednavky as datum_objednavky, sklad.nazov as nazov, sklad.cena as cena_tovaru FROM obj_polozky join obj_zoznam on obj_polozky.ID_objednavky = obj_zoznam.obj_cislo JOIN sklad on obj_polozky.id_tovaru = sklad.id WHERE obj_zoznam.ID_pouzivatela = '"+id_pouzivatela+"'";
			ResultSet rs2 = stmt.executeQuery(sql2);

			while (rs2.next()) {

				out.println("<tr>");

				out.println("<td>");
				out.println(rs2.getString("datum_objednavky"));
				out.println("</td>");

				out.println("<td>");
				out.println(rs2.getString("ID_objednavky"));
				out.println("</td>");

				out.println("<td>");
				out.print(rs2.getString("nazov"));
				out.println("</td>");

				out.println("<td>");
				out.print(rs2.getString("cena_tovaru"));
				out.print("€");
				out.println("</td>");
				out.println("<td>");
				out.print(rs2.getString("celkova_suma_objednavky"));
				out.print("€");
				out.println("</td>");
				out.println("<td>");
				out.print(rs2.getString("stav"));
				out.println("</td>");

				
				out.println("</tr>");

			}

			stmt.close();

		} catch (Exception e) {

		}
		out.print("</tbody>");
		out.print("</table>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div></div>");
		out.println("</br></br>");

		out.println(
				"<form action='viewServlet' method='post'><div style='text-align: center;'><input type='submit' class='dark-button' value='Domov' name='operacia'></div></form>");

	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
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
