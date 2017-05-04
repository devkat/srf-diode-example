package example

import cats.implicits._
import diode._
import diode.data.Pot
import diode.Implicits.runAfterImpl
import io.circe.parser._
import io.circe.generic.auto._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case object LoadModel extends Action

case class NewModel(entries: List[Entry]) extends Action

case class SelectRecordFilter(filter: RecordFilter) extends Action

case class DeleteRecord(entryId: Option[Long], recordId: Option[Long]) extends Action


class NetstatHandler[M](modelRW: ModelRW[M, Pot[List[Entry]]]) extends ActionHandler(modelRW) {

  private def jsonUrl = "/target/scala-2.12/classes/netstat_output.json"

  def parseNetstatModel(s: String): List[Entry] = decode[List[Entry]](s).
    getOrElse(Nil).
    zipWithIndex.
    map { case (entry, ei) =>
      val records = entry.data.out
      entry.copy(
        id = Some(ei),
        data = entry.data.copy(out = records.zipWithIndex.map { case (record, ri) => record.copy(id = Some(ri)) }))
    }

    //.getOrElse({println("parsing failed"); sys.error("")})

  override protected def handle: PartialFunction[Any, ActionResult[M]] = {

    case LoadModel => updated(value.pending(), Effect(Ajax.get(jsonUrl).map(r => NewModel(parseNetstatModel(r.responseText)))).after(2.seconds))

    case NewModel(entries) => updated(value.ready(entries))

    case DeleteRecord(entryId, recordId) => updated(value.map(_.map {
      case entry if entryId.isDefined && entry.id === entryId =>
        val records = entry.data.out.filterNot(record => recordId.isDefined && recordId === record.id)
        entry.copy(data = entry.data.copy(out = records))
      case entry => entry
    }))
  }

}
