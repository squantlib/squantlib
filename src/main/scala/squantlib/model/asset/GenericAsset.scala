package squantlib.model.asset

import org.jquantlib.time.{Date => qlDate}

case class GenericAsset(
  override val assetID:String,
  override val id:String,
  override val latestPrice:Option[Double],
  override val expectedYield:Option[Double],
  override val expectedCoupon:Option[Double],
  override val getDbForwardPrice:Map[qlDate, Double],
  override val getPriceHistory:Map[qlDate, Double]
  ) extends StaticAsset
  
