package utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface InitFormatter {

	DateTimeFormatter FORMATTERDATE= DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	DecimalFormat FORMATTERPRICE = new DecimalFormat("€#.00", DecimalFormatSymbols.getInstance(Locale.FRANCE));
}