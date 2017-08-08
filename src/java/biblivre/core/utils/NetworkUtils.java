/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class NetworkUtils {
	public static boolean isLocalRequest(HttpServletRequest request) throws UnknownHostException {
		InetAddress address = NetworkUtils.remoteIp(request);

		if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
	        return true;
		}

	    try {
	        return NetworkInterface.getByInetAddress(address) != null;
	    } catch (SocketException e) {
	        return false;
	    }
	}

	public static InetAddress remoteIp(final HttpServletRequest request) throws UnknownHostException {
		final Enumeration<String> headers = request.getHeaders("X-Forwarded-for");

		if (headers != null) {
			while (headers.hasMoreElements()) {
				final String[] ips = headers.nextElement().split(",");
				for (int i = 0; i < ips.length; i++) {
					final String proxy = ips[i].trim();
					if (!"unknown".equals(proxy) && !proxy.isEmpty()) {
						try {
							return InetAddress.getByName(proxy);
						} catch (UnknownHostException e) {
						}
					}
				}
			}
		}

		return InetAddress.getByName(request.getRemoteAddr());
	}
}
