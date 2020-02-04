package nl.fuchsia.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

@Configuration
// To enable scanning for teh @JmsListener annotation
@EnableJms
public class JMSConfig {

	@Bean
	public ActiveMQConnectionFactory connectionFactory(@Value("${jms.url}") String jmsUrl) {
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
		activeMQConnectionFactory.setBrokerURL(jmsUrl);
		//activeMQConnectionFactory.setTrustAllPackages(true);
		return activeMQConnectionFactory;
	}

	@Bean
	public ActiveMQQueue queue() {
		return new ActiveMQQueue("cjib.mq.queue");
	}

	@Bean
	public ActiveMQTopic topic() {
		return new ActiveMQTopic("cjib.mq.topic");
	}

//	@Bean
//	public JmsTemplate jmsTemplate(ActiveMQConnectionFactory connectionFactory, ActiveMQTopic topic) {
//		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
//		jmsTemplate.setDefaultDestination(topic);
//		return jmsTemplate;
//	}

	@Bean
	public JmsTemplate jmsTemplate(ActiveMQConnectionFactory connectionFactory, ActiveMQQueue queue) {
		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setDefaultDestination(queue);
		return jmsTemplate;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
		jmsListenerContainerFactory.setConnectionFactory(connectionFactory);
		// true is TOPIC and false is QUEUE
		jmsListenerContainerFactory.setPubSubDomain(false);
		return jmsListenerContainerFactory;
	}
}
