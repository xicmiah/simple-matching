package com.simplematching.models

import com.simplematching.models.Action.{Buy, Sell}
import utest._

object OrderBookTest extends TestSuite {
  val tests = this {
    def genOrder(action: Action, price: Price, size: Size, client: String = "C") =
      Order(client, action, "A", price, size)

    val empty = OrderBook.empty
    "adds buy orders to bids" - {
      val order = genOrder(Buy, 10, 1)
      val (updated, trades) = empty.addOrderAndMatch(order)
      assert(updated.bids.values.flatten.toSeq.contains(order))
      assert(trades.isEmpty)
    }

    "adds sell orders to asks" - {
      val order = genOrder(Sell, 10, 1)
      val (updated, trades) = empty.addOrderAndMatch(order)
      assert(updated.asks.values.flatten.toSeq.contains(order))
      assert(trades.isEmpty)
    }

    "returns matches" - {
      val buy = genOrder(Buy, 10, 1, "C1")
      val sell = genOrder(Sell, 10, 1, "C2")
      val (book, _) = empty.addOrderAndMatch(sell)

      val (result, trades) = book.addOrderAndMatch(buy)
      assert(trades == Seq(Trade("C1", "C2", "A", 10, 1)))
      assert(result == empty)
    }

    "matches orders in FIFO priority" - {
      val twoOrders = empty
        .addOrderAndMatch(genOrder(Buy, 1, 1, "C1"))._1
        .addOrderAndMatch(genOrder(Buy, 1, 1, "C2"))._1

      val (_, Seq(trade)) = twoOrders.addOrderAndMatch(genOrder(Sell, 1, 1, "C3"))
      assert(trade.buyer == "C1")
    }
  }
}
