package com.simplematching.processing

import com.simplematching.models._
import org.scalacheck._

import scala.collection.immutable.SortedMap

object TradeProcessingTest extends Properties("Clients") {
  private def equities = ClientGenerator.equities

  val genAccounts: Gen[ClientAccounts] = for {
    clients <- Gen.nonEmptyContainerOf[Seq, Client](ClientGenerator.genClient)
  } yield ClientAccounts(SortedMap(clients.map(c => c.name -> c):_*))

  private def genTrade(buyer: String, seller: String): Gen[Trade] = for {
    equity <- Gen.oneOf(equities)
    price <- Gen.posNum[Price]
    quantity <- Gen.posNum[Size]
  } yield Trade(buyer, seller, equity, price, quantity)

  val genTradeUpdate: Gen[(ClientAccounts, ClientAccounts)] = for {
    accounts <- genAccounts
    clientNames = accounts.clients.keySet.toSeq
    buyer <- Gen.oneOf(clientNames)
    seller <- Gen.oneOf(clientNames)
    trade <- genTrade(buyer, seller)
  } yield accounts -> accounts.applyTrade(trade)


  property("balanceZeroSum") = Prop.forAll(genTradeUpdate) {
    case (old, updated) =>
      old.clients.values.map(_.balance).sum == updated.clients.values.map(_.balance).sum
  }

  property("assetsZeroSum") = Prop.forAll(genTradeUpdate) {
    case (old, updated) =>
      def totalAssets(clients: ClientAccounts): Map[Equity, Size] = {
        equities.map(equity => equity -> old.clients.values.flatMap(c => c.assets.get(equity)).sum).toMap
      }
      totalAssets(old) == totalAssets(updated)
  }
}
