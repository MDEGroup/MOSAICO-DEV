package it.univaq.disim.mosaico.wp2.repository.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.univaq.disim.mosaico.wp2.repository.data.Metric;
import it.univaq.disim.mosaico.wp2.repository.data.enums.MetricType;
import it.univaq.disim.mosaico.wp2.repository.repository.MetricRepository;
import it.univaq.disim.mosaico.wp2.repository.service.impl.MetricServiceImpl;

@ExtendWith(MockitoExtension.class)
class MetricServiceImplTest {

    @Mock
    private MetricRepository metricRepository;

    private MetricServiceImpl metricService;

    @BeforeEach
    void setUp() {
        metricService = new MetricServiceImpl(metricRepository);
    }

    @Test
    void computeRougeScoreMetricPersistsExpectedScore() {
        String reference = "The cat sat on the mat";
        String generated = "The cat is on mat";
        when(metricRepository.save(org.mockito.Mockito.any(Metric.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Metric.class));

        Metric result = metricService.computeRougeScoreMetric(reference, generated);

        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(metricRepository).save(captor.capture());
        Metric storedMetric = captor.getValue();
        float expectedScore = 8f / 11f;

        assertThat(storedMetric.getName()).isEqualTo("ROUGE Score");
        assertThat(storedMetric.getType()).isEqualTo(MetricType.ROUGE);
        assertThat(storedMetric.getUnit()).isEqualTo("score");
        assertThat(storedMetric.getFloatValue()).isPresent();
        assertThat(storedMetric.getFloatValue().orElseThrow()).isCloseTo(expectedScore, within(1e-6f));
        assertThat(result).isSameAs(storedMetric);
    }

    @Test
    void computeRougeScoreMetricReturnsZeroWhenTextsEmpty() {
        when(metricRepository.save(org.mockito.Mockito.any(Metric.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Metric.class));

        Metric result = metricService.computeRougeScoreMetric("", "generated");

        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(metricRepository).save(captor.capture());
        Metric storedMetric = captor.getValue();

        assertThat(storedMetric.getFloatValue()).isPresent();
        assertThat(storedMetric.getFloatValue().orElseThrow()).isZero();
        assertThat(result).isSameAs(storedMetric);
    }

    @Test
    void computeBleuScoreMetricIsOneForPerfectMatch() {
        when(metricRepository.save(org.mockito.Mockito.any(Metric.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Metric.class));

        Metric result = metricService.computeBleuScoreMetric("The quick brown fox", "The quick brown fox");

        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(metricRepository).save(captor.capture());
        Metric storedMetric = captor.getValue();

        assertThat(storedMetric.getName()).isEqualTo("BLEU Score");
        assertThat(storedMetric.getType()).isEqualTo(MetricType.BLEU);
        assertThat(storedMetric.getUnit()).isEqualTo("score");
        assertThat(storedMetric.getFloatValue().orElseThrow()).isCloseTo(1f, within(1e-6f));
        assertThat(result).isSameAs(storedMetric);
    }

    @Test
    void computeBleuScoreMetricReturnsZeroWhenCandidateEmpty() {
        when(metricRepository.save(org.mockito.Mockito.any(Metric.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Metric.class));

        Metric result = metricService.computeBleuScoreMetric("reference", "");

        ArgumentCaptor<Metric> captor = ArgumentCaptor.forClass(Metric.class);
        verify(metricRepository).save(captor.capture());
        Metric storedMetric = captor.getValue();

        assertThat(storedMetric.getFloatValue().orElseThrow()).isZero();
        assertThat(result).isSameAs(storedMetric);
    }
}
