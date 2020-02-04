package nl.fuchsia.dto;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.LocalDateTime;

public class PersoonJmsDto {

	private String Verzender;
	private String Bericht;

	public String getVerzender() {
		return Verzender;
	}

	public void setVerzender(String verzender) {
		Verzender = verzender;
	}

	public String getBericht() {
		return Bericht;
	}

	public void setBericht(String bericht) {
		Bericht = bericht;
	}

}
