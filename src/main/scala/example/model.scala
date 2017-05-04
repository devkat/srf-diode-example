package example

import diode.data.Pot

case class RootModel(entries: Pot[List[Entry]])

case class Entry(id: Option[Long], agent: String, action: String, sender: String, statuscode: Int, statusmsg: String, data: Data)

case class Data(out: List[Record])

case class Record(id: Option[Long], proto: String, local_addr: String, foreign_addr: String, user: String, pid_program: String)

sealed abstract class RecordFilter(val link: String, val label: String, val proto: Option[String]) {
  def accepts(r: Record): Boolean = proto.isEmpty || proto.contains(r.proto)
}

object RecordFilter {

  object All extends RecordFilter("", "All", None)

  object Tcp extends RecordFilter("tcp", "TCP", Some("tcp"))

  object Udp extends RecordFilter("udp", "UDP", Some("udp"))

  val values = List[RecordFilter](All, Tcp, Udp)
}
