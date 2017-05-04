package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.ext.KeyCode

object NetstatRecordView {

  case class Props(record: NetstatRecord)

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): ReactElement = {
      <.tr(
        ^.className := "view",
        <.td(p.record.proto),
        <.td(p.record.foreign_addr),
        <.td(p.record.local_addr),
        <.td(p.record.user),
        <.td(p.record.pid_program)
      )
    }
  }

  val component = ReactComponentB[Props]("CNetstatRecord")
    .renderBackend[Backend]
    .build

  def apply(P: Props) =
    component.withKey(P.record.hashCode())(P)
}
