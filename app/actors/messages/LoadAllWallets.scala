package actors.messages

import model.models.SnailWallet

case class LoadAllWallets(wallets: List[SnailWallet])
