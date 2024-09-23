//> using dep com.lihaoyi::cask::0.9.4

package scala_cask

import java.util.concurrent.ScheduledFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import scala.util.Try

object WebSocketServer extends cask.MainRoutes:

  val scheduler = Executors.newScheduledThreadPool(1)

  override def port: Int    = 9191
  override def host: String = "0.0.0.0"

  @cask.websocket("/myapp")
  def myapp(): cask.WebsocketResult =
    var sc: Option[ScheduledFuture[?]] = None

    cask.WsHandler { channel =>

      sc = Some(
        scheduler.scheduleAtFixedRate(
          () =>
            Try(
              channel.send(
                cask.Ws.Text("hello at " + System.currentTimeMillis())
              )
            ),
          1,
          1,
          TimeUnit.SECONDS
        )
      )

      cask.WsActor {
        case cask.Ws.Text("") => channel.send(cask.Ws.Close())
        case cask.Ws.Text(data) =>
          channel.send(cask.Ws.Text(data))
      }
    }
  end myapp
  initialize()
end WebSocketServer
