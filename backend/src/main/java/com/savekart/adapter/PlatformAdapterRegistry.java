package com.savekart.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PlatformAdapterRegistry {

    private final Map<String, PlatformAdapter> adapterMap = new HashMap<>();

    @Autowired
    public PlatformAdapterRegistry(List<PlatformAdapter> adapters) {
        for (PlatformAdapter adapter : adapters) {
            adapterMap.put(adapter.getPlatformName().toLowerCase(), adapter);
        }
    }

    public PlatformAdapter getAdapter(String platformName) {
        return adapterMap.get(platformName.toLowerCase());
    }

    public List<PlatformAdapter> getAllAdapters() {
        return new ArrayList<>(adapterMap.values());
    }

    public List<PlatformPriceFetchResult> fetchPricesFromAllPlatforms(String query, String sku) {
        List<CompletableFuture<PlatformPriceFetchResult>> futures = adapterMap.values().stream()
                .map(adapter -> CompletableFuture.supplyAsync(() -> adapter.fetchPrice(query, sku)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(PlatformPriceFetchResult::getCurrentPrice))
                .collect(Collectors.toList());
    }
}
