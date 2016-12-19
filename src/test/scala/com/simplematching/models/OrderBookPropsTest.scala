package com.simplematching.models

import com.simplematching.processing.Generators
import org.scalacheck._

class OrderBookPropsTest extends Properties("OrderBook") {
  private val genOrder = Generators.genOrder(Gen.oneOf("C1", "C2")).map(_.copy(equity = "A"))

  private def runOrders(initial: OrderBook, orders: Iterable[Order]) = {
    orders.foldLeft(initial)(_.addOrderAndMatch(_)._1)
  }

  private val genConstructedBook = for {
    orders <- Gen.containerOf[Iterable, Order](genOrder)
  } yield runOrders(OrderBook.empty, orders)


  property("noBidAskIntersections") = Prop.forAll(genConstructedBook) { orderBook =>
    orderBook.bids.keySet.intersect(orderBook.asks.keySet).isEmpty
  }
}
