package example

import example.view.RecordList
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._
import org.scalajs.dom

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("NetstatApp")
object NetstatApp extends JSApp {

  val baseUrl = BaseUrl(dom.window.location.href.takeWhile(_ != '#'))

  val routerConfig: RouterConfig[RecordFilter] = RouterConfigDsl[RecordFilter].buildConfig { dsl =>
    import dsl._

    val netstatConnection = AppCircuit.connect(_.entries)

    /* how the application renders the list given a filter */
    def filterRoute(s: RecordFilter): Rule = staticRoute("#/" + s.link, s) ~> renderR(router => netstatConnection(p => RecordList(p, s, router)))

    val filterRoutes: Rule = RecordFilter.values.map(filterRoute).reduce(_ | _)

    /* build a final RouterConfig with a default page */
    filterRoutes.notFound(redirectToPage(RecordFilter.All)(Redirect.Replace))
  }

  /** The router is itself a React component, which at this point is not mounted (U-suffix) */
  val router: ReactComponentU[Unit, Resolution[RecordFilter], Any, TopNode] =
    Router(baseUrl, routerConfig.logToConsole)()

  @JSExport
  override def main(): Unit = {
    AppCircuit.dispatch(LoadModel)
    ReactDOM.render(router, dom.document.getElementsByClassName("todoapp")(0))
  }
}
