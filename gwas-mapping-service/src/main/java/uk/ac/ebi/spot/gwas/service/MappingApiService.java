package uk.ac.ebi.spot.gwas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.constant.Location;
import uk.ac.ebi.spot.gwas.dto.AssemblyInfo;
import uk.ac.ebi.spot.gwas.dto.GeneSymbol;
import uk.ac.ebi.spot.gwas.dto.OverlapRegion;
import uk.ac.ebi.spot.gwas.dto.Variation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
@Service
public class MappingApiService {

    private Integer ensemblCount = 0;
    private ObjectMapper mapper = new ObjectMapper();
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mapping.ncbi_source}")
    private String ncbiSource;

    @Value("${mapping.ncbi_logic_name}")
    private String ncbiLogicName;

    @Value("${mapping.ncbi_db_type}")
    private String ncbiDbType;

    public void setEnsemblCount(Integer ensemblCount) {
        this.ensemblCount += ensemblCount;
    }

    public Map<String, List<OverlapRegion>> overlapBandRegion(String mappingLocation) throws InterruptedException {

        String uri = String.format(Location.OVERLAP_BAND_REGION, mappingLocation);
        List<OverlapRegion> overlapRegions = this.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), new TypeReference<List<OverlapRegion>>() {}))
                .orElseGet(ArrayList::new);

        Map<String, List<OverlapRegion>> bands = new HashMap<>();
        bands.put(mappingLocation, overlapRegions);
        return bands;
    }

    // fix manip here
    public Map<String, AssemblyInfo> assemblyInfo(String chromosome) throws InterruptedException { // chromosomeEnd
        String uri = String.format(Location.INFO_ASSEMBLY, chromosome);
        Map<String, AssemblyInfo> assemblyInfoMap = new HashMap<>();

        AssemblyInfo assemblyInfo = this.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), AssemblyInfo.class))
                .orElseGet(AssemblyInfo::new);
        assemblyInfoMap.put(chromosome, assemblyInfo);
        return assemblyInfoMap;
    }

    public Map<String, Variation> variationGet(String snpRsId) throws InterruptedException {
        Map<String, Variation> variationMap = new HashMap<>();
        String uri = String.format("%s/%s", Location.VARIATION, snpRsId);

        Variation variation = this.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), Variation.class))
                .orElseGet(Variation::new);
        variationMap.put(snpRsId, variation);
        return variationMap;
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Map<String, Variation>> variationPost(List<String> snpRsIds) {
        List<Object> cleaned = snpRsIds.stream().map(String::trim).collect(Collectors.toList());
        log.info("Start getting next {} snp rsIds from Ensembl", cleaned.size());
        Object response = postRequest(Collections.singletonMap("ids", cleaned), Location.VARIATION);
        Map<String, Variation> variantMap = mapper.convertValue(response, new TypeReference<Map<String, Variation>>() {});

        setEnsemblCount(cleaned.size());
        log.info("Finished getting {} snp rsIds from Ensembl", getEnsemblCount());
        return CompletableFuture.completedFuture(variantMap);
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Map<String, GeneSymbol>> geneSymbolPost(List<String> reportedGenes) {
        log.info("Start getting next {} reported geneIds from Ensembl", reportedGenes.size());
        Object response = postRequest(Collections.singletonMap("symbols", reportedGenes), Location.REPORTED_GENES);
        Map<String, GeneSymbol> geneMap = mapper.convertValue(response, new TypeReference<Map<String, GeneSymbol>>() {});

        setEnsemblCount(reportedGenes.size());
        log.info("Finished getting {} reported geneIds from Ensembl", getEnsemblCount());
        return CompletableFuture.completedFuture(geneMap);
    }


    public Object postRequest(Map<String, Object> request, String uri) {
        Object response = null;
        try {
            response = restTemplate.postForObject(new URI(uri), request, Object.class);
        } catch (URISyntaxException | HttpStatusCodeException e) {
            log.error("Error: {} for SNP RsIds: {} retrying ...", e.getMessage(), request);
        }
        return response;
    }

    public Optional<ResponseEntity<Object>> getRequest(String uri) throws InterruptedException {
        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.getForEntity(uri, Object.class);
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                log.info("warning: too many request {} retrying ...", uri);
                Thread.sleep(500);
                return this.getRequest(uri);
            }else{
                response = new ResponseEntity<>(Collections.singletonMap("error", e.getResponseBodyAsString()), HttpStatus.OK);
            }
        }
        return Optional.of(response);
    }
}
