package example.view

import diode.Action
import diode.data.Pot
import diode.react.ModelProxy
import example._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import japgolly.scalajs.react.vdom.prefix_<^._

object RecordList {

  case class Props(proxy: ModelProxy[Pot[List[Entry]]], currentFilter: RecordFilter, ctl: RouterCtl[RecordFilter])

  class Backend($ : BackendScope[Props, Unit]) {
    def mounted(props: Props) = Callback {}

    def render(p: Props) = {
      val proxy = p.proxy()
      val dispatch: Action => Callback = p.proxy.dispatchCB

      <.div(
        <.h1("Records"),
        proxy.fold {
          <.div(
            ^.className := "loading",
            ^.dangerouslySetInnerHtml("<svg width='150px' height='150px' xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 100 100\" preserveAspectRatio=\"xMidYMid\" class=\"uil-default\"><rect x=\"0\" y=\"0\" width=\"100\" height=\"100\" fill=\"none\" class=\"bk\"></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(0 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(30 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.08333333333333333s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(60 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.16666666666666666s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(90 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.25s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(120 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.3333333333333333s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(150 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.4166666666666667s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(180 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.5s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(210 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.5833333333333334s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(240 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.6666666666666666s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(270 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.75s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(300 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.8333333333333334s' repeatCount='indefinite'/></rect><rect  x='47.5' y='45' width='5' height='10' rx='5' ry='5' fill='#dddddd' transform='rotate(330 50 50) translate(0 -20)'>  <animate attributeName='opacity' from='1' to='0' dur='1s' begin='0.9166666666666666s' repeatCount='indefinite'/></rect></svg>")
          )
        } { entries =>
          val records = entries.flatMap(_.data.out)
          val filteredTodos = records filter p.currentFilter.accepts
          val tcpCount = records count RecordFilter.Tcp.accepts
          val udpCount = records.length - tcpCount
          <.div(
            records.nonEmpty ?= recordList(dispatch, entries, p.currentFilter, tcpCount),
            records.nonEmpty ?= footer(p, dispatch, p.currentFilter, tcpCount, udpCount)
          )
        }
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

  def apply(proxy: ModelProxy[Pot[List[Entry]]], currentFilter: RecordFilter, ctl: RouterCtl[RecordFilter]) =
    component(Props(proxy, currentFilter, ctl))
}
