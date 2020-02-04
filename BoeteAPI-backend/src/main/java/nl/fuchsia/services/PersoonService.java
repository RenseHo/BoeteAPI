package nl.fuchsia.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nl.fuchsia.dto.PersoonJmsDto;
import nl.fuchsia.exceptionhandlers.NotFoundException;
import nl.fuchsia.exceptionhandlers.UniekVeldException;
import nl.fuchsia.model.Persoon;
import nl.fuchsia.repository.PersoonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class PersoonService {
    private PersoonRepository persoonRepository;
    private JmsTemplate jmsTemplate;

    @Autowired
    public PersoonService(PersoonRepository persoonRepository, JmsTemplate jmsTemplate) {
        this.persoonRepository = persoonRepository;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * Geeft een lijst van personen die in de database staan via de ormPersoonRepository.
     *
     * @return - Roept de methode getOrmPersonen aan in ormPersoonRepository.
     */
    public List<Persoon> getPersonen() {
        return persoonRepository.findAll();
    }

    /**
     * Voegt de persoon toe via de persoonRepository.
     *
     * @param persoon - De toe te voegen persoon.
     */
    public Persoon addPersoon(Persoon persoon) {
        try {
			jmsTemplate.send(session -> session.createTextMessage("Persoon GOED toegevoegd" + LocalDateTime.now()));
            return persoonRepository.save(persoon);
        } catch (TransactionSystemException e) {
			throw new UniekVeldException("BSN nummer: " + persoon.getBsn() + " bestaat reeds.");
        }

    }

    /**
     * haalt de persoon per ID (persoonnr) via de OrmPersoonRepository.
     *
	 * @param persoonnr - ID de op te halen persoon.
	 * @return
	 */
    public Persoon getPersoonById(Integer persoonnr) {
		Optional<Persoon> persoon = persoonRepository.findById(persoonnr);
    	try{
			//jmsTemplate.send(session -> session.createTextMessage("Persoon met persoonnr : "+ persoonnr +" opgevraagd " + LocalDateTime.now()));
			ObjectMapper mapper = new ObjectMapper();
			PersoonJmsDto persoonJmsDto = new PersoonJmsDto();
			persoonJmsDto.setVerzender("Rense");
			persoonJmsDto.setBericht("Persoon opgevraagd");
			String jsonMessage = mapper.writeValueAsString(persoonJmsDto);
			jmsTemplate.send(session -> session.createTextMessage(jsonMessage));
		}catch (JsonProcessingException e){
			throw new UniekVeldException("Foute JSON");
		}

        return persoon.orElseThrow(() -> new NotFoundException("PersoonNummer: " + persoonnr + " bestaat niet"));
    }

    /**
     * wijzigd de persoon in de database op bassis van de meegeven ID nummer in de persoon.
     *
     * @param persoon zijn de gegevens waarin de persoon gewijzigd moet worden
     * @return de nieuwe persoon in de database
     */
    public Persoon updatePersoonById(Integer persoonnr, Persoon persoon) {
        try {
			Optional<Persoon> persoonOpgehaald = persoonRepository.findById(persoonnr);
			persoonOpgehaald.orElseThrow(() -> new NotFoundException("PersoonNummer: " + persoonnr + " bestaat niet"));
			persoon.setPersoonnr(persoonnr);
            persoonRepository.save(persoon);

        } catch (TransactionSystemException e) {
            throw new UniekVeldException("BSN nummer: " + persoon.getBsn() + " bestaat reeds.");
        }

        return persoon;
    }
}
