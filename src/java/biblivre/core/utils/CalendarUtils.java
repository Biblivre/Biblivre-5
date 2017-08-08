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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.common.WorkingWeek;
import net.objectlab.kit.datecalc.joda.JodaWorkingWeek;
import net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import biblivre.core.configurations.Configurations;

public class CalendarUtils {
	
	private static final String CALENDAR_NAME = "BR-BIBL";
	
	public static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static java.sql.Timestamp toSqlTimestamp(Date date) {
		return new java.sql.Timestamp(date.getTime());
	}
	
	public static boolean isMidnight(Date date) {
		Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return date.getTime() == cal.getTimeInMillis();
	}
	
	public static Date calculateExpectedReturnDate(String schema, Date lendingDate, int days) {
		DateCalculator<LocalDate> cal = 
				LocalDateKitCalculatorsFactory.
				getDefaultInstance().
				getDateCalculator(CalendarUtils.CALENDAR_NAME, HolidayHandlerType.FORWARD);
		WorkingWeek week = new WorkingWeek();
		for (int i = 1; i <=7; i++) {
			week = week.withWorkingDayFromCalendar(false, i);
		}
		
		for (int i : Configurations.getIntArray(schema, Constants.CONFIG_BUSINESS_DAYS, "2,3,4,5,6")) {
			week = week.withWorkingDayFromCalendar(true, i);
		}
		cal.setWorkingWeek(new JodaWorkingWeek(week));
		cal.setStartDate(new LocalDate(lendingDate));
		LocalDate newCurrent = cal.moveByDays(days).getCurrentBusinessDate();
		//LocalDate newCurrent = cal.moveByBusinessDays(days).getCurrentBusinessDate();
		return newCurrent.toDate();
	}
	
	public static int calculateDeteDifference(Date initialDate, Date finalDate) {
		DateTime firstDate = new DateTime(initialDate);
		DateTime lastDate = new DateTime(finalDate);
		Integer difference = Days.daysBetween(firstDate, lastDate).getDays();
		return difference < 0 ? 0 : difference;
	}
	
	
//	public static void loadHolidays() {
//		Set<LocalDate> holidays = new HashSet<LocalDate>();
		//List holidays from database and add them to the holidays Set
//		holidays.add(new LocalDate("2006-08-28"));
		//create a Holiday Calendar with the holidays Set
//		HolidayCalendar<LocalDate> calendar = 
//				new DefaultHolidayCalendar<LocalDate>(
//						holidays, 
//						new LocalDate("2006-01-01"), 
//						new LocalDate("2006-12-31"));
		//register the holiday calendar
//		LocalDateKitCalculatorsFactory.
//			getDefaultInstance().
//			registerHolidays(CalendarUtils.CALENDAR_NAME, calendar);
//	}
	
}
