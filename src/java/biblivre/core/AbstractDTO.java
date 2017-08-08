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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.utils.TextUtils;

public abstract class AbstractDTO implements IFJson, Serializable {
	private static final long serialVersionUID = 1L;

	private Date created;
	private int createdBy;
	private Date modified;
	private int modifiedBy;

	private transient Map<String, IFJson> _extraData = new HashMap<String, IFJson>();

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();

		try {
			Class<?> clazz = this.getClass();

			while (clazz != Object.class) {

				for (Field field : clazz.getDeclaredFields()) {
					String name = field.getName();
					Method getter = null;
					
					try {
						getter = clazz.getDeclaredMethod("get" + StringUtils.capitalize(name));
					} catch (NoSuchMethodException e) {
						continue;
					}
	
					Object value = getter.invoke(this);

					if (value != null) {
						if (value instanceof IFJson) {
							json.putOpt(name, ((IFJson) value).toJSONObject());
						} else if (value instanceof Collection) {
							Collection<?> col = (Collection<?>) value;
	
							for (Object item : col) {
								if (item == null) {
									continue;
								}

								if (item instanceof IFJson) {
									json.append(name, ((IFJson) item).toJSONObject());
								} else if (value instanceof Date) {
									json.putOpt(name, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format((Date) value));
								} else if (item instanceof String) {
									json.append(name, ((String) item).trim());
								} else {
									json.append(name, item);
								}
							}
						} else if (value instanceof Date) {
							json.putOpt(name, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format((Date) value));
						} else if (value instanceof String) {
							json.putOpt(name, ((String) value).trim());
						} else {
							json.putOpt(name, value);
						}
					}
				}

				for (Entry<String, IFJson> e : this._extraData.entrySet()) {
					IFJson value = e.getValue();
					
					if (value instanceof DTOCollection) {
						json.put(e.getKey(), this.toJSONArray((DTOCollection<AbstractDTO>) value));
					} else {
						json.put(e.getKey(), value.toJSONObject());
					}
				}

				clazz = clazz.getSuperclass();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	public void addExtraData(String key, IFJson data) {
		this._extraData.put(key, data);
	}

	@SuppressWarnings("unchecked")
	public void populateExtraData(JSONObject json) throws JSONException {
		for (Entry<String, IFJson> e : this._extraData.entrySet()) {
			IFJson value = e.getValue();
			
			if (value instanceof DTOCollection) {
				json.put(e.getKey(), this.toJSONArray((DTOCollection<AbstractDTO>) value));
			} else {
				json.put(e.getKey(), value.toJSONObject());
			}
		}
	}
	
	protected JSONArray toJSONArray(List<? extends IFJson> list) {
		if (list == null) {
			return null;
		}
		
		JSONArray array = new JSONArray();
		for (IFJson e : list) {
			array.put(e.toJSONObject());
		}
		return array;
	}
	
	public String toJSONString() {
		return this.toJSONObject().toString();
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends AbstractDTO> T fromJSONObject(String jsonData) {
		try {
			JSONObject json = new JSONObject(jsonData);
			return this.fromJSONObject(json);
		} catch (JSONException je) {
		}
		
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends AbstractDTO> T fromJSONObject(JSONObject json) {
		Class<?> clazz = this.getClass();

		for (Field field : clazz.getDeclaredFields()) {
			try {
				String name = field.getName();
				Class<?> fieldClass = field.getType();
				Object value = null;

				Method setter = clazz.getDeclaredMethod("set" + StringUtils.capitalize(name), fieldClass);
				if (setter == null) {
					continue;
				}

				if (fieldClass.equals(String.class)) {
					value = json.has(name) ? json.getString(name) : null;
				} else if (fieldClass.equals(Integer.class)) {
					try {
						value = json.has(name) ? json.getInt(name) : null;
					} catch (Exception e) {
						value = -1;
					}
				} else if (fieldClass.equals(Float.class)) {
					String sValue = json.has(name) ? json.getString(name).replaceAll(",", ".") : null;
					value = sValue != null ? Float.valueOf(sValue) : null;
				} else if (fieldClass.equals(Date.class)) {
					value = json.has(name) ? json.getString(name) : null;
					value = value != null ? TextUtils.parseDate((String) value) : null;
				} else if (fieldClass.equals(Boolean.class) || fieldClass.equals(boolean.class)) {
					value = json.has(name) ? json.getBoolean(name) : null;
				}

				if (value == null) {
					continue;
				}

				setter.invoke(this, value);
			} catch (Exception e) {
				continue;
			}
		}
		return (T) this;
	}

	public final Date getCreated() {
		return this.created;
	}

	public final void setCreated(Date created) {
		this.created = created;
	}

	public final int getCreatedBy() {
		return this.createdBy;
	}

	public final void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public final Date getModified() {
		return this.modified;
	}

	public final void setModified(Date modified) {
		this.modified = modified;
	}

	public final int getModifiedBy() {
		return this.modifiedBy;
	}

	public final void setModifiedBy(int modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}
