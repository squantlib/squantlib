package org.qls

import net.squantlib.numerictypes._
import org.jquantlib.time.Date
import org.jquantlib.exercise._

/**
  * Created by ceb on 31/03/16.
  */
object EquityOption extends App {

  // set a timer
  val start = System.nanoTime

  // set up dates
  val calendar = new org.jquantlib.time.calendars.Target
  val todaysDate = new Date(15, 5, 1998)
  val settlementDate = new Date(7, 5, 1998)
  val settings = new org.jquantlib.Settings
  settings.setEvaluationDate(todaysDate)

  // our options
  //import net.squantlib.numerictypes.Option._
  val optionType = net.squantlib.numerictypes.Option.Put
  val underlying: Real = 36
  val strike: Real = 40
  val dividendYield: Spread = 0.00
  val riskFreeRate: Rate = 0.06
  val volatility: Volatility = 0.20
  val maturity = new Date(17, 5, 1999)
  val dayCounter = new org.jquantlib.daycounters.Actual365Fixed

  println(s"Option type = $optionType")
  println(s"Maturity = $maturity")
  println(f"Underlying price = $underlying%2.0f")
  println(f"Strike = $strike%2.0f")
  println(f"Risk-free interest rate = ${100 * riskFreeRate}%2.1f%%")
  println(f"Dividend yield = ${100 * dividendYield}%2.1f%%")
  println(f"Volatility = ${100 * volatility}%2.1f%%")

  // write column headings
  // ...

  //  for (Integer i=1; i<=4; i++)
  //  exerciseDates.push_back(settlementDate + 3*i*Months);
  val exerciseDates = Array(settlementDate, settlementDate)
  val europeanExercise = new EuropeanExercise(maturity)
  val bermudanExercise = new BermudanExercise(exerciseDates)
  val americanExercise = new AmericanExercise(settlementDate, maturity)

  val underlyingH = new org.jquantlib.quotes.SimpleQuote(underlying)
  /*
  import org.jquantlib.quotes.SimpleQuote
          Handle<Quote> underlyingH(
            boost::shared_ptr<Quote>(new SimpleQuote(underlying)));
        // bootstrap the yield/dividend/vol curves
        Handle<YieldTermStructure> flatTermStructure(
            boost::shared_ptr<YieldTermStructure>(
                new FlatForward(settlementDate, riskFreeRate, dayCounter)));
        Handle<YieldTermStructure> flatDividendTS(
            boost::shared_ptr<YieldTermStructure>(
                new FlatForward(settlementDate, dividendYield, dayCounter)));
        Handle<BlackVolTermStructure> flatVolTS(
            boost::shared_ptr<BlackVolTermStructure>(
                new BlackConstantVol(settlementDate, calendar, volatility,
                                     dayCounter)));
        boost::shared_ptr<StrikedTypePayoff> payoff(
                                        new PlainVanillaPayoff(type, strike));
        boost::shared_ptr<BlackScholesMertonProcess> bsmProcess(
                 new BlackScholesMertonProcess(underlyingH, flatDividendTS,
                                               flatTermStructure, flatVolTS));
        // options
        VanillaOption europeanOption(payoff, europeanExercise);
        VanillaOption bermudanOption(payoff, bermudanExercise);
        VanillaOption americanOption(payoff, americanExercise);
        // Analytic formulas:
        // Black-Scholes for European
        method = "Black-Scholes";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                                     new AnalyticEuropeanEngine(bsmProcess)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << "N/A"
                  << std::endl;
        // semi-analytic Heston for European
        method = "Heston semi-analytic";
        boost::shared_ptr<HestonProcess> hestonProcess(
            new HestonProcess(flatTermStructure, flatDividendTS,
                              underlyingH, volatility*volatility,
                              1.0, volatility*volatility, 0.001, 0.0));
        boost::shared_ptr<HestonModel> hestonModel(
                                              new HestonModel(hestonProcess));
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                                     new AnalyticHestonEngine(hestonModel)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << "N/A"
                  << std::endl;
        // semi-analytic Bates for European
        method = "Bates semi-analytic";
        boost::shared_ptr<BatesProcess> batesProcess(
            new BatesProcess(flatTermStructure, flatDividendTS,
                             underlyingH, volatility*volatility,
                             1.0, volatility*volatility, 0.001, 0.0,
                             1e-14, 1e-14, 1e-14));
        boost::shared_ptr<BatesModel> batesModel(new BatesModel(batesProcess));
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                                                new BatesEngine(batesModel)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << "N/A"
                  << std::endl;
        // Barone-Adesi and Whaley approximation for American
        method = "Barone-Adesi/Whaley";
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                       new BaroneAdesiWhaleyApproximationEngine(bsmProcess)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << "N/A"
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Bjerksund and Stensland approximation for American
        method = "Bjerksund/Stensland";
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BjerksundStenslandApproximationEngine(bsmProcess)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << "N/A"
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Integral
        method = "Integral";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                                             new IntegralEngine(bsmProcess)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << "N/A"
                  << std::endl;
        // Finite differences
        Size timeSteps = 801;
        method = "Finite differences";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                 new FDEuropeanEngine<CrankNicolson>(bsmProcess,
                                                     timeSteps,timeSteps-1)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                 new FDBermudanEngine<CrankNicolson>(bsmProcess,
                                                     timeSteps,timeSteps-1)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                 new FDAmericanEngine<CrankNicolson>(bsmProcess,
                                                     timeSteps,timeSteps-1)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Binomial method: Jarrow-Rudd
        method = "Binomial Jarrow-Rudd";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<JarrowRudd>(bsmProcess,timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<JarrowRudd>(bsmProcess,timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<JarrowRudd>(bsmProcess,timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        method = "Binomial Cox-Ross-Rubinstein";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BinomialVanillaEngine<CoxRossRubinstein>(bsmProcess,
                                                                   timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BinomialVanillaEngine<CoxRossRubinstein>(bsmProcess,
                                                                   timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BinomialVanillaEngine<CoxRossRubinstein>(bsmProcess,
                                                                   timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Binomial method: Additive equiprobabilities
        method = "Additive equiprobabilities";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<AdditiveEQPBinomialTree>(bsmProcess,
                                                                   timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<AdditiveEQPBinomialTree>(bsmProcess,
                                                                   timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<AdditiveEQPBinomialTree>(bsmProcess,
                                                                   timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Binomial method: Binomial Trigeorgis
        method = "Binomial Trigeorgis";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<Trigeorgis>(bsmProcess,timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<Trigeorgis>(bsmProcess,timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                new BinomialVanillaEngine<Trigeorgis>(bsmProcess,timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Binomial method: Binomial Tian
        method = "Binomial Tian";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BinomialVanillaEngine<Tian>(bsmProcess,timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BinomialVanillaEngine<Tian>(bsmProcess,timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                      new BinomialVanillaEngine<Tian>(bsmProcess,timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Binomial method: Binomial Leisen-Reimer
        method = "Binomial Leisen-Reimer";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
              new BinomialVanillaEngine<LeisenReimer>(bsmProcess,timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
              new BinomialVanillaEngine<LeisenReimer>(bsmProcess,timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
              new BinomialVanillaEngine<LeisenReimer>(bsmProcess,timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Binomial method: Binomial Joshi
        method = "Binomial Joshi";
        europeanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                    new BinomialVanillaEngine<Joshi4>(bsmProcess,timeSteps)));
        bermudanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                    new BinomialVanillaEngine<Joshi4>(bsmProcess,timeSteps)));
        americanOption.setPricingEngine(boost::shared_ptr<PricingEngine>(
                    new BinomialVanillaEngine<Joshi4>(bsmProcess,timeSteps)));
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << bermudanOption.NPV()
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
        // Monte Carlo Method: MC (crude)
        timeSteps = 1;
        method = "MC (crude)";
        Size mcSeed = 42;
        boost::shared_ptr<PricingEngine> mcengine1;
        mcengine1 = MakeMCEuropeanEngine<PseudoRandom>(bsmProcess)
            .withSteps(timeSteps)
            .withAbsoluteTolerance(0.02)
            .withSeed(mcSeed);
        europeanOption.setPricingEngine(mcengine1);
        // Real errorEstimate = europeanOption.errorEstimate();
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << "N/A"
                  << std::endl;
        // Monte Carlo Method: QMC (Sobol)
        method = "QMC (Sobol)";
        Size nSamples = 32768;  // 2^15
        boost::shared_ptr<PricingEngine> mcengine2;
        mcengine2 = MakeMCEuropeanEngine<LowDiscrepancy>(bsmProcess)
            .withSteps(timeSteps)
            .withSamples(nSamples);
        europeanOption.setPricingEngine(mcengine2);
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << europeanOption.NPV()
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << "N/A"
                  << std::endl;
        // Monte Carlo Method: MC (Longstaff Schwartz)
        method = "MC (Longstaff Schwartz)";
        boost::shared_ptr<PricingEngine> mcengine3;
        mcengine3 = MakeMCAmericanEngine<PseudoRandom>(bsmProcess)
            .withSteps(100)
            .withAntitheticVariate()
            .withCalibrationSamples(4096)
            .withAbsoluteTolerance(0.02)
            .withSeed(mcSeed);
        americanOption.setPricingEngine(mcengine3);
        std::cout << std::setw(widths[0]) << std::left << method
                  << std::fixed
                  << std::setw(widths[1]) << std::left << "N/A"
                  << std::setw(widths[2]) << std::left << "N/A"
                  << std::setw(widths[3]) << std::left << americanOption.NPV()
                  << std::endl;
                     */
  // End test
  println("\nRun completed in " + (System.nanoTime - start) / 1e9 + " seconds")
}
