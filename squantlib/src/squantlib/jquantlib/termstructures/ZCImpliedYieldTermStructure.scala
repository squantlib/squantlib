package squantlib.jquantlib.termstructures

import squantlib.model.rates.{DiscountableCurve, DiscountCurve}
import squantlib.model.yieldparameter.YieldParameter
import squantlib.setting.initializer.Calendars
import org.jquantlib.time.{ Date => qlDate, Calendar}
import org.jquantlib.termstructures.AbstractYieldTermStructure
import org.jquantlib.daycounters.DayCounter


/**
 * Yield termstructure defined from curve & discount termstructure.
 * @param reference date is the date of which discount factor is computed
 * 		  value date is implied from the value date of the discount curve, and is the date of which the market parameter is quoted.
 * 		  reference date is either equal or later than discount value date
 * 
 */
class ZCImpliedYieldTermStructure(val discount:DiscountCurve, val cdr:Calendar, val settlement:Int, val referencedate:qlDate)
extends AbstractYieldTermStructure(referencedate)  {
  
	val maxDate = discount.zc.maxdate.add(90)
	
	val valuedate = discount.valuedate
	
	override def dayCounter = discount.daycount
	
	override def calendar = cdr
	
	override def settlementDays = settlement
	
	def /*@DiscountFactor*/ discountImpl(/*@Time*/ t:Double):Double = {
		/* t is relative to the current reference date*/
		val ref = referenceDate
		val /*@Time*/ originaltime = t + dayCounter.yearFraction(valuedate, ref)
		discount.zc(originaltime, dayCounter) / discount.zc(ref)
	}
	 
	def this(d:DiscountCurve, cdr:Calendar) = this(d, cdr, 0, d.valuedate)
	def this(d:DiscountCurve) = this(d, Calendars(d.currency.code).get, 0, d.valuedate)

}
