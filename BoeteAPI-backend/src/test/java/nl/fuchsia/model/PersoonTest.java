package nl.fuchsia.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PersoonTest {

	private static Validator validator;
	private static Persoon persoon;

	@BeforeAll
	public static void setupAll() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@BeforeEach
	public void setup() {
		persoon = new Persoon(2, "Rense", "Houwing", "Voltawerk2", "36", "8401 EN", "Gorredijk", "123456789", LocalDate.of(1967, 10, 12));
	}

	@Test
	public void voornaamIsNullOrBlank() {
		persoon.setVoornaam(null);

		Set<ConstraintViolation<Persoon>> constraintViolations = validator.validate(persoon);

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Voornaam moet ingevuld zijn");

		persoon.setVoornaam("  ");
		constraintViolations = validator.validate(persoon);

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Voornaam moet ingevuld zijn");
	}

	@Test
	public void testPostcodeError() {
		persoon.setPostcode("8401EN");

		Set<ConstraintViolation<Persoon>> constraintViolations = validator.validate(persoon);

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Voer een geldige postcode in. 4 cijfers, een spatie en 2 hoofdletters");

		persoon.setPostcode("8404 as");

		constraintViolations = validator.validate(persoon);

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Voer een geldige postcode in. 4 cijfers, een spatie en 2 hoofdletters");
	}

	@Test
	void testBsnError() {
		persoon.setBsn("12345678");

		Set<ConstraintViolation<Persoon>> constraintViolations = validator.validate(persoon);

		assertThat(constraintViolations).hasSize(1);
		assertThat(constraintViolations.iterator().next().getMessage()).isEqualTo("Voer een geldig 9 cijferig BSN nummer in.");
	}
}
