package com.simplematching.processing

import com.simplematching.models.{Client, _}
import org.scalacheck.Gen

import scala.collection.immutable.SortedMap

object Generators {
  val equities = Seq("A", "B", "C", "D")

  val genClient: Gen[Client] = for {
    name <- Gen.alphaNumStr
    balance <- Gen.posNum[Money]
    assets <- Gen.containerOfN[Seq, Size](equities.size, Gen.posNum[Size]).map(equities.zip(_).toMap)
  } yield Client(name, balance, assets)

  val genAccounts: Gen[ClientAccounts] = for {
    clients <- Gen.nonEmptyContainerOf[Seq, Client](Generators.genClient)
  } yield ClientAccounts(SortedMap(clients.map(c => c.name -> c): _*))

  def genOrder(genClient: Gen[String]): Gen[Order] = {
    for {
      client <- genClient
      action <- Gen.oneOf(Action.Buy, Action.Sell)
      equity <- Gen.oneOf(Generators.equities)
      price <- Gen.posNum[Price]
      size <- Gen.posNum[Size]
    } yield Order(client, action, equity, price, size)
  }
}
