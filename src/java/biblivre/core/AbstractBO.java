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
package biblivre.core;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import biblivre.core.utils.Constants;
import biblivre.core.utils.Pair;

public abstract class AbstractBO {
	protected Logger logger = Logger.getLogger(this.getClass());
	protected String schema;

	private static HashMap<Pair<Class<? extends AbstractBO>, String>, AbstractBO> instances = new HashMap<Pair<Class<? extends AbstractBO>, String>, AbstractBO>();
	
	@SuppressWarnings("unchecked")
	protected static <T extends AbstractBO> T getInstance(Class<T> cls, String schema) {
		Pair<Class<? extends AbstractBO>, String> pair = new Pair<Class<? extends AbstractBO>, String>(cls, schema);
		T instance = (T)AbstractBO.instances.get(pair);

		if (instance == null) {
			if (!AbstractBO.class.isAssignableFrom(cls)) {
				throw new IllegalArgumentException("BO: getInstance: Class " + cls.getName() + " is not a subclass of BO.");
			}

			try {
				instance = cls.newInstance();
				instance.setSchema(schema);

				AbstractBO.instances.put(pair, instance);
			} catch (Exception ex) { }
		}

		return instance;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getSchema() {
		return StringUtils.defaultString(this.schema, Constants.GLOBAL_SCHEMA);
	}
	
	public boolean isGlobalSchema() {
		return this.getSchema().equals(Constants.GLOBAL_SCHEMA);
	}
	
}
