package squantlib.test

import scala.collection.immutable.TreeMap
import scala.collection.immutable.SortedMap
import scala.collection.Iterable

import squantlib.parameter._
import squantlib.ratecurve._

import org.jquantlib.time.{ Date => JDate }
import org.jquantlib.time.{ Period => JPeriod }
import org.jquantlib.time.TimeUnit
import org.jquantlib.time.Frequency
import org.jquantlib.indexes.ibor._
import org.jquantlib.indexes._
import org.jquantlib.daycounters._
import org.jquantlib.currencies.America._

object DiscountCurveTest {
	
	def main(args:Array[String]) : Unit = {
	  
	  /**
	   * Setting up view functions
	   */
	  val rounding = (x: Double, decimals:Int) => (x * math.pow(10, decimals)).round / math.pow(10, decimals)
	  val percent = (x:Double, decimals:Int) => (rounding(x*100, decimals)) + "%"
	  val vdescribe = (v : TimeVector) => { "value " + v.valuedate.shortDate.toString + ":" + percent(v.value(v.valuedate), 2) + " to " + v.maxdate.shortDate.toString + ":" + percent(v.value(v.maxdays), 2) }
	  val cashdescribe = (r : CashCurve) => r.currency.code + " " + r.floatindex.dayCounter
	  val swapdescribe = (r : SwapCurve) => r.currency.code + " " + r.fixperiod.tenor + "m " + r.fixdaycount.name + " vs " + r.floatindex.tenor.length + "m " + r.floatindex.dayCounter.name
	  val basisdescribe = (r : BasisSwapCurve) => r.currency.code + " " + r.floatindex.tenor.length + "m " + r.floatindex.dayCounter.name + " vs " + r.pivotfloatindex.currency.code + " " + r.pivotfloatindex.tenor.length + "m " + r.pivotfloatindex.dayCounter.name
	  val basis36describe = (r : TenorBasisSwapCurve) => r.currency.code + " " + r.shortindex.tenor.length + "m " + r.shortindex.dayCounter.name + " vs " + " " + r.longindex.tenor.length + "m " + r.longindex.dayCounter.name
	  def valuelist(xlist:Seq[String]):String =  xlist.length match { case 0 => ""; case 1 => xlist(0); case 2 => xlist(0) + ", " + xlist(1); case _ => xlist.head + ", " + valuelist(xlist.tail)}
	  
	  /**
	   * Value date and a "random" period (we use flat curve)
	   */
	  val vd = new JDate(5, 6, 2012)
	  val period6m = new JPeriod(6, TimeUnit.Months)
	  val period30y = new JPeriod(30, TimeUnit.Years)

	  
	  /**
	   * Result display parameters. Max maturity = testperiod * testcase months
	   */
	  val testperiod = 6 // every X months
	  val testcase = 30*2 // number of outputs 
	  var inputset = for (i <- 0 to (testcase * testperiod) if i % testperiod == 0) yield new JPeriod(i, TimeUnit.Months)
	  
	  /**
	   * test spread for each currency
	   */
	  val spreads = {
	  	  val spread1 = new FlatVector(vd, Map(period6m -> 0.00))
		  val spread2 = new FlatVector(vd, Map(period6m -> 0.02))
		  val spread3 = new FlatVector(vd, Map(period6m -> -0.01))
	  	  Seq(spread1, spread2, spread3)
	  }
	   
	  
	  /**
	   * JPY curve definition
	   */
	  
	  val JPY_curvemodel = {
	    
	    val JPY_cash = {
			  val JPY_cashinput = Map(period6m -> 0.01)
			  val JPY_cash_curve = new FlatVector(vd, JPY_cashinput)
			  val JPY_cash_floatindex = new JPYLibor(new JPeriod(6, TimeUnit.Months))
			  new CashCurve(JPY_cash_curve, JPY_cash_floatindex)
		  }
		  
		  val JPY_swap = {
			  val JPY_swapinput = Map(period30y -> 0.01)
			  val JPY_swap_curve = new FlatVector(vd, JPY_swapinput)
			  val JPY_swap_floatindex = new JPYLibor(new JPeriod(6, TimeUnit.Months))
			  val JPY_swap_fixdaycount = new Actual365Fixed
			  val JPY_swap_fixperiod = Frequency.Semiannual
			  new SwapCurve(JPY_swap_curve, JPY_swap_floatindex, JPY_swap_fixdaycount, JPY_swap_fixperiod)
		  }
		  
		  val JPY_basis = {
			  val JPY_basisinput = Map(period30y -> -0.005)
			  val JPY_basis_curve = new FlatVector(vd, JPY_basisinput)
			  val JPY_basis_floatindex = new JPYLibor(new JPeriod(3, TimeUnit.Months))
		 	  new BasisSwapCurve(JPY_basis_curve, JPY_basis_floatindex)
		  }
		  
		  val JPY_basis36 = {
			  val JPY_basis36input = Map(period30y -> 0.002)
			  val JPY_basis36_curve = new FlatVector(vd, JPY_basis36input)
			  val JPY_basis36_sindex = new JPYLibor(new JPeriod(3, TimeUnit.Months))
			  val JPY_basis36_lindex = new JPYLibor(new JPeriod(6, TimeUnit.Months))
		 	  new TenorBasisSwapCurve(JPY_basis36_curve, JPY_basis36_sindex, JPY_basis36_lindex)
		  }
		  
		  new LiborDiscountCurve(JPY_cash, JPY_swap, JPY_basis, JPY_basis36, vd)
	  }

	  println("** JPY Curve **")
	  println("Cash: " + cashdescribe(JPY_curvemodel.cash) + " " + vdescribe(JPY_curvemodel.cash.rate))
	  println("Swap: "  + swapdescribe(JPY_curvemodel.swap) + " " + vdescribe(JPY_curvemodel.swap.rate))
	  println("BSccy: " + basisdescribe(JPY_curvemodel.basis) + " " + vdescribe(JPY_curvemodel.basis.rate))
	  println("BS3m6m: " + basis36describe(JPY_curvemodel.tenorbasis) + " " + vdescribe(JPY_curvemodel.tenorbasis.rate))
	  
	  val JPY_ZC = spreads.map(s => JPY_curvemodel.getZC(s))
	  println("** Discount Curve **")
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(JPY_ZC.map(z => rounding(z.zc.value(d), 4).toString))) })
	  
	  /**
	   * USD curve definition
	   */
	  
	  val USD_curvemodel = {
	    
		  val USD_cash = {
			  val USD_cashinput = Map(period6m -> 0.05)
			  val USD_cash_curve = new FlatVector(vd, USD_cashinput)
			  val USD_cash_floatindex = new USDLibor(new JPeriod(6, TimeUnit.Months))
		 	  new CashCurve(USD_cash_curve, USD_cash_floatindex)
		  }
	
		  val USD_swap = {
			  val USD_swapinput = Map(period30y -> 0.05)
			  val USD_swap_curve = new FlatVector(vd, USD_swapinput)
			  val USD_swap_floatindex = new USDLibor(new JPeriod(3, TimeUnit.Months))
			  val USD_swap_fixdaycount = new Actual360
			  val USD_swap_fixperiod = Frequency.Annual
			  new SwapCurve(USD_swap_curve, USD_swap_floatindex, USD_swap_fixdaycount, USD_swap_fixperiod)
		  }
		  
		  val USD_basis = {
			  val USD_basisinput = Map(period30y -> 0.00)
			  val USD_basis_curve = new FlatVector(vd, USD_basisinput)
			  val USD_basis_floatindex = new USDLibor(new JPeriod(3, TimeUnit.Months))
		 	  new BasisSwapCurve(USD_basis_curve, USD_basis_floatindex)
		  }
		  
		  val USD_basis36 = {
			  val USD_basis36input = Map(period30y -> 0.00)
			  val USD_basis36_curve = new FlatVector(vd, USD_basis36input)
			  val USD_basis36_sindex = new USDLibor(new JPeriod(3, TimeUnit.Months))
			  val USD_basis36_lindex = new USDLibor(new JPeriod(6, TimeUnit.Months))
		 	  new TenorBasisSwapCurve(USD_basis36_curve, USD_basis36_sindex, USD_basis36_lindex)
		  }
		  
		  new LiborDiscountCurve(USD_cash, USD_swap, USD_basis, USD_basis36, vd)
	  }
	  	  
	  println("**USD**")
	  println("Cash: " + cashdescribe(USD_curvemodel.cash) + " " + vdescribe(USD_curvemodel.cash.rate))
	  println("Swap: "  + swapdescribe(USD_curvemodel.swap) + " " + vdescribe(USD_curvemodel.swap.rate))
	  println("BSccy: " + basisdescribe(USD_curvemodel.basis) + " " + vdescribe(USD_curvemodel.basis.rate))
	  println("BS3m6m: " + basis36describe(USD_curvemodel.tenorbasis) + " " + vdescribe(USD_curvemodel.tenorbasis.rate))

	  val USD_ZC = spreads.map(s => USD_curvemodel.getZC(s))
	  println("** Discount Curve **")
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(USD_ZC.map(z => rounding(z.zc.value(d), 4).toString))) })
	  
	  /**
	   * EUR curve definition
	   */
	  
	  val EUR_curvemodel = {
	    
		  val EUR_cash = {
			  val EUR_cashinput = Map(period6m -> 0.03)
			  val EUR_cash_curve = new FlatVector(vd, EUR_cashinput)
			  val EUR_cash_floatindex = new Euribor(new JPeriod(6, TimeUnit.Months))
		 	  new CashCurve(EUR_cash_curve, EUR_cash_floatindex)
		  }
	
		  val EUR_swap = {
			  val EUR_swapinput = Map(period30y -> 0.03)
			  val EUR_swap_curve = new FlatVector(vd, EUR_swapinput)
			  val EUR_swap_floatindex = new Euribor(new JPeriod(6, TimeUnit.Months))
			  val EUR_swap_fixdaycount = new Thirty360
			  val EUR_swap_fixperiod = Frequency.Annual
			  new SwapCurve(EUR_swap_curve, EUR_swap_floatindex, EUR_swap_fixdaycount, EUR_swap_fixperiod)
		  }
		  
		  val EUR_basis = {
			  val EUR_basisinput = Map(period30y -> -0.005)
			  val EUR_basis_curve = new FlatVector(vd, EUR_basisinput)
			  val EUR_basis_floatindex = new Euribor(new JPeriod(3, TimeUnit.Months))
		 	  new BasisSwapCurve(EUR_basis_curve, EUR_basis_floatindex)
		  }
		  
		  val EUR_basis36 = {
			  val EUR_basis36input = Map(period30y -> 0.001)
			  val EUR_basis36_curve = new FlatVector(vd, EUR_basis36input)
			  val EUR_basis36_sindex = new Euribor(new JPeriod(3, TimeUnit.Months))
			  val EUR_basis36_lindex = new Euribor(new JPeriod(6, TimeUnit.Months))
		 	  new TenorBasisSwapCurve(EUR_basis36_curve, EUR_basis36_sindex, EUR_basis36_lindex)
		  }
		  
		  new LiborDiscountCurve(EUR_cash, EUR_swap, EUR_basis, EUR_basis36, vd)
	  }

	  	  
	  println("**EUR**")
	  println("Cash: " + cashdescribe(EUR_curvemodel.cash) + " " + vdescribe(EUR_curvemodel.cash.rate))
	  println("Swap: "  + swapdescribe(EUR_curvemodel.swap) + " " + vdescribe(EUR_curvemodel.swap.rate))
	  println("BSccy: " + basisdescribe(EUR_curvemodel.basis) + " " + vdescribe(EUR_curvemodel.basis.rate))
	  println("BS3m6m: " + basis36describe(EUR_curvemodel.tenorbasis) + " " + vdescribe(EUR_curvemodel.tenorbasis.rate))
	  
	  val EUR_ZC = spreads.map(s => EUR_curvemodel.getZC(s))
	  println("** Discount Curve **")
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(EUR_ZC.map(z => rounding(z.zc.value(d), 4).toString))) })


	  /**
	   * BRL curve definition
	   */
	  
	  val BRL_curvemodel = {
	    
		  var BRL_fx = 1.58
		  
		  val BRL_points = {
			  var points = TreeMap.empty[JPeriod, Double]
			  points ++= Map(new JPeriod(6, TimeUnit.Months) -> 301d)
			  points ++= Map(new JPeriod(9, TimeUnit.Months) -> 994d)
			  points ++= Map(new JPeriod(12, TimeUnit.Months) -> 1354d)
			  points ++= Map(new JPeriod(12*2, TimeUnit.Months) -> 2489d)
			  points ++= Map(new JPeriod(12*3, TimeUnit.Months) -> 4243d)
			  points ++= Map(new JPeriod(12*5, TimeUnit.Months) -> 6554d)
			  points ++= Map(new JPeriod(12*7, TimeUnit.Months) -> 9117d)
			  points ++= Map(new JPeriod(12*10, TimeUnit.Months) -> 13317d)
			  points
		  }
		  
		  val BRL_pointscurve = new SplineNoExtrapolation(vd, BRL_points, 1)
		  val BRL_multiplier = 10000
		  val BRL_currency = new BRLCurrency
	 	  val BRL_pivotcurrency = new USDCurrency
		  val BRL_swappt = new SwapPointCurve(BRL_pointscurve, BRL_multiplier, BRL_currency, BRL_pivotcurrency)
		  
		  new FXDiscountCurve(BRL_swappt, BRL_fx, vd)
	  }
	  
	  println("**BRL**")
	  println("fx: " + BRL_curvemodel.fx)
	  println("points: "  + vdescribe(BRL_curvemodel.swappoint.points))
		  
	  	  
	  /**
	   * Cross currency discounting - curve discounted by pivotcurve
	   */
	  
	  println("** JPY discounted by USD **")
	  val JPY_ccyZC = USD_ZC.map(s => JPY_curvemodel.getZC(USD_curvemodel, s))
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(JPY_ccyZC.map(z => rounding(z.zc.value(d), 4).toString))) })

	  /**
	   * Cross pivot curve discounted by non-pivot
	   */
	  
	  println("** USD discounted by EUR **")
	  val USD_ccyZC = EUR_ZC.map(s => USD_curvemodel.getZC(EUR_curvemodel, s))
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(USD_ccyZC.map(z => rounding(z.zc.value(d), 4).toString))) })
	  
	  /**
	   * Non-pivot discounted by non-pivot (through pivot)
	   */
	  println("** JPY discounted by EUR **")
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  val JPY_EURccyZC = USD_ccyZC.map(s => JPY_curvemodel.getZC(USD_curvemodel, s))
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(JPY_EURccyZC.map(z => rounding(z.zc.value(d), 4).toString))) })
	  
	  /**
	   * FX discounting by pivotcurve
	   */
	  
	  println("** BRL discounted by USD **") 
	  val BRL_ZC = USD_ZC.map(s => BRL_curvemodel.getZC(USD_curvemodel, s))
	  println("[ZC1, ZC2, ZC3, spread1, spread2, spread3]")
	  inputset.foreach( (d:JPeriod) => { println(d.toString() + ", " + valuelist(BRL_ZC.map(z => rounding(z.zc.value(d), 4).toString))) })
	  
    }
}

