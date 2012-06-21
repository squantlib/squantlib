package squantlib.parameter.yieldparameter

import scala.collection.immutable.TreeMap
import scala.collection.immutable.SortedMap
import org.jquantlib.time.{ Date => JDate }
import org.jquantlib.time.{ Period => JPeriod }
import org.jquantlib.time.TimeUnit
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator

/**
 * TimeVector with Linear interpolation and no extrapolation. (ie. y-value is constant after last date)
 * Date is internally described as number of days since value date, but can be accessed by Date or Period.
 * 
 * @constructor constructs linear curve from given points. extra flat points are added after final date to manage overshoot.
 * @param input points
 */
class LinearNoExtrapolation(var valuedate : JDate, inputvalues:SortedMap[JPeriod, Double]) extends YieldParameter with AbstractYieldParameter {
	require(inputvalues.size >= 2)
  
    val linearfunction = {
	    var inputpoints :TreeMap[Long, Double] = TreeMap.empty
	    for (d <- inputvalues.keySet) { inputpoints ++= Map(d.days(valuedate) -> inputvalues(d)) }
	    val keysarray = inputpoints.keySet.toArray
	    val valarray = keysarray.map((i:Long) => inputpoints(i))
	    new LinearInterpolator().interpolate(keysarray.map((i:Long)=>i.toDouble), valarray)    
	}
	
	val mindays = inputvalues.firstKey.days(valuedate)
	val maxdays = inputvalues.lastKey.days(valuedate)
	val maxdate = new JDate(valuedate.serialNumber() + maxdays)
	val maxperiod = new JPeriod(maxdays.toInt, TimeUnit.Days)

	def lowextrapolation(v : Long) = inputvalues.first._2
    def highextrapolation(v : Long) = inputvalues.last._2
    def interpolation(v : Long) = linearfunction.value(v.toDouble)
}