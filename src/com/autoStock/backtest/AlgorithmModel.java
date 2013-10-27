package com.autoStock.backtest;

import java.util.ArrayList;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.backtest.watchmaker.WMAdjustment;
import com.autoStock.indicator.IndicatorBase;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.IndicatorParameters;
import com.autoStock.signal.SignalDefinitions.SignalParameters;
import com.autoStock.strategy.StrategyOptions;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmModel {
	public StrategyOptions strategyOptions;
	public ArrayList<SignalParameters> listOfSignalParameters = new ArrayList<SignalParameters>();
	public ArrayList<IndicatorParameters> listOfIndicatorParameters = new ArrayList<IndicatorParameters>();
	public WMAdjustment wmAdjustment;
	
	public AlgorithmModel(){ }
	
	public AlgorithmModel(StrategyOptions strategyOptions, ArrayList<SignalParameters> listOfSignalParameters, ArrayList<IndicatorParameters> listOfIndicatorParameters) {
		this.strategyOptions = strategyOptions;
		this.listOfSignalParameters = listOfSignalParameters;
		this.listOfIndicatorParameters = listOfIndicatorParameters;
	}
	
	public AlgorithmModel copy(){
		AlgorithmModel algorithmModel = new AlgorithmModel();
		
		algorithmModel.strategyOptions = this.strategyOptions.copy();
		algorithmModel.wmAdjustment = this.wmAdjustment.copy();
		
		for (SignalParameters signalParameters : listOfSignalParameters){
			algorithmModel.listOfSignalParameters.add(signalParameters.copy());
		}
		
		for (IndicatorParameters indicatorParameters : listOfIndicatorParameters){
			algorithmModel.listOfIndicatorParameters.add(indicatorParameters.copy());
		}
		
		return algorithmModel;
	}
	
	public static AlgorithmModel getCurrentAlgorithmModel(AlgorithmBase algorithmBase){
		ArrayList<SignalParameters> listOfSignalParameters = new ArrayList<SignalParameters>();
		ArrayList<IndicatorParameters> listOfIndicatorParameters = new ArrayList<IndicatorParameters>();
		
		for (SignalBase signalBase : algorithmBase.signalGroup.getListOfSignalBase()){
			listOfSignalParameters.add(signalBase.signalParameters.copy());
		}
		
		for (IndicatorBase indicatorBase : algorithmBase.indicatorGroup.getListOfIndicatorBase()){
			listOfIndicatorParameters.add(indicatorBase.indicatorParameters.copy());
		}
		
		return new AlgorithmModel(algorithmBase.strategyBase.strategyOptions, listOfSignalParameters, listOfIndicatorParameters);
	}
}
