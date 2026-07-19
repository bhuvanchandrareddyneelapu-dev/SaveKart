package com.savekart.adapter;

public interface PlatformAdapter {
    String getPlatformName();
    PlatformPriceFetchResult fetchPrice(String query, String sku);
}
