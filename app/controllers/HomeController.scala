package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import play.api.libs.json._
import model._
import play.api.libs.json
import play.api.libs.mailer.MailerClient
import play.modules.reactivemongo.ReactiveMongoApi

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */

class HomeController @Inject()(val reactiveMongoApi: ReactiveMongoApi, mailerClient: MailerClient) extends Controller with TransactionsHandler {

  def testEmail(too: String, from: String, message: String, amount: String) = Action { implicit request: Request[AnyContent] =>
    import email._

    val recipientEmail = new RecipientEmail(mailerClient)

    recipientEmail.send(too, from, message, amount)

    Ok("SENT EMAIL")
  }
}