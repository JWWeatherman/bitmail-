package modules
import actors._
import bitcoin.StartupWalletLoader
import com.google.inject.{AbstractModule, Provides}
import email.{Email, EmailWithMailer, EmailWithSendGrid, SendGridConfiguration}
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[BitcoinClientActor]("BitcoinClientActor")
    bindActor[NotificationSendingActor]("NotificationSendingActor")
    bindActor[EmailCommunicationsActor](ActorNames.EmailCommunications)
    bindActor[SendgridActor](ActorNames.Mail)
    bind(classOf[StartupWalletLoader]).asEagerSingleton()
    bind(classOf[Email]).to(classOf[EmailWithSendGrid])
  }

  @Provides
  def provideSendGrid(): SendGridConfiguration = {
    val key = System.getProperty("SENDGRIDSECRET")
    SendGridConfiguration(key)
  }
}
