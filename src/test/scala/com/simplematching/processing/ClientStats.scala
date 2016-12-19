package com.simplematching.processing

import com.simplematching.models._

object ClientStats {
  def totalBalance(accounts: ClientAccounts): Money = {
    accounts.clients.values.map(_.balance).sum
  }

  def totalAssets(accounts: ClientAccounts): Map[Equity, Size] = {
    ClientGenerator.equities.map { equity =>
      equity -> accounts.clients.values.flatMap(c => c.assets.get(equity)).sum
    }.toMap
  }
}
