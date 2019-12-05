package nl.fuchsia.services;

import nl.fuchsia.model.Feit;
import nl.fuchsia.repository.FeitRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FeitServiceTest {
    @Mock
    private FeitRepository feitRepository;
    @InjectMocks
    private FeitService feitService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testAddFeit() {
        Feit feit = new Feit();

        feitService.addFeit(feit);

        verify(feitRepository).addFeit(feit);
    }
}