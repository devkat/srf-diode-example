package example

import diode.Action
import diode.react.ModelProxy
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._
import org.scalajs.dom.ext.KeyCode

object NetstatRecordList {

  case class Props(proxy: ModelProxy[List[NetstatEntry]], currentFilter: NetstatRecordFilter, ctl: RouterCtl[NetstatRecordFilter])

  class Backend($ : BackendScope[Props, Unit]) {
    def mounted(props: Props) = Callback {}

    def render(p: Props) = {
      val proxy = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB
      val records = proxy.flatMap(_.data.out)
      val filteredTodos = records filter p.currentFilter.accepts
      val tcpCount = records count NetstatRecordFilter.Tcp.accepts
      val udpCount = records.length - tcpCount

      <.div(
        <.h1("Records"),
        <.header(
          ^.className := "header"
        ),
        records.nonEmpty ?= recordList(dispatch, filteredTodos, tcpCount),
        records.nonEmpty ?= footer(p, dispatch, p.currentFilter, tcpCount, udpCount)
      )
    }

    def recordList(dispatch: Action => Callback, records: List[NetstatRecord], tcpCount: Int) =
      <.section(
        ^.className := "main",
        <.ul(
          ^.className := "todo-list",
          records.map(
            record =>
              NetstatRecordView(NetstatRecordView.Props(record = record)))
        )
      )

    def footer(p: Props, dispatch: Action => Callback, currentFilter: NetstatRecordFilter, tcpCount: Int, udpCount: Int): ReactElement =
      NetstatFooter(
        NetstatFooter.Props(
          filterLink = p.ctl.link,
          currentFilter = currentFilter,
          onSelectFilter = f => dispatch(SelectRecordFilter(f)),
          tcpCount = tcpCount,
          udpCount = udpCount
        ))
  }

  private val component = ReactComponentB[Props]("NetstatRecordList")
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(proxy: ModelProxy[List[NetstatEntry]], currentFilter: NetstatRecordFilter, ctl: RouterCtl[NetstatRecordFilter]) =
    component(Props(proxy, currentFilter, ctl))
}
