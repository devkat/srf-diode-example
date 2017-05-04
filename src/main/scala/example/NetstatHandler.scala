package example

import cats.implicits._
import diode._
import io.circe.parser._
import io.circe.generic.auto._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

case object LoadNetstatModel extends Action

case class NewNetstatModel(entries: List[NetstatEntry]) extends Action

case class SelectRecordFilter(filter: NetstatRecordFilter) extends Action

case class DeleteRecord(entryId: Option[Long], recordId: Option[Long]) extends Action


class NetstatHandler[M](modelRW: ModelRW[M, List[NetstatEntry]]) extends ActionHandler(modelRW) {

  private def jsonUrl = "/target/scala-2.12/classes/netstat_output.json"

  def parseNetstatModel(s: String): List[NetstatEntry] = decode[List[NetstatEntry]](s).
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

    case LoadNetstatModel => effectOnly(Effect(Ajax.get(jsonUrl).map(r => NewNetstatModel(parseNetstatModel(r.responseText)))))

    case NewNetstatModel(entries) => updated(entries)

    case DeleteRecord(entryId, recordId) => updated(value.map {
      case entry if entryId.isDefined && entry.id === entryId =>
        val records = entry.data.out.filterNot(record => recordId.isDefined && recordId === record.id)
        entry.copy(data = entry.data.copy(out = records))
      case entry => entry
    })
  }

}
