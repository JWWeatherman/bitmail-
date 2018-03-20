package controllers

import javax.inject.Inject

import model.WalletStorage
import play.api.mvc.Controller
import play.modules.reactivemongo.ReactiveMongoApi

class RecipientController @Inject()(
    val reactiveMongoApi: ReactiveMongoApi,
    val walletStorage: WalletStorage
) extends Controller {}
