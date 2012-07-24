package com.autoStock.backtest;

import java.util.ArrayList;

import com.autoStock.Co;
import com.autoStock.finance.Account;
import com.autoStock.signal.SignalControl;

/**
 * @author Kevin Kowalewski
 * 
 */
public class BacktestUtils {

	public static String getCurrentBacktestValueGroup() {
		return "--------> Best backtest results: \n" + 
			"Long entry: " + SignalControl.pointToSignalLongEntry + "\n" + 
			"Long exit: " + SignalControl.pointToSignalLongExit + "\n" + 
			"Short entry: " + SignalControl.pointToSignalShortEntry + "\n" +
			"Short exit: " + SignalControl.pointToSignalShortExit + "\n" +
			"Period Length: " + SignalControl.periodLength + "\n" + 
			"Period Average PPC: " + SignalControl.periodAverageForPPC + "\n" + 
			"Period Average DI: " + SignalControl.periodAverageForDI + "\n" + 
			"Period Average CCI: " + SignalControl.periodAverageForCCI + "\n" + 
			"Period Average MACD: " + SignalControl.periodAverageForMACD + "\n" + 
			"Period Average TRIX: " + SignalControl.periodAverageForTRIX + "\n" +
			"Total transactions: " + Account.instance.getTransactions() + "\n" + 
			"Bank account balance: " + Account.instance.getBankBalance() + "\n";
	}
	
	public static void printBestBacktestResults(ArrayList<String> listOfStringBestBacktestResults){
		Co.println("Best backtest results...");
		for (String string : listOfStringBestBacktestResults){
			Co.println(string);
		}
	}
}