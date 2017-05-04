package example

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

object NetstatFooter {

  case class Props(
      filterLink: NetstatRecordFilter => ReactTag,
      onSelectFilter: NetstatRecordFilter => Callback,
      currentFilter: NetstatRecordFilter,
      tcpCount: Int,
      udpCount: Int
  )

  class Backend($ : BackendScope[Props, Unit]) {
    def filterLink(p: Props)(s: NetstatRecordFilter) =
      <.li(p.filterLink(s)((p.currentFilter == s) ?= (^.className := "selected"), s.label))

    def render(p: Props) =
      <.footer(
        ^.className := "footer",
        <.span(
          ^.className := "todo-count",
          <.strong(p.tcpCount), " TCP ", s" ${if (p.tcpCount == 1) "record" else "records"}",
          ", ",
          <.strong(p.udpCount), " UDP ", s" ${if (p.udpCount == 1) "record" else "records"}"
        ),
        <.ul(
          ^.className := "filters",
          NetstatRecordFilter.values.map(filterLink(p)(_))
        )
      )
  }

  private val component = ReactComponentB[Props]("Footer").stateless
    .renderBackend[Backend]
    .build

  def apply(p: Props) = component(p)
}
