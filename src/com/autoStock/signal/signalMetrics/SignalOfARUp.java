/**
 * 
 */
package com.autoStock.signal.signalMetrics;

import com.autoStock.algorithm.AlgorithmBase;
import com.autoStock.signal.SignalBase;
import com.autoStock.signal.SignalDefinitions.SignalMetricType;
import com.autoStock.signal.SignalDefinitions.SignalParameters;

/**
 * @author Kevin Kowalewski
 *
 */
public class SignalOfARUp extends SignalBase {
	public SignalOfARUp(SignalParameters signalParameters, AlgorithmBase algorithmBase){
		super(SignalMetricType.metric_ar_up, signalParameters, algorithmBase);
	}
}