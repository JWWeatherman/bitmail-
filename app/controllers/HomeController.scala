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
class HomeController @Inject()() extends Controller {

  def triggerCompile = Action {
    Ok(Json.obj("compile_message" -> "RELOAD COMPILE"))
  }
}
