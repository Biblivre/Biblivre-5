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


import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class Pair<L, R> implements Serializable, JSONString {
	private static final long serialVersionUID = 1L;

	private final L left;
	private final R right;

	public Pair(final L left, final R right) {
		this.left = left;
		this.right = right;
	}
	
	public R getRight() {
		return this.right;
	}

	public L getLeft() {
		return this.left;
	}

	public static <A, B> Pair<A, B> create(A left, B right) {
		return new Pair<A, B>(left, right);
	}

	@Override
	public final boolean equals(Object o) {
		if (!(o instanceof Pair<?,?>)) {
			return false;
		}

		final Pair<?, ?> other = (Pair<?, ?>) o;
		return Pair.equal(this.getLeft(), other.getLeft()) && Pair.equal(this.getRight(), other.getRight());
	}

	public static final boolean equal(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		
		return o1.equals(o2);
	}

	@Override
	public int hashCode() {
		int hLeft = getLeft() == null ? 0 : getLeft().hashCode();
		int hRight = getRight() == null ? 0 : getRight().hashCode();

		return hLeft + (37 * hRight);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('<');
		if (this.left == null) {
			sb.append("null");
		} else {
			sb.append(this.left.toString());
		}
		sb.append(", ");
		if (this.right == null) {
			sb.append("null");
		} else {
			sb.append(this.right.toString());
		}
		sb.append('>');
		return sb.toString();
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();

		try {
			json.putOpt("left", this.getLeft());
			json.putOpt("right", this.getRight());
		} catch (JSONException e) {

		}

		return json.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <A, B> Pair<A, B> fromJSONObject(JSONObject json) {
		try {
			A left = (A)json.get("left");
			B right = (B)json.get("right");

			return new Pair<A, B>(left, right);
		} catch (JSONException e) {

		}
		return null;
	}
}
