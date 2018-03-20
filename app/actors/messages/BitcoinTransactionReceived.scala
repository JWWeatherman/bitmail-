package actors.messages

import forms.CreateWalletForm
import org.bitcoinj.core.Coin

case class BitcoinTransactionReceived(transData: CreateWalletForm.Data,
                                      publicKeyAddress: String,
                                      transactionId: String,
                                      previousValue: Coin,
                                      newValue: Coin)
