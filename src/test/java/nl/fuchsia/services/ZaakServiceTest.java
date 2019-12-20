package nl.fuchsia.services;

import nl.fuchsia.dto.ZaakDto;
import nl.fuchsia.exceptionhandlers.NullException;
import nl.fuchsia.model.Feit;
import nl.fuchsia.model.Persoon;
import nl.fuchsia.model.Zaak;
import nl.fuchsia.repository.FeitRepository;
import nl.fuchsia.repository.PersoonRepository;
import nl.fuchsia.repository.ZaakRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ZaakServiceTest {

	@Mock
	private ZaakRepository zaakRepository;

	@Mock
	private PersoonRepository persoonRepository;

	@Mock
	private FeitRepository feitRepository;

	@InjectMocks
	private ZaakService zaakService;

	@BeforeEach
	public void setup() {
		initMocks(this);
	}

	@Test
	public void testAddZaak() {
		Persoon persoon = new Persoon(1, "Rense", "Houwing", "De buren", "10", "8402 GH", "Drachten", "123456789", LocalDate.of(1990, 10, 12));
		List<Feit> feiten = new ArrayList<>();
		when(persoonRepository.getPersoonById(1)).thenReturn(persoon);
		ZaakDto zaakDto = new ZaakDto(1, LocalDate.of(2019, 2, 18), "Leeuwarden", 1, new ArrayList<Integer>(Arrays.asList(1, 2)));
		List<Integer> feitnrs = zaakDto.getFeitnrs();
		for (int feitnr : feitnrs) {
			Feit test = new Feit(feitnr, "VBF-003", "Test", 4.00);
			when(feitRepository.getFeitById(feitnr)).thenReturn(test);
			feiten.add(test);
		}
		Zaak zaak = new Zaak(LocalDate.of(2019, 2, 18), "Leeuwarden", persoon, feiten);

		zaakService.addZaak(zaakDto);

		verify(persoonRepository).getPersoonById(1);
		verify(feitRepository).getFeitById(1);
		verify(zaakRepository).addZaak(zaak);
	}

	@Test
	public void testPersoonDoesNotExist() {
		ZaakDto zaakDto = new ZaakDto(1, LocalDate.of(2019, 2, 18), "Leeuwarden", 1, new ArrayList<Integer>(Arrays.asList(1, 2)));

		assertThatThrownBy(() -> zaakService.addZaak(zaakDto))
			.isInstanceOf(NullException.class).hasMessage("Persoonnr 1 bestaat niet");
	}

	@Test
	public void testFeitDoesNotExist() {
		Persoon persoon = new Persoon(1, "Rense", "Houwing", "De buren", "10", "8402 GH", "Drachten", "123456789", LocalDate.of(1990, 10, 12));
		ZaakDto zaakDto = new ZaakDto(1, LocalDate.of(2019, 2, 18), "Leeuwarden", 1, new ArrayList<Integer>(Arrays.asList(1, 2)));
		when(persoonRepository.getPersoonById(1)).thenReturn(persoon);

		assertThatThrownBy(() -> zaakService.addZaak(zaakDto))
			.isInstanceOf(NullException.class).hasMessage("Feitnr 1 bestaat niet");

		when(feitRepository.getFeitById(1)).thenReturn(new Feit(1, "VBF-003", "Test", 4.00));

		assertThatThrownBy(() -> zaakService.addZaak(zaakDto))
			.isInstanceOf(NullException.class).hasMessage("Feitnr 2 bestaat niet");
	}

	@Test
	public void testGetZaken() {
		zaakService.getZaken();

		verify(zaakRepository).getZaken();
	}
}
