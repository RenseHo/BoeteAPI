package nl.fuchsia.services;

import nl.fuchsia.dto.ZaakDto;
import nl.fuchsia.dto.ZaakAddStatusDto;
import nl.fuchsia.dto.ZaakAddFeitDto;
import nl.fuchsia.exceptionhandlers.*;
import nl.fuchsia.model.*;
import nl.fuchsia.repository.FeitRepository;
import nl.fuchsia.repository.PersoonRepository;
import nl.fuchsia.repository.StatusRepository;
import nl.fuchsia.repository.ZaakRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZaakService {

    private ZaakRepository zaakRepository;
    private PersoonRepository persoonRepository;
    private FeitRepository feitRepository;
    private StatusRepository statusRepository;

    public ZaakService(ZaakRepository zaakRepository, PersoonRepository persoonRepository, FeitRepository feitRepository, StatusRepository statusRepository) {
        this.zaakRepository = zaakRepository;
        this.persoonRepository = persoonRepository;
        this.feitRepository = feitRepository;
        this.statusRepository = statusRepository;
    }

    /**
     * Voegt een nieuwe zaak toe inclusief een persoon en minimaal 1 feit.
     *
     * @param zaakDto de ingevoerde zaakDto
     * @return de gemaakte zaak inclusief persoon en feit(en).
     */
    public ZaakDto addZaak(ZaakDto zaakDto) {
        Zaak zaak = new Zaak();
        List<String> exceptions = new ArrayList<>();

        Persoon persoon = persoonRepository.getPersoonById(zaakDto.getPersoonnr());

        if (persoon == null) {
            exceptions.add(" Persoonnr " + zaakDto.getPersoonnr() + " bestaat niet");
        }
        List<Feit> feiten = new ArrayList<>();
        for (int feitNr : zaakDto.getFeitnrs()) {
            Feit feit = feitRepository.getFeitById(feitNr);
            if (feit == null) {
                exceptions.add("Feitnr " + feitNr + " bestaat niet");
            } else feiten.add(feit);
        }
        if (exceptions.size() > 0) {
            throw new NotFoundException(exceptions.toString());
        }
        List<ZaakStatus> zaakStatussen = new ArrayList<>();
        ZaakStatus zaakStatus = new ZaakStatus();
        zaakStatus.setStatus(new Status(1, "Open"));
        zaakStatus.setVeranderdatum(LocalDate.now());
        zaakStatus.setZaak(zaak);
        zaakStatussen.add(zaakStatus);

        zaak.setOvertredingsdatum(zaakDto.getOvertredingsdatum());
        zaak.setPleeglocatie(zaakDto.getPleeglocatie());
        zaak.setPersoon(persoon);
        zaak.setFeiten(feiten);
        zaak.setZaakStatus(zaakStatussen);
        Zaak savedZaak = zaakRepository.addZaak(zaak);

        SetZaakStatusDto(zaakDto, savedZaak);

        return zaakDto;
    }

    @Transactional
    public ZaakDto updZaakStatus(Integer zaakNr, ZaakAddStatusDto zaakAddStatusDto) {
        List<String> notFoundExceptions = new ArrayList<>();

        if (zaakRepository.getZaakById(zaakNr) == null) {
            notFoundExceptions.add("ZaakNummer: " + zaakNr + " bestaat niet");
        }

        if (statusRepository.getStatusById(zaakAddStatusDto.getStatusNr()) == null) {
            notFoundExceptions.add("StatusNummer: " + zaakAddStatusDto.getStatusNr() + " bestaat niet");
        }

        if (notFoundExceptions.size() > 0) {
            throw new NotFoundException(notFoundExceptions.toString());
        }

        Status status = statusRepository.getStatusById(zaakAddStatusDto.getStatusNr());

        Zaak zaak = zaakRepository.getZaakById(zaakNr);
        ZaakStatus zaakStatus = new ZaakStatus(LocalDate.now(), status, zaak);

        List<ZaakStatus> zaakStatussen = zaak.getZaakStatus();
        zaakStatussen.add(zaakStatus);
        zaak.setZaakStatus(zaakStatussen);
        zaakRepository.addZaak(zaak);

        return SetZaakDto(zaak);
    }

    private ZaakDto SetZaakDto(Zaak zaak) {
        ZaakDto zaakDto = new ZaakDto();
        SetFeitnrsDto(zaak, zaakDto);
        return zaakDto;
    }

    public List<ZaakDto> getZaken() {
        List<Zaak> zaken = zaakRepository.getZaken();
        return setZakenDtos(zaken);
    }

    public ZaakDto getZaakById(Integer zaakNr) {

        if (zaakRepository.getZaakById(zaakNr) == null) {
            throw new NotFoundException("ZaakNummer: " + zaakNr + " bestaat niet");
        }

        Zaak zaak = zaakRepository.getZaakById(zaakNr);

        return SetZaakDto(zaak);
    }

    public List<ZaakDto> getZakenByPersoon(Integer persoonnr) {

        Persoon persoon = persoonRepository.getPersoonById(persoonnr);

        if (persoon == null) {
            throw new NotFoundException("Persoonnr " + persoonnr + " bestaat niet");
        }
        List<Zaak> zaken = zaakRepository.getZakenByPersoon(persoon);
        return setZakenDtos(zaken);
    }

	/**
	 * Voegt 1 of meer bestaande feiten toe aan een bestaande zaak.
	 *
	 * @param zaakNr             de betreffende bestaande zaak
	 * @param listZaakAddFeitDto de toe te voegen feit(en)
	 * @return de geupdate zaak.
	 */
	@Transactional
	public ZaakDto updZaakFeit(Integer zaakNr, List<ZaakAddFeitDto> listZaakAddFeitDto) {
		List<String> notFoundExceptions = new ArrayList<>();
		List<String> uniekVeldExceptions = new ArrayList<>();


		if (zaakRepository.getZaakById(zaakNr) == null) {
			notFoundExceptions.add("zaakNummer: " + zaakNr + " bestaat niet");
		}
		for (ZaakAddFeitDto zaakAddFeitDto : listZaakAddFeitDto) {
			if (feitRepository.getFeitById(zaakAddFeitDto.getFeitNr()) == null) {
				notFoundExceptions.add("feitNummer: " + zaakAddFeitDto.getFeitNr() + " bestaat niet");
			}
		}
		if (notFoundExceptions.size() > 0) {
			notFoundExceptions.add("geen feit(en) toegevoegd");
			throw new NotFoundException(notFoundExceptions.toString());
		}
		Zaak zaak = zaakRepository.getZaakById(zaakNr);
		List<Feit> zaakFeiten = zaak.getFeiten();
		for (ZaakAddFeitDto zaakAddFeitDto : listZaakAddFeitDto) {
			int feitNrDto = zaakAddFeitDto.getFeitNr();
			for (Feit feit : zaakFeiten) {
				if (feit.getFeitNr() == feitNrDto) {
					uniekVeldExceptions.add("feitNummer: " + zaakAddFeitDto.getFeitNr() + " is reeds toegevoegd aan deze zaak");
				}
			}
		}
		if (uniekVeldExceptions.size() > 0) {
			uniekVeldExceptions.add("geen feit(en) toegevoegd");
			throw new UniekVeldException(uniekVeldExceptions.toString());
		}
		for (ZaakAddFeitDto zaakAddFeitDto : listZaakAddFeitDto) {
			zaakFeiten.add(feitRepository.getFeitById(zaakAddFeitDto.getFeitNr()));
			zaak.setFeiten(zaakFeiten);
		}
		return SetZaakDto(zaak);
	}

    private void SetFeitnrsDto(Zaak zaak, ZaakDto zaakDto) {
        zaakDto.setOvertredingsdatum(zaak.getOvertredingsdatum());
        zaakDto.setPleeglocatie(zaak.getPleeglocatie());
        zaakDto.setPersoonnr(zaak.getPersoon().getPersoonnr());

        List<Integer> feitnrs = new ArrayList<>();
        for (Feit feiten : zaak.getFeiten()) {
            int dtoFeitnr = feiten.getFeitNr();
            feitnrs.add(dtoFeitnr);
        }
        zaakDto.setFeitnrs(feitnrs);

        SetZaakStatusDto(zaakDto, zaak);
    }

    private List<ZaakDto> setZakenDtos(List<Zaak> zaken) {
        List<ZaakDto> zaakDtos = new ArrayList<>();

        for (Zaak zaak : zaken) {
            ZaakDto dtoZaken = new ZaakDto();
            dtoZaken.setZaaknr(zaak.getZaaknr());

            SetFeitnrsDto(zaak, dtoZaken);

            zaakDtos.add(dtoZaken);
        }
        return zaakDtos;
    }

    public void SetZaakStatusDto(ZaakDto zaakDto, Zaak zaak) {
        List<Integer> zaakStatusnrs = new ArrayList<>();
        for (ZaakStatus zaakStatusNr : zaak.getZaakStatus()) {

            int dtoZaakStatusnr = zaakStatusNr.getZaakstatusnr();
            zaakStatusnrs.add(dtoZaakStatusnr);
        }
        zaakDto.setZaakstatusnr(zaakStatusnrs);

        zaakDto.setZaaknr(zaak.getZaaknr());
    }
}
