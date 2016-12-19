package com.simplematching.models

import com.simplematching.models.Action._
import utest._

object TradeTest extends TestSuite {
  def tests = this {
    val buy = Order("C1", Buy, "A", 1, 1)
    val sell = buy.copy(client = "C2", action = Sell)

    "fromMatchingOrders ignores argument order" - {
      assert(Trade.fromMatchingOrders(buy, sell) == Trade.fromMatchingOrders(sell, buy))
    }

    "fromMatchingOrders forbids same side" - {
      intercept[Exception] {
        Trade.fromMatchingOrders(buy, buy)
      }

      intercept[Exception] {
        Trade.fromMatchingOrders(sell, sell)
      }
    }
  }
}
