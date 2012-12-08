package com.autoStock.algorithm.core;

import com.autoStock.Co;
import com.autoStock.algorithm.AlgorithmTest;
import com.autoStock.algorithm.core.AlgorithmDefinitions.AlgorithmMode;
import com.autoStock.exchange.request.RequestMarketSymbolData;
import com.autoStock.exchange.request.base.RequestHolder;
import com.autoStock.exchange.request.listener.RequestMarketSymbolDataListener;
import com.autoStock.exchange.results.ExResultMarketSymbolData.ExResultSetMarketSymbolData;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.position.PositionManager;
import com.autoStock.trading.platform.ib.definitions.HistoricalDataDefinitions.Period;
import com.autoStock.trading.types.MarketSymbolData;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 *
 */
public class ActiveAlgorithmContainer {
	public AlgorithmTest algorithm;
	public RequestMarketSymbolData requestMarketData;
	public Symbol symbol;
	private Exchange exchange;
	
	public ActiveAlgorithmContainer(boolean canTrade, Exchange exchange, Symbol symbol){
		this.symbol = symbol;
		this.exchange = exchange;
		algorithm = new AlgorithmTest(canTrade, exchange, symbol, AlgorithmMode.mode_engagement);
	}
	
	public void activate(){
		requestMarketData = new RequestMarketSymbolData(new RequestHolder(this), new RequestMarketSymbolDataListener() {
			@Override
			public void failed(RequestHolder requestHolder) {
				Co.println("--> Completed?");
			}
			
			@Override
			public void receiveQuoteSlice(RequestHolder requestHolder, QuoteSlice quoteSlice) {
				if (quoteSlice.priceClose != 0){
					algorithm.receiveQuoteSlice(quoteSlice);
				}
			}
			
			@Override
			public void completed(RequestHolder requestHolder, ExResultSetMarketSymbolData exResultSetMarketData) {
				Co.println("--> Completed?");
			}
		}, new MarketSymbolData(exchange, symbol, "STK"), Period.min.seconds * 1000);
	}
	
	public void deactivate(){
		Co.println("--> Deactivating: " + symbol.symbolName);
		if (requestMarketData != null){requestMarketData.cancel();}
		algorithm.endOfFeed(symbol);
		Position position = PositionManager.getInstance().getPosition(symbol);
		if (position != null){
			if (position.positionType == PositionType.position_long){
				PositionManager.getInstance().executePosition(algorithm.getCurrentQuoteSlice(), position.exchange, algorithm.strategy.signal, PositionType.position_long_exit, position);
			}else if (position.positionType == PositionType.position_short){
				PositionManager.getInstance().executePosition(algorithm.getCurrentQuoteSlice(), position.exchange, algorithm.strategy.signal, PositionType.position_short_exit, position);	
			}else{
				throw new IllegalStateException();
			}
		}
	}
}
