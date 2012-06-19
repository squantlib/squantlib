package squantlib.test

import scala.collection.JavaConversions._
import scala.collection.immutable.TreeMap

import squantlib.math.timeseries.TSAnalysis
import squantlib.timeseries.HistoricalVol
import squantlib.timeseries.HistoricalCorrelation

import org.jquantlib.time.{ Date => JDate }
import org.jquantlib.time.TimeSeries
import org.jquantlib.time.Date
import org.jquantlib.time.Month


object HistoricalVolCorrelTest {

    val data = Array[(JDate, Double)] (
				(new JDate(26, Month.December, 2011), 77.98),
				(new JDate(27, Month.December, 2011), 77.88),
				(new JDate(28, Month.December, 2011), 77.94),
				(new JDate(29, Month.December, 2011), 77.64),
				(new JDate(30, Month.December, 2011), 76.91),
				(new JDate(2, Month.January, 2012), 76.90),
				(new JDate(3, Month.January, 2012), 76.74),
				(new JDate(4, Month.January, 2012), 76.72),
				(new JDate(5, Month.January, 2012), 77.12),
				(new JDate(6, Month.January, 2012), 76.97),
				(new JDate(9, Month.January, 2012), 76.86),
				(new JDate(10, Month.January, 2012), 76.85),
				(new JDate(11, Month.January, 2012), 76.85),
				(new JDate(12, Month.January, 2012), 76.76),
				(new JDate(13, Month.January, 2012), 76.97),
				(new JDate(16, Month.January, 2012), 76.78),
				(new JDate(17, Month.January, 2012), 76.83),
				(new JDate(18, Month.January, 2012), 76.82),
				(new JDate(19, Month.January, 2012), 77.11),
				(new JDate(20, Month.January, 2012), 77.01),
				(new JDate(23, Month.January, 2012), 77.02),
				(new JDate(24, Month.January, 2012), 77.67),
				(new JDate(25, Month.January, 2012), 77.78),
				(new JDate(26, Month.January, 2012), 77.45),
				(new JDate(27, Month.January, 2012), 76.70),
				(new JDate(30, Month.January, 2012), 76.35),
				(new JDate(31, Month.January, 2012), 76.27),
				(new JDate(1, Month.February, 2012), 76.20),
				(new JDate(2, Month.February, 2012), 76.22),
				(new JDate(3, Month.February, 2012), 76.60),
				(new JDate(6, Month.February, 2012), 76.55),
				(new JDate(7, Month.February, 2012), 76.76),
				(new JDate(8, Month.February, 2012), 77.04),
				(new JDate(9, Month.February, 2012), 77.67),
				(new JDate(10, Month.February, 2012), 77.61),
				(new JDate(13, Month.February, 2012), 77.57),
				(new JDate(14, Month.February, 2012), 78.44),
				(new JDate(15, Month.February, 2012), 78.43),
				(new JDate(16, Month.February, 2012), 78.94),
				(new JDate(17, Month.February, 2012), 79.55),
				(new JDate(20, Month.February, 2012), 79.63),
				(new JDate(21, Month.February, 2012), 79.74),
				(new JDate(22, Month.February, 2012), 80.29),
				(new JDate(23, Month.February, 2012), 80.00),
				(new JDate(24, Month.February, 2012), 81.20),
				(new JDate(27, Month.February, 2012), 80.61),
				(new JDate(28, Month.February, 2012), 80.46),
				(new JDate(29, Month.February, 2012), 81.15),
				(new JDate(1, Month.March, 2012), 81.12),
				(new JDate(2, Month.March, 2012), 81.47)
    )
    
    val data2 = Array[(JDate, Double)] (
				(new JDate(26, Month.December, 2011), 105.39),
				(new JDate(27, Month.December, 2011), 106.43),
				(new JDate(28, Month.December, 2011), 105.95),
				(new JDate(29, Month.December, 2011), 104.82),
				(new JDate(30, Month.December, 2011), 104.75),
				(new JDate(2, Month.January, 2012), 103.76),
				(new JDate(3, Month.January, 2012), 102.64),
				(new JDate(4, Month.January, 2012), 103.40),
				(new JDate(5, Month.January, 2012), 103.34),
				(new JDate(6, Month.January, 2012), 104.34),
				(new JDate(9, Month.January, 2012), 103.75),
				(new JDate(10, Month.January, 2012), 104.48),
				(new JDate(11, Month.January, 2012), 103.12),
				(new JDate(12, Month.January, 2012), 100.97),
				(new JDate(13, Month.January, 2012), 102.54),
				(new JDate(16, Month.January, 2012), 102.51),
				(new JDate(17, Month.January, 2012), 103.09),
				(new JDate(18, Month.January, 2012), 102.66),
				(new JDate(19, Month.January, 2012), 104.59),
				(new JDate(20, Month.January, 2012), 104.55),
				(new JDate(23, Month.January, 2012), 106.56),
				(new JDate(24, Month.January, 2012), 105.95),
				(new JDate(25, Month.January, 2012), 107.20),
				(new JDate(26, Month.January, 2012), 105.55),
				(new JDate(27, Month.January, 2012), 105.65),
				(new JDate(30, Month.January, 2012), 105.69),
				(new JDate(31, Month.January, 2012), 105.84),
				(new JDate(1, Month.February, 2012), 105.97),
				(new JDate(2, Month.February, 2012), 106.00),
				(new JDate(3, Month.February, 2012), 105.83),
				(new JDate(6, Month.February, 2012), 105.93),
				(new JDate(7, Month.February, 2012), 107.76),
				(new JDate(8, Month.February, 2012), 107.28),
				(new JDate(9, Month.February, 2012), 108.33),
				(new JDate(10, Month.February, 2012), 107.39),
				(new JDate(13, Month.February, 2012), 107.29),
				(new JDate(14, Month.February, 2012), 107.90),
				(new JDate(15, Month.February, 2012), 107.88),
				(new JDate(16, Month.February, 2012), 107.53),
				(new JDate(17, Month.February, 2012), 107.52),
				(new JDate(20, Month.February, 2012), 105.38),
				(new JDate(21, Month.February, 2012), 105.66),
				(new JDate(22, Month.February, 2012), 106.10),
				(new JDate(23, Month.February, 2012), 105.07),
				(new JDate(24, Month.February, 2012), 104.30),
				(new JDate(27, Month.February, 2012), 103.75),
				(new JDate(28, Month.February, 2012), 103.62),
				(new JDate(29, Month.February, 2012), 104.00),
				(new JDate(1, Month.March, 2012), 103.71),
				(new JDate(2, Month.March, 2012), 103.96))    
    
    val testarray = Set[Double] (
	        -0.0012832030504524,
			0.000770119406564252,
			-0.00385654160975365,
			-0.00944685123184717,
			-0.000130030557364077,
			-0.00208279169279209,
			-0.000260654243623444,
			0.0052002197271075,
			-0.00194691475606423
	)

    val c:java.lang.Class[java.lang.Double] = java.lang.Double.TYPE
    
    val datamap = data.map(d => (d._1 -> new java.lang.Double(d._2))).toMap
    val ts = new TimeSeries[java.lang.Double](c, datamap)

    val datamap2 = data2.map(d => (d._1 -> new java.lang.Double(d._2))).toMap
    val ts2 = new TimeSeries[java.lang.Double](c, datamap2)
  
	def main(args: Array[String]): Unit = {
	  
		val fxsorted = TreeMap(data :_*)
		val fxsorted2 = TreeMap(data2 :_*)
		val annualdays = 252
		
		println("array size " + fxsorted.size + " vs " + fxsorted2.size)
		
        println("Log")
        val logvalues = TSAnalysis.LogReturn(fxsorted)
        for (k <- logvalues.keySet) { println(k + " " + logvalues(k)) }
	  
        println("Array Variance")
        val variancearray = TSAnalysis.Variance(TSAnalysis.LogReturn(fxsorted), 10)
        for (k <- variancearray.keySet) { println(k + " " + variancearray(k)) }
        
        println("Array StdDev")
        val stddevarray = TSAnalysis.StdDev(TSAnalysis.LogReturn(fxsorted), 10)
        for (k <- stddevarray.keySet) { println(k + " " + stddevarray(k)) }
        
        println("Array Volatility")
        val volarray = TSAnalysis.HistoricalVol(fxsorted, 10, 252.0)
        for (k <- volarray.keySet) { println(k + " " + volarray(k)) }
        
        println("Array Covariance")
        val covararray = TSAnalysis.Covariance(fxsorted, fxsorted2, 10)
        for (k <- covararray.keySet) { println(k + " " + covararray(k)) }
        
        println("Array Correlation")
        val correlarray = TSAnalysis.Correlation(fxsorted, fxsorted2, 10)
        for (k <- correlarray.keySet) { println(k + " " + correlarray(k)) }

        println("TimeSeries Volatility")
        val volcalculator = new HistoricalVol(10, 252.0)
        val vols = volcalculator.calculate(ts)
        for (k <- vols.keySet) { println(k + " " + vols.get(k)) }
        
        println("TimeSeries Correlation")
        val correlcalculator = new HistoricalCorrelation(10)
        val correls = correlcalculator.calculate(ts, ts2)
        for (k <- correls.keySet) { println(k + " " + correls.get(k)) }
	}

}