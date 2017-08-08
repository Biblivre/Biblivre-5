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
package biblivre.marc;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

public class MarcFileReader implements MarcReader {

	private static final Pattern LEADER_PATTERN = Pattern.compile("^(000|LDR|LEADER)\\s+", Pattern.CASE_INSENSITIVE);
	
	private InputStream input;
	private Scanner scanner;
	private StringBuilder marc;
	private boolean validStart;
	
	public MarcFileReader(InputStream input, String encoding) {
		this.input = input;
        this.scanner = new Scanner(this.input, encoding);
        this.marc = null;
        this.validStart = false;
	}

	@Override
	public boolean hasNext() {
		if (this.marc != null) {
			return true;
		}

		if (this.marc == null && this.validStart) {
			return false;
		}
		
		while (this.scanner.hasNextLine()) {
			String line = this.scanner.nextLine().trim();

			if (line.length() <= 3) {
				continue;
			}

			if (MarcFileReader.LEADER_PATTERN.matcher(line).find()) {
				this.marc = new StringBuilder(line + "\n");
				this.validStart = true;
				return true;
			}
		}

		this.scanner.close();
		return false;
	}

	@Override
	public Record next() {
		Record record = null;
		
		while (this.scanner.hasNextLine()) {
			String line = this.scanner.nextLine().trim();
			
			if (line.length() <= 3) {
				continue;
			}

			if (MarcFileReader.LEADER_PATTERN.matcher(line).find()) {
				if (this.marc == null) {
					this.marc = new StringBuilder(line + "\n");					
				} else {
					record = MarcUtils.marcToRecord(this.marc.toString(), null, RecordStatus.NEW);
					this.marc = new StringBuilder(line + "\n");
					return record;
				}
			}

			if (this.marc != null) {
				this.marc.append(line).append("\n");				
			}
		}

		if (this.marc.length() > 0) {
			record = MarcUtils.marcToRecord(this.marc.toString(), null, RecordStatus.NEW);
			this.marc = null;
		}

		this.scanner.close();
		return record;
	}
}
