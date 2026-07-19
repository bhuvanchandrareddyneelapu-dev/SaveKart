package com.savekart;

import com.savekart.adapter.AmazonAdapter;
import com.savekart.adapter.PlatformPriceFetchResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AbstractPlatformAdapterTest {

    private final AmazonAdapter amazonAdapter = new AmazonAdapter();

    @Test
    void testFetchPriceResilienceAndStructure() {
        PlatformPriceFetchResult result = amazonAdapter.fetchPrice("iPhone 15 Pro Max", "SKU-IPHONE15");
        assertNotNull(result);
        assertEquals("Amazon", result.getPlatformName());
        assertTrue(result.getCurrentPrice() > 0.0);
        assertNotNull(result.getProductUrl());
        assertEquals("IN_STOCK", result.getStockStatus());
    }
}
