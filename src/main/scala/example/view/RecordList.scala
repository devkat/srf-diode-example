package example.view

import diode.Action
import diode.react.ModelProxy
import example._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._

object RecordList {

  case class Props(proxy: ModelProxy[List[Entry]], currentFilter: RecordFilter, ctl: RouterCtl[RecordFilter])

  class Backend($ : BackendScope[Props, Unit]) {
    def mounted(props: Props) = Callback {}

    def render(p: Props) = {
      val proxy = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB
      val records = proxy.flatMap(_.data.out)
      val filteredTodos = records filter p.currentFilter.accepts
      val tcpCount = records count RecordFilter.Tcp.accepts
      val udpCount = records.length - tcpCount

      <.div(
        <.h1("Records"),
        <.header(
          ^.className := "header"
        ),
        records.nonEmpty ?= recordList(dispatch, proxy, p.currentFilter, tcpCount),
        records.nonEmpty ?= footer(p, dispatch, p.currentFilter, tcpCount, udpCount)
      )
    }

    def recordList(dispatch: Action => Callback, entries: List[Entry], filter: RecordFilter, tcpCount: Int) =
      <.section(
        ^.className := "main",
        <.table(
          ^.className := "record-list",
          <.thead(
            <.tr(
              <.th("Protocol"),
              <.th("Foreign addr"),
              <.th("Local addr"),
              <.th("User"),
              <.th("PID program")
            )
          ),
          entries.map(entry =>
            <.tbody(
              <.tr(
                <.th(^.colSpan := 6)(
                  s"agent: ${entry.agent}, action: ${entry.action}, sender: ${entry.sender}, statuscode: ${entry.statuscode}, statusmsg: ${entry.statusmsg}"
                )
              ),
              entry.data.out.filter(filter.accepts).map(record =>
                RecordView(RecordView.Props(
                  record = record,
                  onDelete = dispatch(DeleteRecord(entry.id, record.id))
                )))
            )
          )
        )
      )

    def footer(p: Props, dispatch: Action => Callback, currentFilter: RecordFilter, tcpCount: Int, udpCount: Int): ReactElement =
      Footer(
        Footer.Props(
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

  def apply(proxy: ModelProxy[List[Entry]], currentFilter: RecordFilter, ctl: RouterCtl[RecordFilter]) =
    component(Props(proxy, currentFilter, ctl))
}
