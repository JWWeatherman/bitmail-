package bitcoin

import akka.actor.ActorRef
import com.google.inject.Inject
import com.google.inject.name.Named
import model.models.SnailTransaction
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.Cursor
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext

class StartupWalletLoader @Inject()(
  mongoApi : ReactiveMongoApi,
  @Named("BitcoinClientActor") bitcoinClient : ActorRef)(implicit ec: ExecutionContext)
{
  def loadAllWallets = {
    for {
      database <- mongoApi.database
      transactions = database.collection[JSONCollection]("transactions")
      snails <- transactions.find(Json.obj())(transactions.pack.writer(a => a)).cursor[SnailTransaction]().collect(-1, Cursor.ContOnError[List[SnailTransaction]]((v, e) => {}))
    } yield {
      for {
        snail <- snails
      } yield {
        bitcoinClient ! snail
      }
    }
  }

  loadAllWallets
}