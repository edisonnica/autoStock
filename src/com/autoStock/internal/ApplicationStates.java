/**
 * 
 */
package com.autoStock.internal;

import com.autoStock.database.DatabaseCore;
import com.autoStock.exchange.ExchangeController;
import com.autoStock.internal.Global.Mode;
import com.autoStock.r.RJavaController;

/**
 * @author Kevin Kowalewski
 *
 */
public class ApplicationStates {
	private static DatabaseCore databaseCore;
	private static ExchangeController exchangeController;
	
	public static void startup(Mode mode){
		Global.setMode(mode);
//		ConsoleScanner.start(); //TODO: Messes with JRI
		
		databaseCore = new DatabaseCore();
		databaseCore.init();
		
		RJavaController.getInstance(); //Invokes startup
		
		if (mode != Mode.client_skip_tws){
			exchangeController = new ExchangeController();
			exchangeController.init();
		}
		
		if (mode == Mode.client){
			
		}
		
		if (mode == Mode.server){
			
		}
	}
	
	public static void shutdown(){
		System.exit(0);
	}
}
