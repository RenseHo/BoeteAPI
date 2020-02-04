package nl.fuchsia.jms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.fuchsia.dto.PersoonJmsDto;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.TextMessage;

@Component
public class JmsController {
	@JmsListener(destination = "cjib.mq.queue")
	public void handleMessageQueue(TextMessage jsonMessage) throws JMSException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		PersoonJmsDto persoonJmsDto = mapper.readValue(jsonMessage.getText(), PersoonJmsDto.class);
		//System.out.println("QUEUE MESSAGE RECEIVED: " + jsonMessage.getText());

		System.out.println("QUEUE MESSAGE RECEIVED: " + persoonJmsDto.getVerzender() + " " + persoonJmsDto.getBericht());
	}
}
