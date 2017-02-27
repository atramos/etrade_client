package com.github.atramos.quant.universe;

import java.io.IOException;
import java.util.List;

public interface Universe {

	List<String> listStocks() throws IOException;
}
