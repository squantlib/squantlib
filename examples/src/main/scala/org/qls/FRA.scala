package org.qls

/**
  * Created by ceb on 02/04/16.
  */
object FRA extends App {
/*
/*  This example shows how to set up a term structure and price a simple
    forward-rate agreement.
*/
// the only header you need to use QuantLib
#include <ql/quantlib.hpp>
#ifdef BOOST_MSVC
/* Uncomment the following lines to unmask floating-point
   exceptions. Warning: unpredictable results can arise...
   See http://www.wilmott.com/messageview.cfm?catid=10&threadid=9481
   Is there anyone with a definitive word about this?
*/
// #include <float.h>
// namespace { unsigned int u = _controlfp(_EM_INEXACT, _MCW_EM); }
#endif
#include <boost/timer.hpp>
#include <iostream>
#define LENGTH(a) (sizeof(a)/sizeof(a[0]))
using namespace std;
using namespace QuantLib;
#ifdef BOOST_MSVC
#  ifdef QL_ENABLE_THREAD_SAFE_OBSERVER_PATTERN
#    include <ql/auto_link.hpp>
#    define BOOST_LIB_NAME boost_system
#    include <boost/config/auto_link.hpp>
#    undef BOOST_LIB_NAME
#    define BOOST_LIB_NAME boost_thread
#    include <boost/config/auto_link.hpp>
#    undef BOOST_LIB_NAME
#  endif
#endif
#if defined(QL_ENABLE_SESSIONS)
namespace QuantLib {
    Integer sessionId() { return 0; }
}
#endif
int main(int, char* []) {
    try {
        boost::timer timer;
        std::cout << std::endl;
        /*********************
         ***  MARKET DATA  ***
         *********************/
        RelinkableHandle<YieldTermStructure> euriborTermStructure;
        boost::shared_ptr<IborIndex> euribor3m(
                                       new Euribor3M(euriborTermStructure));
        Date todaysDate = Date(23, May, 2006);
        Settings::instance().evaluationDate() = todaysDate;
        Calendar calendar = euribor3m->fixingCalendar();
        Integer fixingDays = euribor3m->fixingDays();
        Date settlementDate = calendar.advance(todaysDate, fixingDays, Days);
        std::cout << "Today: " << todaysDate.weekday()
                  << ", " << todaysDate << std::endl;
        std::cout << "Settlement date: " << settlementDate.weekday()
                  << ", " << settlementDate << std::endl;
        // 3 month term FRA quotes (index refers to monthsToStart)
        Rate threeMonthFraQuote[10];
        threeMonthFraQuote[1]=0.030;
        threeMonthFraQuote[2]=0.031;
        threeMonthFraQuote[3]=0.032;
        threeMonthFraQuote[6]=0.033;
        threeMonthFraQuote[9]=0.034;
        /********************
         ***    QUOTES    ***
         ********************/
        // SimpleQuote stores a value which can be manually changed;
        // other Quote subclasses could read the value from a database
        // or some kind of data feed.
        // FRAs
        boost::shared_ptr<SimpleQuote> fra1x4Rate(
                                      new SimpleQuote(threeMonthFraQuote[1]));
        boost::shared_ptr<SimpleQuote> fra2x5Rate(
                                      new SimpleQuote(threeMonthFraQuote[2]));
        boost::shared_ptr<SimpleQuote> fra3x6Rate(
                                      new SimpleQuote(threeMonthFraQuote[3]));
        boost::shared_ptr<SimpleQuote> fra6x9Rate(
                                      new SimpleQuote(threeMonthFraQuote[6]));
        boost::shared_ptr<SimpleQuote> fra9x12Rate(
                                      new SimpleQuote(threeMonthFraQuote[9]));
        RelinkableHandle<Quote> h1x4;  h1x4.linkTo(fra1x4Rate);
        RelinkableHandle<Quote> h2x5;  h2x5.linkTo(fra2x5Rate);
        RelinkableHandle<Quote> h3x6;  h3x6.linkTo(fra3x6Rate);
        RelinkableHandle<Quote> h6x9;  h6x9.linkTo(fra6x9Rate);
        RelinkableHandle<Quote> h9x12; h9x12.linkTo(fra9x12Rate);
        /*********************
         ***  RATE HELPERS ***
         *********************/
        // RateHelpers are built from the above quotes together with
        // other instrument dependant infos.  Quotes are passed in
        // relinkable handles which could be relinked to some other
        // data source later.
        DayCounter fraDayCounter = euribor3m->dayCounter();
        BusinessDayConvention convention = euribor3m->businessDayConvention();
        bool endOfMonth = euribor3m->endOfMonth();
        boost::shared_ptr<RateHelper> fra1x4(
                           new FraRateHelper(h1x4, 1, 4,
                                             fixingDays, calendar, convention,
                                             endOfMonth, fraDayCounter));
        boost::shared_ptr<RateHelper> fra2x5(
                           new FraRateHelper(h2x5, 2, 5,
                                             fixingDays, calendar, convention,
                                             endOfMonth, fraDayCounter));
        boost::shared_ptr<RateHelper> fra3x6(
                           new FraRateHelper(h3x6, 3, 6,
                                             fixingDays, calendar, convention,
                                             endOfMonth, fraDayCounter));
        boost::shared_ptr<RateHelper> fra6x9(
                           new FraRateHelper(h6x9, 6, 9,
                                             fixingDays, calendar, convention,
                                             endOfMonth, fraDayCounter));
        boost::shared_ptr<RateHelper> fra9x12(
                           new FraRateHelper(h9x12, 9, 12,
                                             fixingDays, calendar, convention,
                                             endOfMonth, fraDayCounter));
        /*********************
         **  CURVE BUILDING **
         *********************/
        // Any DayCounter would be fine.
        // ActualActual::ISDA ensures that 30 years is 30.0
        DayCounter termStructureDayCounter =
            ActualActual(ActualActual::ISDA);
        double tolerance = 1.0e-15;
        // A FRA curve
        std::vector<boost::shared_ptr<RateHelper> > fraInstruments;
        fraInstruments.push_back(fra1x4);
        fraInstruments.push_back(fra2x5);
        fraInstruments.push_back(fra3x6);
        fraInstruments.push_back(fra6x9);
        fraInstruments.push_back(fra9x12);
        boost::shared_ptr<YieldTermStructure> fraTermStructure(
                     new PiecewiseYieldCurve<Discount,LogLinear>(
                                         settlementDate, fraInstruments,
                                         termStructureDayCounter,
                                         tolerance));
        // Term structures used for pricing/discounting
        RelinkableHandle<YieldTermStructure> discountingTermStructure;
        discountingTermStructure.linkTo(fraTermStructure);
        /***********************
         ***  construct FRA's ***
         ***********************/
        Calendar fraCalendar = euribor3m->fixingCalendar();
        BusinessDayConvention fraBusinessDayConvention =
            euribor3m->businessDayConvention();
        Position::Type fraFwdType = Position::Long;
        Real fraNotional = 100.0;
        const Integer FraTermMonths = 3;
        Integer monthsToStart[] = { 1, 2, 3, 6, 9 };
        euriborTermStructure.linkTo(fraTermStructure);
        cout << endl;
        cout << "Test FRA construction, NPV calculation, and FRA purchase"
             << endl
             << endl;
        Size i;
        for (i=0; i<LENGTH(monthsToStart); i++) {
            Date fraValueDate = fraCalendar.advance(
                                       settlementDate,monthsToStart[i],Months,
                                       fraBusinessDayConvention);
            Date fraMaturityDate = fraCalendar.advance(
                                            fraValueDate,FraTermMonths,Months,
                                            fraBusinessDayConvention);
            Rate fraStrikeRate = threeMonthFraQuote[monthsToStart[i]];
            ForwardRateAgreement myFRA(fraValueDate, fraMaturityDate,
                                       fraFwdType,fraStrikeRate,
                                       fraNotional, euribor3m,
                                       discountingTermStructure);
            cout << "3m Term FRA, Months to Start: "
                 << monthsToStart[i]
                 << endl;
            cout << "strike FRA rate: "
                 << io::rate(fraStrikeRate)
                 << endl;
            cout << "FRA 3m forward rate: "
                 << myFRA.forwardRate()
                 << endl;
            cout << "FRA market quote: "
                 << io::rate(threeMonthFraQuote[monthsToStart[i]])
                 << endl;
            cout << "FRA spot value: "
                 << myFRA.spotValue()
                 << endl;
            cout << "FRA forward value: "
                 << myFRA.forwardValue()
                 << endl;
            cout << "FRA implied Yield: "
                 << myFRA.impliedYield(myFRA.spotValue(),
                                       myFRA.forwardValue(),
                                       settlementDate,
                                       Simple,
                                       fraDayCounter)
                 << endl;
            cout << "market Zero Rate: "
                 << discountingTermStructure->zeroRate(fraMaturityDate,
                                                       fraDayCounter,
                                                       Simple)
                 << endl;
            cout << "FRA NPV [should be zero]: "
                 << myFRA.NPV()
                 << endl
                 << endl;
        }
        cout << endl << endl;
        cout << "Now take a 100 basis-point upward shift in FRA quotes "
             << "and examine NPV"
             << endl
             << endl;
        const Real BpsShift = 0.01;
        threeMonthFraQuote[1]=0.030+BpsShift;
        threeMonthFraQuote[2]=0.031+BpsShift;
        threeMonthFraQuote[3]=0.032+BpsShift;
        threeMonthFraQuote[6]=0.033+BpsShift;
        threeMonthFraQuote[9]=0.034+BpsShift;
        fra1x4Rate->setValue(threeMonthFraQuote[1]);
        fra2x5Rate->setValue(threeMonthFraQuote[2]);
        fra3x6Rate->setValue(threeMonthFraQuote[3]);
        fra6x9Rate->setValue(threeMonthFraQuote[6]);
        fra9x12Rate->setValue(threeMonthFraQuote[9]);
        for (i=0; i<LENGTH(monthsToStart); i++) {
            Date fraValueDate = fraCalendar.advance(
                                       settlementDate,monthsToStart[i],Months,
                                       fraBusinessDayConvention);
            Date fraMaturityDate = fraCalendar.advance(
                                            fraValueDate,FraTermMonths,Months,
                                            fraBusinessDayConvention);
            Rate fraStrikeRate =
                threeMonthFraQuote[monthsToStart[i]] - BpsShift;
            ForwardRateAgreement myFRA(fraValueDate, fraMaturityDate,
                                       fraFwdType, fraStrikeRate,
                                       fraNotional, euribor3m,
                                       discountingTermStructure);
            cout << "3m Term FRA, 100 notional, Months to Start = "
                 << monthsToStart[i]
                 << endl;
            cout << "strike FRA rate: "
                 << io::rate(fraStrikeRate)
                 << endl;
            cout << "FRA 3m forward rate: "
                 << myFRA.forwardRate()
                 << endl;
            cout << "FRA market quote: "
                 << io::rate(threeMonthFraQuote[monthsToStart[i]])
                 << endl;
            cout << "FRA spot value: "
                 << myFRA.spotValue()
                 << endl;
            cout << "FRA forward value: "
                 << myFRA.forwardValue()
                 << endl;
            cout << "FRA implied Yield: "
                 << myFRA.impliedYield(myFRA.spotValue(),
                                       myFRA.forwardValue(),
                                       settlementDate,
                                       Simple,
                                       fraDayCounter)
                 << endl;
            cout << "market Zero Rate: "
                 << discountingTermStructure->zeroRate(fraMaturityDate,
                                                       fraDayCounter,
                                                       Simple)
                 << endl;
            cout << "FRA NPV [should be positive]: "
                 << myFRA.NPV()
                 << endl
                 << endl;
        }
        double seconds = timer.elapsed();
        Integer hours = int(seconds/3600);
        seconds -= hours * 3600;
        Integer minutes = int(seconds/60);
        seconds -= minutes * 60;
        cout << " \nRun completed in ";
        if (hours > 0)
            cout << hours << " h ";
        if (hours > 0 || minutes > 0)
            cout << minutes << " m ";
        cout << fixed << setprecision(0)
             << seconds << " s\n" << endl;
        return 0;
    } catch (exception& e) {
        cerr << e.what() << endl;
        return 1;
    } catch (...) {
        cerr << "unknown error" << endl;
        return 1;
    }
}
Generated by Doxygen 1.8.9.1
 */
}
