package ukf;

import java.sql.Connection;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class Guard implements HttpSessionBindingListener {
	Connection connection; // premenna pamatajuca si nase spojenie

	// nacitanie spojenia v konstruktore
	public Guard(Connection c) {
		connection = c;
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		/* nic */}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		try {
			if (connection != null)
				connection.close(); // uvolnenie
		} catch (Exception e) {
		}
	}
}