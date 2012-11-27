package squantlib.montecarlo.payoff

import DisplayUtils._
import JsonUtils._

/**
 * Interprets general formula as the sum of weighted variables + constant, with cap and floor.
 * Any symbols other than +, -, *, /, > and < are considered as an independent variable.
 * Brackets are not supported.
 */
case class GeneralPayoff(val formula:Map[Set[String], Double], val floor:Option[Double], val cap:Option[Double]) extends Payoff {
	
	override val variables:Set[String] = formula.keySet.flatten
	 
	def leverage(index:String):Double = leverage(Set(index))
	def leverage(index:Set[String]):Double = formula.getOrElse(index, 0.0)
	
	val constant:Double = formula.getOrElse(Set.empty, 0.0)
	
	override def price(fixing:Double) (implicit d:DummyImplicit) = 
	  if (variables.size == 1) price(Map(variables.head -> fixing))
	  else None
	
	override def price(fixings:Map[String, Double]):Option[Double] = {
	  if (!variables.forall(x => fixings.contains(x))) return None
	  
	  var rate = formula.map{
      	case (vs, c) if vs.isEmpty => c
      	case (vs, c) => vs.toList.map(x => fixings(x)).product * c}.sum
    
      if (floor.isDefined) rate = rate.max(floor.get)
      if (cap.isDefined) rate = rate.min(cap.get)
	
      Some(rate)
	}
	 
	override def price = 
	  if (variables.isEmpty) Some(constant)
	  else None
	
	override def toString:String = formula.map{
	  case (vs, c) if (vs.isEmpty) => "(const) " + c
	  case (vs, c) => "[ " + vs.reduce((x, y) => x + " x " + y) + " ] x " + c
	}.reduce((x, y) => x + " + " + y)
	
	def remodelize:Payoff = 
	  variables.size match {
	    case 0 => FixedPayoff(constant)
	    case 1 => {
	      val variable = variables.head
	      Linear1dPayoff(variable, Some(leverage(variable)), Some(constant), floor, cap)
	    }
	    case _ => this
	  }
	
}


object GeneralPayoff {
  
	def apply(formula:String):GeneralPayoff = {
	  val (parsedformula, floor, cap) = FormulaParser.parse(formula)
	  GeneralPayoff(parsedformula, floor, cap)
	}

}
