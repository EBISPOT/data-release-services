package uk.ac.ebi.spot.gwas.service.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.constant.DataType;
import uk.ac.ebi.spot.gwas.constant.Type;
import uk.ac.ebi.spot.gwas.constant.Uri;
import uk.ac.ebi.spot.gwas.dto.AssemblyInfo;
import uk.ac.ebi.spot.gwas.dto.RestResponseResult;
import uk.ac.ebi.spot.gwas.service.data.EnsemblRestcallHistoryService;
import uk.ac.ebi.spot.gwas.service.mapping.ApiService;
import uk.ac.ebi.spot.gwas.util.CacheUtil;
import uk.ac.ebi.spot.gwas.util.MappingUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AssemblyInfoService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AppConfig config;
    private final EnsemblRestcallHistoryService historyService;
    private final ApiService mappingApiService;

    public AssemblyInfoService(AppConfig config,
                               EnsemblRestcallHistoryService historyService,
                               ApiService mappingApiService) {
        this.config = config;
        this.historyService = historyService;
        this.mappingApiService = mappingApiService;
    }

    public Map<String, AssemblyInfo> getAssemblyInfo(DataType dataType, List<String> chromosomes) { // Get Chromosome End
        int count = 1;
        Map<String, AssemblyInfo> cached = CacheUtil.assemblyInfo(dataType, config.getCacheDir());
        for (String chromosome : chromosomes) {
            AssemblyInfo assemblyInfo = cached.get(chromosome);
            if (assemblyInfo == null) {
                cached.putAll(this.restApiCall(chromosome));
            }
            MappingUtil.statusLog(dataType.name(), count++, chromosomes.size());
        }
        CacheUtil.saveToFile(dataType, config.getCacheDir(), cached);
        return cached;
    }

    public AssemblyInfo getAssemblyInfoFromDB(String chromosome) {
        RestResponseResult result = historyService.getHistoryByTypeParamAndVersion(Type.INFO_ASSEMBLY, chromosome, config.getERelease());
        AssemblyInfo assemblyInfo = new AssemblyInfo();
        if (result == null) {
            assemblyInfo = this.restApiCall(chromosome).get(chromosome);
        } else {
            try {
                assemblyInfo = mapper.readValue(result.getRestResult(), AssemblyInfo.class);
            } catch (JsonProcessingException e) { log.info(e.getMessage()); }
        }
        return assemblyInfo;
    }

    // fix manip here
    public Map<String, AssemblyInfo> restApiCall(String chromosome) { // chromosomeEnd
        String uri = String.format("%s/%s/%s", config.getServer(), Uri.INFO_ASSEMBLY, chromosome);
        Map<String, AssemblyInfo> assemblyInfoMap = new HashMap<>();

        AssemblyInfo assemblyInfo = mappingApiService.getRequest(uri)
                .map(response -> mapper.convertValue(response.getBody(), AssemblyInfo.class))
                .orElseGet(AssemblyInfo::new);
        assemblyInfoMap.put(chromosome, assemblyInfo);
        return assemblyInfoMap;
    }

}
