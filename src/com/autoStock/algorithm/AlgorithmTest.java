/**
 * 
 */
package com.autoStock.algorithm;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.algorithm.reciever.ReceiverOfQuoteSlice;
import com.autoStock.analysis.AnalysisADX;
import com.autoStock.analysis.AnalysisBB;
import com.autoStock.analysis.AnalysisCCI;
import com.autoStock.analysis.AnalysisMACD;
import com.autoStock.analysis.AnalysisSTORSI;
import com.autoStock.analysis.results.ResultsADX;
import com.autoStock.analysis.results.ResultsBB;
import com.autoStock.analysis.results.ResultsCCI;
import com.autoStock.analysis.results.ResultsMACD;
import com.autoStock.analysis.results.ResultsSTORSI;
import com.autoStock.signal.SignalOfPPC;
import com.autoStock.tables.TableController;
import com.autoStock.tables.TableDefinitions.AsciiTables;
import com.autoStock.tools.Benchmark;
import com.autoStock.tools.DateTools;
import com.autoStock.tools.MathTools;
import com.autoStock.tools.StringTools;
import com.autoStock.types.TypeQuoteSlice;
import com.tictactec.ta.lib.MAType;

/**
 * @author Kevin Kowalewski
 *
 */
public class AlgorithmTest extends AlgorithmBase implements ReceiverOfQuoteSlice {
	
	private int periodLength = 30;
	private int periodWindow = 10;
	public Benchmark bench = new Benchmark();
	
	private AnalysisCCI analysisOfCCI = new AnalysisCCI(periodLength, false);
	private AnalysisADX analysisOfADX = new AnalysisADX(periodLength, false);
	private AnalysisMACD analysisOfMACD = new AnalysisMACD(periodLength, false);
	private AnalysisBB analysisOfBB = new AnalysisBB(periodLength, false);
	private AnalysisSTORSI analysisOfSTORSI = new AnalysisSTORSI(periodLength, false);
	private ArrayList<ArrayList<String>> listOfDisplayRows = new ArrayList<ArrayList<String>>();
	
	private ArrayList<TypeQuoteSlice> listOfQuoteSlice = new ArrayList<TypeQuoteSlice>();
	//private boolean havePosition = false;
	
	public AlgorithmTest(){
		
	}
	
	public ReceiverOfQuoteSlice getReceiver(){
		return this;
	}

	@Override
	public void receiveQuoteSlice(TypeQuoteSlice typeQuoteSlice) {
		//Co.println("Received backtest quote: " + DateTools.getPrettyDate(typeQuoteSlice.dateTime) + ", " + typeQuoteSlice.priceClose);
		
		listOfQuoteSlice.add(typeQuoteSlice);
		
		//bench.tick();
	
		if (listOfQuoteSlice.size() > (periodLength + periodWindow)){
			float analysisPrice = (float) typeQuoteSlice.priceClose;
			
			if (listOfQuoteSlice.size() > (periodLength + periodWindow)){
				listOfQuoteSlice.remove(0);
			}
			
			analysisOfCCI.setDataSet(listOfQuoteSlice);
			analysisOfADX.setDataSet(listOfQuoteSlice);
			analysisOfBB.setDataSet(listOfQuoteSlice);
			analysisOfMACD.setDataSet(listOfQuoteSlice);
			analysisOfSTORSI.setDataSet(listOfQuoteSlice);
			
			ResultsCCI resultsCCI = analysisOfCCI.analyize();
			ResultsADX resultsADX = analysisOfADX.analize();
			ResultsBB resultsBB = analysisOfBB.analyize(MAType.Ema);
			ResultsMACD resultsMACD = analysisOfMACD.analize();
			ResultsSTORSI resultsSTORSI = analysisOfSTORSI.analize();
			
			float analysisOfCCIResult = (float) resultsCCI.arrayOfCCI[listOfQuoteSlice.size()-periodLength];
			float analysisOfADXResult = (float) resultsADX.arrayOfADX[listOfQuoteSlice.size()-periodLength];
			float analysisOfBBResultUpper = (float) resultsBB.arrayOfUpperBand[listOfQuoteSlice.size()-periodLength];
			float analysisOfBBResultLower = (float) resultsBB.arrayOfLowerBand[listOfQuoteSlice.size()-periodLength];
			float analysisOfMACDResultSignal = (float) resultsMACD.arrayOfMACDSignal[listOfQuoteSlice.size()-periodLength]*100;
			float analysisOfSTORSIResultK = (float) resultsSTORSI.arrayOfPercentK[listOfQuoteSlice.size()-periodLength];
			float analysisOfSTORSIResultD = (float) resultsSTORSI.arrayOfPercentD[listOfQuoteSlice.size()-periodLength];
			
			ArrayList<String> columnValues = new ArrayList<String>();
			
			SignalOfPPC signalOfPPC = new SignalOfPPC(resultsCCI.arrayOfPrice, 3);
			
			columnValues.add(DateTools.getPrettyDate(typeQuoteSlice.dateTime));
			columnValues.add(String.valueOf(typeQuoteSlice.priceClose));
			columnValues.add(String.valueOf(StringTools.addPlusToPositiveNumbers(MathTools.roundToTwoDecimalPlaces(typeQuoteSlice.priceClose - listOfQuoteSlice.get(listOfQuoteSlice.size()-2).priceClose))));
			columnValues.add(String.valueOf(MathTools.roundToTwoDecimalPlaces(signalOfPPC.getChange())));
			columnValues.add(String.valueOf(new SignalOfPPC(resultsCCI.arrayOfPrice, 3).getSignal().strength + "," + signalOfPPC.getSignal().signalTypeMetric.name()));
			columnValues.add(String.valueOf(MathTools.roundToTwoDecimalPlaces(analysisOfADXResult)));
			columnValues.add(String.valueOf(MathTools.roundToTwoDecimalPlaces(analysisOfBBResultUpper)));
			columnValues.add(String.valueOf(MathTools.roundToTwoDecimalPlaces(analysisOfBBResultLower)));
			columnValues.add(String.valueOf(analysisOfMACDResultSignal));
			columnValues.add(String.valueOf(MathTools.roundToTwoDecimalPlaces(analysisOfSTORSIResultK)));
			columnValues.add(String.valueOf(MathTools.roundToTwoDecimalPlaces(analysisOfSTORSIResultD)));
			
			listOfDisplayRows.add(columnValues);
			
			//Co.println("Analyized: " + typeQuoteSlice.priceClose + ", " + analysisOfCCIResult);
//			if (analysisResult > 50 && havePosition == false){
//				BasicBalance.buy(analysisPrice);
//				havePosition = true;
//			}else if (analysisResult < -200 && havePosition == true){
//				BasicBalance.sell(analysisPrice);
//				havePosition = false;
//			}
		}else{
			//Co.println("Waiting for more data (period condition)... ");
		}
	}

	@Override
	public void endOfFeed() {
		bench.total();
		new TableController().displayTable(AsciiTables.analysis_test, listOfDisplayRows);
	}
}