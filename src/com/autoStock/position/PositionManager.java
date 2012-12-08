/**
 * 
 */
package com.autoStock.position;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.order.OrderDefinitions.OrderMode;
import com.autoStock.position.PositionDefinitions.PositionType;
import com.autoStock.signal.Signal;
import com.autoStock.tools.Lock;
import com.autoStock.tools.MathTools;
import com.autoStock.trading.types.Position;
import com.autoStock.types.Exchange;
import com.autoStock.types.QuoteSlice;
import com.autoStock.types.Symbol;

/**
 * @author Kevin Kowalewski
 * 
 */
public class PositionManager implements PositionStatusListener {
	private static PositionManager instance = new PositionManager();
	private volatile PositionGenerator positionGenerator = new PositionGenerator();
	private volatile PositionExecutor positionExecutor = new PositionExecutor();
	private volatile ArrayList<Position> listOfPosition = new ArrayList<Position>();
	public OrderMode orderMode;
	private Lock lock = new Lock();
	
	public static PositionManager getInstance(){
		return instance;
	}

	public synchronized Position executePosition(QuoteSlice quoteSlice, Exchange exchange, Signal signal, PositionType positionType, Position inboundPosition) {
		synchronized (lock) {
			if (positionType == PositionType.position_long_entry) {
				Position position = positionGenerator.generatePosition(quoteSlice, signal, positionType, exchange);
				if (position != null){
					position.setPositionListener(this);
					listOfPosition.add(position);
					positionExecutor.executeLongEntry(position);
				}
				return position;
			} else if (positionType == PositionType.position_short_entry) {
				Position position = positionGenerator.generatePosition(quoteSlice, signal, positionType, exchange);
				if (position != null){
					position.setPositionListener(this);
					listOfPosition.add(position);
					positionExecutor.executeShortEntry(position);
				}
				return position;
			} else if (positionType == PositionType.position_long_exit) {
				positionExecutor.executeLongExit(inboundPosition);
				return inboundPosition;
			} else if (positionType == PositionType.position_short_exit) {
				positionExecutor.executeShortExit(inboundPosition);	
				return inboundPosition;
			} else {
				throw new UnsupportedOperationException();
			}
		}
	}

	public void updatePositionPrice(QuoteSlice quoteSlice, Position position) {
		if (position != null) {
			position.updatePositionUnitPrice(quoteSlice.priceClose);
		}
	}

	public void executeExitAll() {
		synchronized (lock) {
			if (listOfPosition.size() == 0) {
				Co.println("--> No positions to sell");
			}else{
				Co.println("--> Exiting all positions: " + listOfPosition.size());
				Co.println("--> X: " + listOfPosition.get(0).symbol.symbolName);
			}
			
			ArrayList<Position> listOfModifyablePosition = (ArrayList<Position>) listOfPosition.clone();

			for (Position position : listOfModifyablePosition) {
				if (position.positionType == PositionType.position_long) {
					positionExecutor.executeLongExit(position);
				} else if (position.positionType == PositionType.position_short) {
					positionExecutor.executeShortExit(position);
				} else {
					throw new IllegalStateException("No condition matched PositionType: " + position.positionType.name());
				}
			}
			listOfPosition.clear();
		}
	}

	public Position getPosition(Symbol symbol) {
		synchronized (lock) {
			for (Position position : listOfPosition) {
				if (position.symbol.symbolName.equals(symbol.symbolName)) {
					return position;
				}
			}
		}
		
		return null;
	}

	public double getCurrentProfitLossAfterComission(boolean bothComissions) {
		synchronized (lock) {
			double currentProfitLoss = 0;
			for (Position position : listOfPosition) {
				currentProfitLoss += position.getPositionProfitLossAfterComission(bothComissions);
			}
			
			return MathTools.round(currentProfitLoss);
		}
	}
	
	public double getCurrentProfitLossBeforeComission() {
		synchronized (lock) {
			double currentProfitLoss = 0;
			for (Position position : listOfPosition) {
				currentProfitLoss += position.getPositionProfitLossBeforeComission();
			}
			
			return MathTools.round(currentProfitLoss);
		}
	}

	public int getPositionListSize() {
		synchronized (lock) {
			return listOfPosition.size();			
		}
	}

	public double getAllPositionValueIncludingFees() {
		synchronized(lock){
			double valueOfAllPositions = 0; 
			for (Position position : listOfPosition){
				valueOfAllPositions += position.getPositionValue().valueCurrentWithFees;
			}
			
			return valueOfAllPositions;
		}
	}

	@Override
	public void positionStatusChange(Position position) {
		synchronized(lock){
			if (PositionManager.getInstance().orderMode == OrderMode.mode_exchange){
				Co.println("--> PositionManager, position status change: " + position.positionType.name());
			}
			if (position.positionType == PositionType.position_exited || position.positionType == PositionType.position_cancelled){
				listOfPosition.remove(position);
				position = null;
			}
		}
	}

	public void reset() {
		synchronized (lock) {
			listOfPosition.clear();	
		}
	}
}
