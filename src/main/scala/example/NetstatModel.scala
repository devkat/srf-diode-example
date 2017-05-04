package example

case class NetstatModel(entries: List[NetstatEntry])

case class NetstatEntry(agent: String, action: String, sender: String, statuscode: Int, statusmsg: String, data: NetstatData)

case class NetstatData(out: List[NetstatRecord])

case class NetstatRecord(proto: String, local_addr: String, foreign_addr: String, user: String, pid_program: String)

sealed abstract class NetstatRecordFilter(val link: String, val label: String, val proto: Option[String]) {
  def accepts(r: NetstatRecord): Boolean = proto.isEmpty || proto.contains(r.proto)
}

object NetstatRecordFilter {

  object All extends NetstatRecordFilter("", "All", None)

  object Tcp extends NetstatRecordFilter("tcp", "TCP", Some("tcp"))

  object Udp extends NetstatRecordFilter("udp", "UDP", Some("udp"))

  val values = List[NetstatRecordFilter](All, Tcp, Udp)
}
