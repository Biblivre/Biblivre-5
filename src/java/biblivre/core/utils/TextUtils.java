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

import java.io.InputStream;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class TextUtils {
	public static String encodePassword(String password) {
		if (StringUtils.isBlank(password)) {
			throw new IllegalArgumentException("Password is null");
		}

		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(password.getBytes("UTF-8"));
			byte[] pass = new Base64().encode(md.digest());
			
			return new String(pass);
		} catch(Exception e) {
			return "";
		}
	}
	
	public static boolean endsInValidCharacter(String str) {
		if (str == null) {
			return true;
		}
		
		String lastChar = String.valueOf(str.charAt(str.length() - 1));
		
		return TextUtils.removeDiacriticals(lastChar).matches("[0-9a-zA-Z]");
	}
	
	public static String camelCase(String str) {
		if (StringUtils.isBlank(str)) {
			return "";
		}

		String[] terms = StringUtils.split(str.toLowerCase(), "_");
		
		StringBuilder result = new StringBuilder(terms[0]);
		
		for (int i = 1; i < terms.length; i++) {
			result.append(StringUtils.capitalize(terms[i]));
		}

		return result.toString();
	}
	
	public static String biblivreEncode(String input) {
		if (input == null) {
			return "";
		}
		
		return StringUtils.reverse(Base64.encodeBase64String(input.getBytes()));
	}

	public static String biblivreDecode(String input) {
		if (input == null) {
			return "";
		}
		
		return new String(Base64.decodeBase64(StringUtils.reverse(input)));
	}
	
	public static String biblivreEncrypt(String input) {
		return input;
		
		/*
		if (StringUtils.isBlank(input)) {
			return "";
		}

		byte[] plaintext = input.getBytes();

		try {
			Cipher cipher = Cipher.getInstance("AES");
			Key key = new SecretKeySpec(Constants.CRYPT_KEY, "AES");

			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] ciphertext = cipher.doFinal(plaintext);
			
			return Base64.encodeBase64String(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
		*/
	}
	
	public static String biblivreDecrypt(String input) {
		return input;
		
		/*
		if (StringUtils.isBlank(input)) {
			return "";
		}
		
		byte[] ciphertext = Base64.decodeBase64(input);

		try {
			Cipher cipher = Cipher.getInstance("AES");
			Key key = new SecretKeySpec(Constants.CRYPT_KEY, "AES");

			cipher.init(Cipher.DECRYPT_MODE, key);		
			byte[] plaintext = cipher.doFinal(ciphertext);
			
			return new String(plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
		*/
	}
	
	public static String preparePhrase(String input) {
		return TextUtils.removeDiacriticals(TextUtils.removeDoubleSpaces(input)).toLowerCase();
	}

	public static String[] prepareWords(String phrase) {
		return StringUtils.split(TextUtils.removeNonLettersOrDigits(phrase, " "));	
	}
	
	public static String[] prepareAutocomplete(String phrase) {
		return StringUtils.split(TextUtils.preparePhrase(phrase));	
	}

	public static String prepareWord(String word) {
		return TextUtils.removeNonLettersOrDigits(word, "");	
	}
	
	public static String[] prepareExactTerms(String phrase) {
		String[] terms = TextUtils.prepareWords(phrase);
		
		Arrays.sort(terms, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {  
				if (o1.length() < o2.length()) {
					return 1;
				} else if (o1.length() > o2.length()) {
					return -1;
				}
				return o1.compareTo(o2);
			}
		});

		List<String> newList = new ArrayList<String>();
		for (int i = 0; i < terms.length; i++) {
			String term = terms[i];
			if (term.length() > 2) {
				newList.add(term);
			}
			
			if (newList.size() > 2) {
				break;
			}
		}

		return newList.toArray(new String[]{});
	}
	
	public static String removeDiacriticals(String input) {
		if (input == null) {
			return "";
		}

		String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
		return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	public static String removeDoubleSpaces(String input) {
		if (input == null) {
			return "";
		}

		String trimmed = StringUtils.trimToEmpty(input); 
		return trimmed.replaceAll("\\s{2,}", " ");
	}
	
	public static String removeNonLettersOrDigits(String input, String replace) {
		if (input == null) {
			return "";
		}

		return input.replaceAll("[^\\p{L}\\p{N}*]", replace).replaceAll("\\*([^\\s])", "$1");
	}

	public static Date parseDate(String date) throws ParseException {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		return DateUtils.parseDate(date, new String[] { DateFormatUtils.ISO_DATETIME_FORMAT.getPattern() });
	}

	public static int defaultInt(String value) {
		return TextUtils.defaultInt(value, 0);
	}

	public static int defaultInt(String value, int defValue) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			return defValue;
		}
	}

	public static String detectCharset(InputStream input) {
		UniversalDetector detector = new UniversalDetector(null);
		byte[] buf = new byte[4096];
		int read;

		try {
			while ((read = input.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, read);
			}

			input.reset();
		} catch (Exception e) {
			return null;
		}

		detector.dataEnd();
		
		return detector.getDetectedCharset();
	}
	
	public static String incrementLastChar(String text) {
		if (StringUtils.isBlank(text)) {
			return text;
		}

		return text + "zzzzzzzzzzzzzzzzz";
	}

}
