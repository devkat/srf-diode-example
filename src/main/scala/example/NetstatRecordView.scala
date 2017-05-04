package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.ext.KeyCode

object NetstatRecordView {

  case class Props(record: NetstatRecord)

  class Backend($ : BackendScope[Props, Unit]) {
    def render(p: Props): ReactElement = {
      <.li(
        <.div(
          ^.className := "view",
          <.span(p.record.proto),
          <.span(p.record.foreign_addr),
          <.span(p.record.local_addr),
          <.span(p.record.user),
          <.span(p.record.pid_program)
        )
      )
    }
  }

  val component = ReactComponentB[Props]("CNetstatRecord")
    .renderBackend[Backend]
    .build

  def apply(P: Props) =
    component.withKey(P.record.hashCode())(P)
}
