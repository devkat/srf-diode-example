package example

import diode._
import io.circe._
import io.circe.parser._
import io.circe.generic.auto._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

case object LoadNetstatModel extends Action

case class NewNetstatModel(entries: List[NetstatEntry]) extends Action

case class SelectRecordFilter(filter: NetstatRecordFilter) extends Action


class NetstatHandler[M](modelRW: ModelRW[M, List[NetstatEntry]]) extends ActionHandler(modelRW) {

  private def jsonUrl = "/target/scala-2.12/classes/netstat_output.json"

  def parseNetstatModel(s: String) = decode[List[NetstatEntry]](s) match {
    case Left(e) =>
      println(e)
      Nil
    case Right(entries) => entries
  }

    //.getOrElse({println("parsing failed"); sys.error("")})

  override protected def handle: PartialFunction[Any, ActionResult[M]] = {

    case LoadNetstatModel => effectOnly(Effect(Ajax.get(jsonUrl).map(r => NewNetstatModel(parseNetstatModel(r.responseText)))))

    case NewNetstatModel(entries) => updated(entries)
  }

}
